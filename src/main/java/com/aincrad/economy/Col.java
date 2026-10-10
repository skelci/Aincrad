package com.aincrad.economy;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * Col — Aincrad's virtual currency. 1 Col = 1 unit (no sub-denominations).
 * Immutable value object: use {@link #add(Col)}, {@link #subtract(Col)},
 * and {@link #multiply(int)} to derive new instances.
 */
public final class Col {

    public static final Col ZERO = new Col(0);

    private final long amount;

    public Col(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Col amount cannot be negative: " + amount);
        }
        this.amount = amount;
    }

    public long getAmount() {
        return amount;
    }

    public boolean isZero() {
        return amount == 0;
    }

    /** @return a new Col with the given amount added. */
    public Col add(Col other) {
        return new Col(this.amount + other.amount);
    }

    /**
     * @return a new Col with the given amount subtracted, never below zero.
     * @throws IllegalArgumentException if the result would be negative.
     */
    public Col subtract(Col other) {
        long result = this.amount - other.amount;
        if (result < 0) {
            throw new IllegalArgumentException(
                    "Cannot subtract " + other.amount + " from " + this.amount);
        }
        return new Col(result);
    }

    /** @return a new Col scaled by the given factor. */
    public Col multiply(int factor) {
        return new Col(this.amount * factor);
    }

    /** @return a new Col with the sign flipped. */
    public Col negate() {
        return new Col(-this.amount);
    }

    /**
     * Formats this amount for display, e.g. {@code "1,234 Col"}.
     */
    public String format() {
        return format(this.amount);
    }

    /**
     * Formats this amount as an inline chat component, e.g.
     * a green {@code "1,234"} with a gray {@code " Col"} suffix.
     */
    public MutableComponent formatComponent() {
        return Component.literal(format(this.amount))
                .withStyle(ChatFormatting.GREEN)
                .append(Component.literal(" Col").withStyle(ChatFormatting.GRAY));
    }

    // ── Static helpers ───────────────────────────────────────────────

    /** Formats a raw long with comma grouping, e.g. {@code 1234567 -> "1,234,567"}. */
    public static String format(long amount) {
        return String.format("%,d", amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Col col = (Col) o;
        return amount == col.amount;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(amount);
    }

    @Override
    public String toString() {
        return format();
    }
}
