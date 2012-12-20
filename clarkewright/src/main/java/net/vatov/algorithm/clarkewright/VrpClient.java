package net.vatov.algorithm.clarkewright;

public class VrpClient {

    private final Integer clientId;
    private final Double demand;
    private VehicleRoute route;

    public VrpClient(Integer clientId, Double demand) {
        this.clientId = clientId;
        this.demand = demand;
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
        return new StringBuilder("[").append(clientId).append(", d:").append(demand).append("]").toString();
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

}
