package net.vatov.math.utils;

public class VrpDepot extends VrpClient {

    public VrpDepot(String label) {
        super(0, 0d, label);
    }
    
    public VrpDepot() {
        super(0, 0d);
    }

    @Override
    public boolean isDepot() {
        return true;
    }
}
