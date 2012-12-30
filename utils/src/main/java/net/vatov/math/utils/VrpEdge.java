package net.vatov.math.utils;

public class VrpEdge {

    private final VrpClient from;
    private final VrpClient to;
    private final double distance;
    
    public VrpEdge(VrpClient from, VrpClient to, double distance) {
        this.from = from;
        this.to = to;
        this.distance = distance;
    }

    public VrpClient getFrom() {
        return from;
    }

    public VrpClient getTo() {
        return to;
    }
    
    @Override
    public boolean equals(Object o) {
        if (null == o || !(o instanceof VrpEdge)) {
            return false;
        }
        VrpEdge e = (VrpEdge)o;
        return this.from.equals(e.getFrom()) && this.to.equals(e.getTo());
    }
    
    @Override
    public int hashCode() {
        return from.hashCode() * 37 * to.hashCode();
    }

    public double getDistance() {
        return distance;
    }
    
    public String toString() {
        return from + "->" + to;
    }
}