package com.ivan1pl.witchcraft.jdbc.statement;

/**
 * Represents query parameter value.
 */
public class ParameterValue {
    /**
     * Parameter value.
     */
    private final Object value;

    /**
     * Parameter SQL type.
     */
    private final int type;

    /**
     * Constructor.
     * @param value parameter value
     * @param type parameter SQL type
     */
    public ParameterValue(Object value, int type) {
        this.value = value;
        this.type = type;
    }

    /**
     * Constructor.
     * @param value parameter value
     * @param type parameter SQL type
     */
    public ParameterValue(Object value, ParameterType type) {
        this.value = value;
        this.type = type.getType();
    }

    /**
     * Get parameter value.
     * @return parameter value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Get parameter SQL type.
     * @return parameter SQL type
     */
    public int getType() {
        return type;
    }
}
