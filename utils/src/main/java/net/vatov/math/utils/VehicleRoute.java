package net.vatov.math.utils;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class VehicleRoute {

    private Deque<VrpClient> clients = new ArrayDeque<VrpClient>();
    private Double distance = 0d;
    private Double demand = 0d;
    private String label;

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

    public boolean containsEdge(VrpEdge e) {
        if (clients.isEmpty()) {
            return false;
        }
        if (e.getFrom().isDepot()) {
            return e.getTo().equals(clients.peekFirst());
        }
        if (e.getTo().isDepot()) {
            return e.getFrom().equals(clients.peekLast());
        }
        VrpClient[] clientsArray = clients.toArray(new VrpClient[clients.size()]);
        if (clientsArray.length < 2) {
            return false;
        }
        int i=0,j=1;
        do {
            if (e.getFrom().equals(clientsArray[i]) && e.getTo().equals(clientsArray[j])) {
                return true;
            }
            i++;
            j++;
        } while (j < clientsArray.length);
        return false;
    }

    public String getLabel() {
        if (null != label) {
            return label;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        if (!clients.isEmpty()) {
            for (VrpClient c : clients) {
                sb.append(c.getLabel()).append(", ");
            }
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append(")");
        return sb.toString();
    }

    public void setLabel(String label) {
        this.label = label;
    }
}