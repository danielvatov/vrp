package net.vatov.algorithm.clarkewright;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.vatov.algorithm.AlgorithmException;
import net.vatov.ampl.solver.Solver;
import net.vatov.ampl.solver.io.UserIO;
import net.vatov.math.utils.Distance;
import net.vatov.math.utils.SavingRow;
import net.vatov.math.utils.VehicleRoute;
import net.vatov.math.utils.VrpClient;
import net.vatov.math.utils.VrpDepot;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClarkeWright extends Solver {

    private static final Logger logger = LoggerFactory.getLogger(ClarkeWright.class);

    final private RealMatrix euclideanCoordinatesXY;
    final private RealMatrix distances;
    final private Integer vehicleCapacity;
    final private Map<Integer, VrpClient> clients = new HashMap<Integer, VrpClient>();
    private Integer serviceTime;
    private RealMatrix timeWindows;

    private List<VehicleRoute> routes;

    private VrpDepot depot;

    /**
     * 
     * @param euclideanCoordinatesXY
     *            Coordinates of the clients and depot in euclidean space. Depot is
     *            expected to be at the first row
     * @param vehicleCapacity
     *            Capacity of homogeneous vehicle fleet
     * @param demands
     *            Demands of each client for goods. Depot's demand is 0.
     */
    public ClarkeWright(RealMatrix euclideanCoordinatesXY, Integer vehicleCapacity, RealVector demands) {
        this.euclideanCoordinatesXY = euclideanCoordinatesXY;
        this.distances = Distance.computeEuclideanDistances(this.euclideanCoordinatesXY);
        this.vehicleCapacity = vehicleCapacity;
        
        depot = new VrpDepot();
        
        final int CLIENTS_NUM = demands.getDimension() - 1;
        routes = new ArrayList<VehicleRoute>(CLIENTS_NUM);
        // initial routes - one for every customer
        for (int i = 1; i <= CLIENTS_NUM; ++i) {
            VehicleRoute route = new VehicleRoute();
            VrpClient client = new VrpClient(i, demands.getEntry(i));
            clients.put(client.getClientId(), client);
            route.addClient(client);
            route.setDistance(distances.getEntry(0, i) + distances.getEntry(i, 0));
            if (route.getDemand() <= vehicleCapacity) {
                logger.debug("Initial route {}", route);
                routes.add(route);
            } else {
                logger.info("Problem does not have feasible solution - route {} has demand higher than {}", route,
                        vehicleCapacity);
            }
        }
        logger.debug("Distances: {}", distances);
        logger.debug("Vehicle capacity: {}", vehicleCapacity);
        logger.debug("Clients: {}", clients);
    }

    @Override
    public Map<String, String> solve(InputStream input, UserIO io) {
        SavingRow[] savings = computeSavings();
        for (SavingRow s : savings) {
            VehicleRoute route1 = getRouteIfNotInternal(s.getFrom());
            VehicleRoute route2 = getRouteIfNotInternal(s.getTo());
            if (null == route1 || null == route2) {
                logger.debug("Client {} is {} internal, client {} is {} internal", new Object[] { s.getFrom(),
                        null == route1 ? "" : "not", s.getTo(), null == route2 ? "" : "not" });
                continue;
            }
            if (route1.equals(route2)) {
                logger.debug("Both clients are in the same route \nroute: {}\n\tclient1: {}\n\tclient2: {}",
                        new Object[] { route1, s.getFrom(), s.getTo() });
                continue;
            }
            if (vehicleOverload(route1, route2)) {
                logger.debug(
                        "The demand of the merged routes will excess vehicle capacity, \n\troute1: {}\n\troute2: {}\n\tvehicleCapacity: {}",
                        new Object[] { route1, route2, vehicleCapacity });
                continue;
            }
            io.pause(String.format("Merged routes %s and %s\nSaving %f", route1.getLabel(), route2.getLabel(), s.getSaving()));
            mergeRoutes(route1, route2, s);
            io.refreshData(routes);
        }
        //TODO
        return null;
    }

    /**
     * Merge two routes. s.from client is part of route1 and s.to client is part
     * of route2.
     * 
     * @param route1
     * @param route2
     * @param s
     */
    private void mergeRoutes(VehicleRoute route1, VehicleRoute route2, SavingRow s) {
        if (route1.peekFirstClient().equals(s.getFrom())) {
            if (route2.peekFirstClient().equals(s.getTo())) {
                // reverse clients; route2 + route1
                while (!route2.clientsIsEmpty()) {
                    route1.addClientFirst(route2.pollFirstClient());
                }
            } else if (route2.peekLastClient().equals(s.getTo())) {
                // route2+route1 ??? if have time windows may benefit from
                // route1 + route2 ???
                while (!route2.clientsIsEmpty()) {
                    route1.addClientFirst(route2.pollLastClient());
                }
            } else {
                throw new AlgorithmException("Client " + s.getTo() + " not found in route " + route2);
            }
        } else if (route1.peekLastClient().equals(s.getFrom())) {
            if (route2.peekFirstClient().equals(s.getTo())) {
                while (!route2.clientsIsEmpty()) {
                    route1.addClientLast(route2.pollFirstClient());
                }
            } else if (route2.peekLastClient().equals(s.getTo())) {
                while (!route2.clientsIsEmpty()) {
                    route1.addClientLast(route2.pollLastClient());
                }
            } else {
                throw new AlgorithmException("Client " + s.getTo() + " not found in route " + route2);
            }
        } else {
            throw new AlgorithmException("Client " + s.getFrom() + " not found in route " + route1);
        }
        route1.setDistance(route1.getDistance() + route2.getDistance()
                - (distances.getEntry(s.getFrom().getClientId(), 0) + distances.getEntry(0, s.getTo().getClientId()))
                + distances.getEntry(s.getFrom().getClientId(), s.getTo().getClientId()));
        routes.remove(route2);
        logger.debug("merged route {}", route1);
    }

    private VehicleRoute getRouteIfNotInternal(VrpClient client) {
        for (VehicleRoute r : routes) {
            if (r.containsClient(client)) {
                if (client.equals(r.peekFirstClient()) || client.equals(r.peekLastClient())) {
                    return r;
                } else {
                    return null;
                }
            }
        }
        throw new AlgorithmException("Client " + client + " not part of any route");
    }

    private Boolean vehicleOverload(VehicleRoute route1, VehicleRoute route2) {
        return route1.getDemand() + route2.getDemand() > vehicleCapacity;
    }

    private SavingRow[] computeSavings() {
        ArrayList<SavingRow> ret = new ArrayList<SavingRow>(clients.size() * (clients.size() - 1));
        // Client distances from the depot are at row 0 (column 0)
        for (int row = 1; row <= clients.size(); ++row) {
            for (int column = 1; column <= clients.size(); ++column) {
                if (row == column) {
                    continue;
                }
                Double saving = distances.getEntry(row, 0) + distances.getEntry(0, column)
                        - distances.getEntry(row, column);
                if (saving >= 0) {
                    ret.add(new SavingRow(saving, clients.get(row), clients.get(column)));
                }
            }
        }
        Collections.sort(ret);
        Collections.reverse(ret);
        return ret.toArray(new SavingRow[ret.size()]);
    }

    public RealMatrix getEuclideanCoordinatesXY() {
        return euclideanCoordinatesXY;
    }

    public RealMatrix getDistances() {
        return distances;
    }

    public Map<Integer, VrpClient> getClients() {
        return clients;
    }

    public VrpDepot getDepot() {
        return depot;
    }

    public List<VehicleRoute> getRoutes() {
        return routes;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }
}
