package net.pwing.itemattributes.util;

import me.redned.config.ConfigOption;

public class IntRange {
    @ConfigOption(name = "min", description = "The minimum value of the range.", required = true)
    private int min;

    @ConfigOption(name = "max", description = "The maximum value of the range.", required = true)
    private int max;

    public IntRange() {
    }

    public IntRange(int value) {
        this(value, value);
    }

    public IntRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }

    public static IntRange minInclusive(int min) {
        return new IntRange(min, Integer.MAX_VALUE);
    }

    public static IntRange maxInclusive(int max) {
        return new IntRange(0, max);
    }
}
