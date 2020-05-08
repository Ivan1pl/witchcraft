package com.ivan1pl.witchcraft.commands.base;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents option value passed to a command.
 */
public class OptionValue {
    /**
     * Option's short name.
     */
    private final char shortName;

    /**
     * Option's long name.
     */
    private final String longName;

    /**
     * Whether the option accepts parameters.
     */
    private final boolean hasParameter;

    /**
     * Maximum number of values this option accepts.
     */
    private final int maxValues;

    /**
     * Option values.
     */
    private final Queue<String> values = new LinkedList<>();

    /**
     * Constructor.
     * @param shortName option's short name
     * @param longName option's long name
     * @param hasParameter whether the option accepts parameters
     * @param maxValues maximum number of values this option accepts
     */
    public OptionValue(char shortName, String longName, boolean hasParameter, int maxValues) {
        this.shortName = shortName;
        this.longName = longName;
        this.hasParameter = hasParameter;
        this.maxValues = maxValues;
    }

    /**
     * Add option value.
     * @param value value to add
     */
    public void addValue(String value) {
        if (maxValues > 0 && values.size() >= maxValues) {
            throw new IllegalStateException("Too many option values passed to the command");
        }
        values.add(value);
    }

    /**
     * Get first of option's unconsumed values.
     * @return first unconsumed value
     */
    public String getValue() {
        return values.poll();
    }

    /**
     * Get option's short name.
     * @return option's short name
     */
    public char getShortName() {
        return shortName;
    }

    /**
     * Get option's long name.
     * @return option's long name
     */
    public String getLongName() {
        return longName;
    }

    /**
     * Check if the option accepts parameters.
     * @return whether the option accepts parameters
     */
    public boolean isHasParameter() {
        return hasParameter;
    }
}
