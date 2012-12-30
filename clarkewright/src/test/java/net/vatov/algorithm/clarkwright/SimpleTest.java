package net.vatov.algorithm.clarkwright;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.math.array.DoubleArray.increment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;

import net.vatov.algorithm.clarkewright.ClarkeWright;
import net.vatov.math.plot.GuiIO;
import net.vatov.math.plot.PlotVrp;
import net.vatov.math.utils.VrpClient;

import org.apache.commons.collections15.FactoryUtils;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.math.plot.Plot3DPanel;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.io.PajekNetReader;
import edu.uci.ics.jung.visualization.VisualizationViewer;

public class SimpleTest {

    public static void main(String[] args) throws Exception {
        cw();
    }

    public static void cw () {
        RealMatrix coordinates = MatrixUtils.createRealMatrix(5, 2);
        coordinates.setRow(0, new double[] { 10, 6 });
        coordinates.setRow(1, new double[] { 12, 7 });
        coordinates.setRow(2, new double[] { 12, 5 });
        coordinates.setRow(3, new double[] { 0, 0 });
        coordinates.setRow(4, new double[] { 0, 12 });
        ClarkeWright clarkeWright = new ClarkeWright(coordinates, 24, MatrixUtils.createRealVector(new double[] { 0, 12, 12, 10, 10 }));
        List<VrpClient> clients = new ArrayList<VrpClient>(clarkeWright.getClients().values());
        Collections.sort(clients);
        clients.add(0, clarkeWright.getDepot());
        PlotVrp plotVrp = new PlotVrp(clarkeWright.getEuclideanCoordinatesXY(), clarkeWright.getDistances(), clients);
        plotVrp.plotGraph();
        plotVrp.refreshRoutes(clarkeWright.getRoutes());
        GuiIO guiIO = new GuiIO(plotVrp);
        clarkeWright.solve(null, guiIO);
    }

    public static void jung2() throws IOException {
        JFrame jf = new JFrame();
        Graph g = getGraph();
        VisualizationViewer vv = new VisualizationViewer(new FRLayout(g));
        jf.getContentPane().add(vv);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.pack();
        jf.setVisible(true);
    }

    public static Graph getGraph() throws IOException {
        PajekNetReader pnr = new PajekNetReader(FactoryUtils.instantiateFactory(Object.class));
        Graph g = new UndirectedSparseGraph();

        pnr.load("src/test/resources/simple.net", g);
        return g;
    }

    public static void jung() {
        // Graph<V, E> where V is the type of the vertices
        // and E is the type of the edges
        Graph<Integer, String> g = new SparseMultigraph<Integer, String>();
        // Add some vertices. From above we defined these to be type Integer.
        g.addVertex((Integer) 1);
        g.addVertex((Integer) 2);
        g.addVertex((Integer) 3);
        // Add some edges. From above we defined these to be of type String
        // Note that the default is for undirected edges.
        g.addEdge("Edge-A", 1, 2); // Note that Java 1.5 auto-boxes primitives
        g.addEdge("Edge-B", 2, 3);
        // Let's see what we have. Note the nice output from the
        // SparseMultigraph<V,E> toString() method
        System.out.println("The graph g = " + g.toString());
        // Note that we can use the same nodes and edges in two different
        // graphs.
        Graph<Integer, String> g2 = new SparseMultigraph<Integer, String>();
        g2.addVertex((Integer) 1);
        g2.addVertex((Integer) 2);
        g2.addVertex((Integer) 3);
        g2.addEdge("Edge-A", 1, 3);
        g2.addEdge("Edge-B", 2, 3, EdgeType.DIRECTED);
        g2.addEdge("Edge-C", 3, 2, EdgeType.DIRECTED);
        g2.addEdge("Edge-P", 2, 3); // A parallel edge
        System.out.println("The graph g2 = " + g2.toString());
    }

    public static void plot3d() {
        // define your data
        double[] x = increment(0.0, 0.1, 1.0); // x = 0.0:0.1:1.0
        double[] y = increment(0.0, 0.05, 1.0);// y = 0.0:0.05:1.0
        double[][] z1 = f1(x, y);
        double[][] z2 = f2(x, y);

        // create your PlotPanel (you can use it as a JPanel) with a legend at
        // SOUTH
        Plot3DPanel plot = new Plot3DPanel("SOUTH");

        // add grid plot to the PlotPanel
        plot.addGridPlot("z=cos(PI*x)*sin(PI*y)", x, y, z1);
        plot.addGridPlot("z=sin(PI*x)*cos(PI*y)", x, y, z2);

        // put the PlotPanel in a JFrame like a JPanel
        JFrame frame = new JFrame("a plot panel");
        frame.setSize(600, 600);
        frame.setContentPane(plot);
        frame.setVisible(true);

    }

    // function definition: z=cos(PI*x)*sin(PI*y)
    public static double f1(double x, double y) {
        double z = cos(x * PI) * sin(y * PI);
        return z;
    }

    // grid version of the function
    public static double[][] f1(double[] x, double[] y) {
        double[][] z = new double[y.length][x.length];
        for (int i = 0; i < x.length; i++)
            for (int j = 0; j < y.length; j++)
                z[j][i] = f1(x[i], y[j]);
        return z;
    }

    // another function definition: z=sin(PI*x)*cos(PI*y)
    public static double f2(double x, double y) {
        double z = sin(x * PI) * cos(y * PI);
        return z;
    }

    // grid version of the function
    public static double[][] f2(double[] x, double[] y) {
        double[][] z = new double[y.length][x.length];
        for (int i = 0; i < x.length; i++)
            for (int j = 0; j < y.length; j++)
                z[j][i] = f2(x[i], y[j]);
        return z;
    }
}
