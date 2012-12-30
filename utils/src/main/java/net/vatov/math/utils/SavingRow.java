package net.vatov.math.utils;

import java.util.Arrays;


public class SavingRow implements Comparable<SavingRow> {
    private Double saving;
    private VrpClient from;
    private VrpClient to;

    public SavingRow(Double saving, VrpClient from, VrpClient to) {
        if (null == saving || null == from || null == to) {
            throw new NullPointerException();
        }
        this.saving = saving;
        this.from = from;
        this.to = to;
    }

    public Double getSaving() {
        return saving;
    }

    public VrpClient getFrom() {
        return from;
    }

    public VrpClient getTo() {
        return to;
    }

    @Override
    public String toString() {
        return String.format("%1$f (%2$s,%3$s)", saving, from, to);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] { saving, from.getClientId(), to.getClientId() });
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SavingRow) {
            return 0 == compareTo((SavingRow) o);
        }
        return false;
    }

    public int compareTo(SavingRow arg0) {
        if (!saving.equals(arg0.getSaving())) {
            return (int) Math.signum(saving - arg0.saving);
        } else if (from != arg0.getFrom()) {
            return (int) Math.signum(from.getClientId() - arg0.getFrom().getClientId());
        } else if (to != arg0.getTo()) {
            return (int) Math.signum(to.getClientId() - arg0.getTo().getClientId());
        }
        return 0;
    }
}
