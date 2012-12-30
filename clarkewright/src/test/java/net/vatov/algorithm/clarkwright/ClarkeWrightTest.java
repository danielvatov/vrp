package net.vatov.algorithm.clarkwright;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import net.vatov.algorithm.AlgorithmException;
import net.vatov.algorithm.clarkewright.ClarkeWright;
import net.vatov.ampl.solver.io.UserIO;
import net.vatov.math.utils.SavingRow;
import net.vatov.math.utils.VehicleRoute;
import net.vatov.math.utils.VrpClient;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

//@RunWith(PowerMockRunner.class)
//@PrepareForTest({SavingRow.class})
public class ClarkeWrightTest {

    private static class Params {
        RealMatrix coordinates;
        int vehicleCapacity;
        RealVector demands;
        Object expected;
    }

    // TODO check loop avoidance
    /*
     * Currently no check is done whether route excludes the depot. The
     * algorithm should be protected from this but this should be proved with
     * suitable tests.
     */
    private List<Params> data() {
        Params in1 = new Params();
        in1.coordinates = MatrixUtils.createRealMatrix(5, 2);
        in1.coordinates.setRow(0, new double[] { 10, 6 });
        in1.coordinates.setRow(1, new double[] { 12, 7 });
        in1.coordinates.setRow(2, new double[] { 12, 5 });
        in1.coordinates.setRow(3, new double[] { 0, 0 });
        in1.coordinates.setRow(4, new double[] { 0, 12 });
        in1.vehicleCapacity = 24;
        in1.demands = MatrixUtils.createRealVector(new double[] { 0, 12, 12, 10, 10 });
        VrpClient c1 = new VrpClient(1, 12d), c2 = new VrpClient(2, 12d), c3 = new VrpClient(3, 10d), c4 = new VrpClient(4, 10d);
        SavingRow[] expected = new SavingRow[] { new SavingRow(0.005528, c1, c3), new SavingRow(0.005528, c2, c4),
                new SavingRow(0.005528, c3, c1), new SavingRow(0.005528, c4, c2), new SavingRow(0.897972, c1, c4),
                new SavingRow(0.897972, c2, c3), new SavingRow(0.897972, c3, c2), new SavingRow(0.897972, c4, c1),
                new SavingRow(2.472136, c1, c2), new SavingRow(2.472136, c2, c1), new SavingRow(11.323808, c3, c4),
                new SavingRow(11.323808, c4, c3) };
        Arrays.sort((Object[])expected);
        in1.expected = new SavingRow[expected.length];
        for (int i = 0 ; i<expected.length; ++i) {
            ((SavingRow[])in1.expected)[expected.length -1 - i] = expected[i];
        }
        return Arrays.asList(new Params[] { in1 });
    }

    @Test
    public final void testSolve() {
        Params params = data().get(0);
        ClarkeWright cw = new ClarkeWright(params.coordinates, params.vehicleCapacity, params.demands);
        cw.solve(null, getDummyIO());
        List<VehicleRoute> routes = Whitebox.<List<VehicleRoute>> getInternalState(cw, "routes");
        assertEquals("Incorrect number of routes", 2, routes.size());
        for (VehicleRoute r : routes) {
            assertEquals("Incorrect number of clients in route " + r, 2, r.clientNum().intValue());
            VrpClient c1 = r.peekFirstClient();
            VrpClient c2 = r.peekLastClient();
            if (1 == c1.getClientId() && 2 == c2.getClientId()) {
                assertEquals("Route distance incorrect " + r, 6.47213595499958, r.getDistance().doubleValue(),0.001);
                assertEquals("Route demand incorrect " + r, 24, r.getDemand().intValue());
            } else if (3 == c1.getClientId() && 4 == c2.getClientId()) {
                assertEquals("Route distance incorrect " + r, 35.3238075793812, r.getDistance().doubleValue(),0.001);
                assertEquals("Route demand incorrect " + r, 20, r.getDemand().intValue());
            } else {
                fail("Incorrect clients in route " + r);
            }
        }
    }

    private UserIO getDummyIO() {
        return new UserIO() {
            
            public void refreshData(Object data) {                
            }
            
            public void pause(String msg) {
            }
            
            public Boolean getYesNo(Boolean defaultValue, String question) {
                return null;
            }
            
            public Integer getInt(Integer defaultValue, String question) {
                return null;
            }
            
            public Integer getChoice(List<String> options, Integer defaultOption, String question) {
                return null;
            }
        };
    }

    @Test
    public final void testInitialState() {
        Params params = data().get(0);
        ClarkeWright cw = new ClarkeWright(params.coordinates, params.vehicleCapacity, params.demands);
        List<VehicleRoute> routes = Whitebox.<List<VehicleRoute>> getInternalState(cw, "routes");
        for (VehicleRoute r : routes) {
            assertEquals("Wrong number of clients in initial route " + r, 1, r.clientNum().intValue());
            assertEquals("Wrong demand for client " + r.peekFirstClient(),
                    params.demands.getEntry(r.peekFirstClient().getClientId()), r.getDemand(), 0.0);
        }
    }

    @Test
    public final void testComputeSavings() throws Exception {
        Params params = data().get(0);
        SavingRow[] expectedSavings = (SavingRow[]) params.expected;
        ClarkeWright cw = new ClarkeWright(params.coordinates, params.vehicleCapacity, params.demands);
        SavingRow[] res = Whitebox.<SavingRow[]> invokeMethod(cw, "computeSavings", new Object[] {});

        int clientsNum = params.coordinates.getRowDimension();
        assertEquals("Wrong size of savings array", ((int) Math.pow(clientsNum - 1, 2)) - (clientsNum - 1), res.length);
        assertSavingArrayEquals("Wrong savings result", expectedSavings, res);
    }

    @Test(expected = AlgorithmException.class)
    public final void testGetRouteIfNotInternal() throws Exception {
        Params params = data().get(0);
        ClarkeWright cw = new ClarkeWright(params.coordinates, params.vehicleCapacity, params.demands);
        VehicleRoute route = Whitebox.<VehicleRoute> invokeMethod(cw, "getRouteIfNotInternal", new Object[] { new VrpClient(3,0d) });
        assertNotNull("Route not found", route);

        route = Whitebox.<VehicleRoute> invokeMethod(cw, "getRouteIfNotInternal", new Object[] { new VrpClient(30, 0d) });
    }

    private void assertSavingArrayEquals(String string, SavingRow[] expectedSavings, SavingRow[] res) {
        String fromMsg = string + " (form)";
        String toMsg = string + " (to)";
        String savingMsg = string + "(saving)";
        String len = string + " (len)";
        assertEquals(len, expectedSavings.length, res.length);
        for (int i = 0; i < expectedSavings.length; ++i) {
            assertEquals(savingMsg, expectedSavings[i].getSaving(), res[i].getSaving(), 0.000001);
            assertEquals(fromMsg, expectedSavings[i].getFrom(), res[i].getFrom());
            assertEquals(toMsg, expectedSavings[i].getTo(), res[i].getTo());
        }
    }
}
