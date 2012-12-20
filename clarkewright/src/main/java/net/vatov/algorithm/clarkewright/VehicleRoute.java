package net.vatov.algorithm.clarkewright;

import java.util.ArrayDeque;
import java.util.Deque;

public class VehicleRoute {

    private Deque<VrpClient> clients = new ArrayDeque<VrpClient>();
    private Double distance = 0d;
    private Double demand = 0d;

    @Override
    public String toString() {
        return new StringBuilder().append("[clients: ").append(clients).append(" distance: ").append(distance)
                .append(" demand: ").append(demand).append("]").toString();
    }
    
    public void addClient(VrpClient client) {
        clients.add(client);
        demand += client.getDemand();
    }

    public Double getDemand() {
        return demand;
    }

    public Double getDistance() {
        return distance;
    }
    
    public void setDistance(Double distance) {
        this.distance = distance;
    }
    
    public boolean containsClient(VrpClient c) {
        return clients.contains(c);
    }

    public VrpClient peekFirstClient() {
        return clients.peekFirst();
    }

    public VrpClient peekLastClient() {
        return clients.peekLast();
    }

    public VrpClient pollFirstClient() {
        VrpClient c = clients.pollFirst();
        c.setRoute(null);
        demand -= c.getDemand();
        return c;
    }

    public VrpClient pollLastClient() {
        VrpClient c = clients.pollLast();
        c.setRoute(null);
        demand -= c.getDemand();
        return c;
    }

    public void addClientFirst(VrpClient c) {
        clients.addFirst(c);
        demand += c.getDemand();
        c.setRoute(this);
    }

    public void addClientLast(VrpClient c) {
        clients.addLast(c);
        demand += c.getDemand();
        c.setRoute(this);
    }
    
    public boolean clientsIsEmpty() {
        return clients.isEmpty();
    }
    
    public Integer clientNum() {
        return clients.size();
    }
}
