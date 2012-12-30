package net.vatov.math.utils;

public class VrpClient implements Comparable<VrpClient>{

    private final Integer clientId;
    private final Double demand;
    private VehicleRoute route;
    private final String label;

    public VrpClient(Integer clientId, Double demand, String label) {
        this.clientId = clientId;
        this.demand = demand;
        this.label = label;
    }

    public VrpClient(Integer clientId, Double demand) {
        this(clientId, demand, clientId.toString());
    }

    public Integer getClientId() {
        return clientId;
    }

    public Double getDemand() {
        return demand;
    }

    public VehicleRoute getRoute() {
        return route;
    }

    public void setRoute(VehicleRoute route) {
        this.route = route;
    }

    @Override
    public String toString() {
        return new StringBuilder("[").append(getLabel()).append(", d:").append(demand).append("]").toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VrpClient) {
            return ((VrpClient)obj).getClientId().equals(clientId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return clientId.hashCode();
    }

    public String getLabel() {
        if (null != label) {
            return label;
        }
        return clientId.toString();
    }

    public int compareTo(VrpClient o) {
        return this.getClientId() - o.getClientId();
    }
    
    public boolean isDepot() {
        return false;
    }

}
