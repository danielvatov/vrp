package net.vatov.math.utils;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class VehicleRouteTest {

    private static VehicleRoute R;
    private static VehicleRoute R1;
    private static VehicleRoute R2;
    
    @BeforeClass
    public final static void createRoute(){
        R = new VehicleRoute();
        R.addClientLast(new VrpClient(1, 1d));
        R.addClientLast(new VrpClient(2, 2d));
        R.addClientLast(new VrpClient(3, 3d));
        R.addClientLast(new VrpClient(4, 4d));
        R.addClientLast(new VrpClient(5, 5d));
        R.addClientLast(new VrpClient(6, 6d));
        R.setDistance(10D);
        R1 = new VehicleRoute();
        R1.addClientLast(new VrpClient(1, 1d));
        R1.setDistance(10D);
        R2 = new VehicleRoute();
        R2.addClientLast(new VrpClient(1, 1d));
        R2.addClientLast(new VrpClient(2, 2d));
        R2.setDistance(10D);
    }
    
    @Test
    public final void testContainsEdge() {
        VrpEdge e = new VrpEdge(new VrpClient(1, 1d), new VrpClient(2, 2d), 1);
        assertTrue("Edge should be found in the route",R.containsEdge(e));
    }
    
    @Test
    public final void testContainsEdgeOneNode() {
        VrpEdge e = new VrpEdge(new VrpDepot(), new VrpClient(1, 2d), 1);
        assertTrue("Edge should be found in the route",R1.containsEdge(e));
    }
    
    @Test
    public final void testContainsEdgeTwoNodes() {
        VrpEdge e = new VrpEdge(new VrpClient(1, 1d), new VrpClient(2, 2d), 1);
        assertTrue("Edge should be found in the route",R2.containsEdge(e));
    }

    @Test
    public final void testDoesNotContainsEdgeTwoNodes() {
        VrpEdge e = new VrpEdge(new VrpClient(3, 1d), new VrpClient(2, 2d), 1);
        assertFalse("Edge should NOT be found in the route",R2.containsEdge(e));
    }
    
    @Test
    public final void testContainsEdgeDepot() {
        VrpEdge start = new VrpEdge(new VrpDepot(), new VrpClient(1, 1d), 1);
        VrpEdge end = new VrpEdge(new VrpClient(6, 6d), new VrpDepot(), 1);
        assertTrue("Edge should be found in the route",R.containsEdge(start));
        assertTrue("Edge should be found in the route",R.containsEdge(end));
    }

    @Test
    public final void testDoesNotContainsEdge() {
        VrpEdge e = new VrpEdge(new VrpClient(1, 1d), new VrpClient(7, 2d), 1);
        assertFalse("Edge should NOT be found in the route",R.containsEdge(e));
    }
}
