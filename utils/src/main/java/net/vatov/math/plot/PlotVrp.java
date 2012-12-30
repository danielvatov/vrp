package net.vatov.math.plot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import net.vatov.math.utils.VehicleRoute;
import net.vatov.math.utils.VrpClient;
import net.vatov.math.utils.VrpDepot;
import net.vatov.math.utils.VrpEdge;

import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.TransformerUtils;
import org.apache.commons.math3.linear.RealMatrix;

import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class PlotVrp {

    public Dimension size = new Dimension(900, 650);

    private final RealMatrix coordinates;
    private final RealMatrix distances;
    private final List<VrpClient> clients;
    private Map<VehicleRoute, Color> routeColors;
    private JFrame frame;

    public PlotVrp(final RealMatrix coordinates, final RealMatrix distances, final List<VrpClient> clients) {
        this.coordinates = coordinates;
        this.distances = distances;
        this.clients = clients;
        routeColors = new HashMap<VehicleRoute, Color>();
    }

    public void plotGraph() {
        Graph<VrpClient, VrpEdge> g = createGraph();
        frame = new JFrame();
        Dimension d = getGraphDimension(coordinates);
        Transformer<VrpClient, Point2D> transformer = getVertexTransformer(d);
        StaticLayout<VrpClient, VrpEdge> staticLayout = new StaticLayout<VrpClient, VrpEdge>(g, transformer);
        final VisualizationViewer<VrpClient, VrpEdge> vv = new VisualizationViewer<VrpClient, VrpEdge>(staticLayout);

        vv.getRenderContext().setEdgeLabelTransformer(getEdgeLabeler());
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<VrpClient>());
        Transformer<VrpClient, Shape> shapeTransformer = new Transformer<VrpClient, Shape>() {
            public Shape transform(VrpClient input) {
                float d = 7f;
                if (input instanceof VrpDepot) {
                    return new Rectangle2D.Float(0f, 0f, d, d);
                } else {
                    return new Ellipse2D.Float(0f, 0f, d, d);
                }
            }
        };
        vv.getRenderContext().setVertexShapeTransformer(shapeTransformer);
        Transformer<VrpClient, Paint> vertexFillPaintTransformer = new Transformer<VrpClient, Paint>() {

            public Paint transform(VrpClient input) {
                if (vv.getPickedVertexState().isPicked(input)) {
                    return Color.YELLOW;
                }
                if (input instanceof VrpDepot) {
                    return Color.GREEN;
                } else {
                    return Color.BLUE;
                }
            }
        };
        vv.getRenderContext().setVertexFillPaintTransformer(vertexFillPaintTransformer);

        vv.getRenderContext().setEdgeDrawPaintTransformer(getEdgeRouteDrawTransformer(vv));
        vv.getRenderContext().setEdgeIncludePredicate(getEdgeRoutePredicate(vv));
        vv.getRenderContext().setEdgeArrowPredicate(getEdgeArrowPredicate());
        DefaultModalGraphMouse<VrpClient, String> gm = new DefaultModalGraphMouse<VrpClient, String>();
        gm.setMode(Mode.TRANSFORMING);
        vv.addKeyListener(gm.getModeKeyListener());
        vv.setGraphMouse(gm);

        vv.setPreferredSize(size);
        frame.add(vv, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(size);
        frame.pack();
        frame.setVisible(true);
    }
    
    private Predicate<Context<Graph<VrpClient, VrpEdge>, VrpEdge>> getEdgeArrowPredicate() {
        return new Predicate<Context<Graph<VrpClient,VrpEdge>,VrpEdge>>() {
            
            public boolean evaluate(Context<Graph<VrpClient, VrpEdge>, VrpEdge> object) {
                return false;
            }
        };
    }

    public Component getFrame() {
        return frame;
    }
    
    private Predicate<Context<Graph<VrpClient, VrpEdge>, VrpEdge>> getEdgeRoutePredicate(
            VisualizationViewer<VrpClient, VrpEdge> vv) {
        return new Predicate<Context<Graph<VrpClient, VrpEdge>, VrpEdge>>() {

            public boolean evaluate(Context<Graph<VrpClient, VrpEdge>, VrpEdge> object) {
                return null != getRouteForEdge(object.element);
            }
        };
    }

    private Transformer<VrpEdge, String> getEdgeLabeler() {
        return new Transformer<VrpEdge, String>() {

            public String transform(VrpEdge input) {
                VehicleRoute r = getRouteForEdge(input);
                if (null == r) {
                    return "NA";
                }
                return r.getLabel();
            }
        };
    }

    private Transformer<VrpEdge, Paint> getEdgeRouteDrawTransformer(VisualizationViewer<VrpClient, VrpEdge> vv) {
        return new Transformer<VrpEdge, Paint>() {
            public Paint transform(VrpEdge input) {
                VehicleRoute r = getRouteForEdge(input);
                if (null != r) {
                    return routeColors.get(r);
                }
                return Color.RED;
            }
        };
    }

    private VehicleRoute getRouteForEdge(VrpEdge e) {
        for (VehicleRoute r : routeColors.keySet()) {
            if (r.containsEdge(e)) {
                return r;
            }
        }
        return null;
    }

    private Predicate<Context<Graph<VrpClient, String>, String>> getEdgeSelectionPredicate(
            final VisualizationViewer<VrpClient, String> vv) {
        Predicate<Context<Graph<VrpClient, String>, String>> edgeIncludePredicate = new Predicate<Context<Graph<VrpClient, String>, String>>() {

            public boolean evaluate(Context<Graph<VrpClient, String>, String> ctx) {
                Pair<VrpClient> endpoints = ctx.graph.getEndpoints(ctx.element);
                return vv.getPickedVertexState().isPicked(endpoints.getFirst())
                        || vv.getPickedVertexState().isPicked(endpoints.getSecond());
            }
        };
        return edgeIncludePredicate;
    }

    public void refreshRoutes(List<VehicleRoute> routes) {
        for (VehicleRoute r : routes) {
            if (!routeColors.containsKey(r)) {
                routeColors
                        .put(r, new Color(getRandomColorChannel(), getRandomColorChannel(), getRandomColorChannel()));
            }
        }
        List<VehicleRoute> toRemove = new ArrayList<VehicleRoute>();
        for (VehicleRoute r : routeColors.keySet()) {
            if (!routes.contains(r)) {
                toRemove.add(r);
            }
        }
        for (VehicleRoute r : toRemove) {
            routeColors.remove(r);
        }
        frame.repaint();
    }

    private int getRandomColorChannel() {
        return Long.valueOf(Math.round(Math.random() * 255)).intValue();
    }

    private Dimension getGraphDimension(RealMatrix coordinates) {
        double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE, minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        for (int row = 0; row < coordinates.getRowDimension(); ++row) {
            double[] r = coordinates.getRow(row);
            if (r[0] < minX) {
                minX = r[0];
            }
            if (r[0] > maxX) {
                maxX = r[0];
            }
            if (r[1] < minY) {
                minY = r[1];
            }
            if (r[1] > maxY) {
                maxY = r[1];
            }
        }
        return new Dimension(Long.valueOf(Math.round(maxX - minX)).intValue(), Long.valueOf(Math.round(maxY - minY))
                .intValue());
    }

    private Transformer<VrpClient, Point2D> getVertexTransformer(Dimension d) {
        Map<VrpClient, Point2D> map = new HashMap<VrpClient, Point2D>();
        double base = 20, scale = 0;
        double widthDiff = size.getWidth() - 4 * base - d.getWidth();
        double heightDiff = size.getHeight() - 4 * base - d.getHeight();
        if (widthDiff > 0 && heightDiff > 0) {
            if (widthDiff > heightDiff) {
                scale = (size.getHeight() - 4 * base) / d.getHeight();
            } else {
                scale = (size.getWidth() - 4 * base) / d.getWidth();
            }
        }
        for (VrpClient c : clients) {
            Point2D p = new Point2D() {
                private double x, y;

                @Override
                public void setLocation(double x, double y) {
                    this.x = x;
                    this.y = y;
                }

                @Override
                public double getY() {
                    return y;
                }

                @Override
                public double getX() {
                    return x;
                }

                @Override
                public String toString() {
                    return x + "," + y;
                }
            };
            p.setLocation(base + scale * coordinates.getEntry(c.getClientId(), 0),
                    base + scale * coordinates.getEntry(c.getClientId(), 1));
            map.put(c, p);
        }

        Transformer<VrpClient, Point2D> transformer = TransformerUtils.mapTransformer(map);
        return transformer;
    }

    private Graph<VrpClient, VrpEdge> createGraph() {
        Graph<VrpClient, VrpEdge> ret = DirectedSparseMultigraph.<VrpClient, VrpEdge>getFactory().create();
        for (VrpClient c : clients) {
            ret.addVertex(c);
        }
        for (int i = 0; i < distances.getRowDimension(); ++i) {
            for (int j = 0; j < distances.getColumnDimension(); ++j) {
                if (i == j) {
                    continue;
                }
                ret.addEdge(new VrpEdge(clients.get(i), clients.get(j), distances.getEntry(i, j)), clients.get(i),
                        clients.get(j), EdgeType.DIRECTED);
            }
        }
        return ret;
    }
}
