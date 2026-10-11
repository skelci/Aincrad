package com.aincrad.paper.economy;

/**
 * Col — Aincrad's virtual currency. 1 Col = 1 unit.
 * Immutable value object with arithmetic helpers.
 */
public final class Col {
    public static final Col ZERO = new Col(0);

    private final long amount;

    public Col(long amount) {
        if (amount < 0) throw new IllegalArgumentException("Col amount cannot be negative: " + amount);
        this.amount = amount;
    }

    public long getAmount() { return amount; }
    public boolean isZero() { return amount == 0; }

    public Col add(Col other) { return new Col(this.amount + other.amount); }

    public Col subtract(Col other) {
        long result = this.amount - other.amount;
        if (result < 0) throw new IllegalArgumentException("Cannot subtract " + other.amount + " from " + this.amount);
        return new Col(result);
    }

    public Col multiply(int factor) { return new Col(this.amount * factor); }
    public Col negate() { return new Col(-this.amount); }

    public String format() { return format(this.amount); }

    public static String format(long amount) { return String.format("%,d", amount); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return amount == ((Col) o).amount;
    }

    @Override
    public int hashCode() { return Long.hashCode(amount); }

    @Override
    public String toString() { return format(); }
}
