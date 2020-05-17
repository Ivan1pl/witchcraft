package com.ivan1pl.witchcraft.jdbc.statement;

/**
 * Represents a single occurrence of a named parameter in a query.
 */
public class Parameter {
    /**
     * Parameter name.
     */
    private final String name;

    /**
     * Parameter begin index.
     */
    private final int beginIndex;

    /**
     * Parameter end index.
     */
    private final int endIndex;

    /**
     * Create parameter definition.
     * @param name parameter name
     * @param beginIndex parameter begin index
     * @param endIndex parameter end index
     */
    public Parameter(String name, int beginIndex, int endIndex) {
        this.name = name;
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
    }

    /**
     * Get parameter name.
     * @return parameter name
     */
    public String getName() {
        return name;
    }

    /**
     * Get parameter begin index.
     * @return parameter begin index
     */
    public int getBeginIndex() {
        return beginIndex;
    }

    /**
     * Get parameter end index.
     * @return parameter end index
     */
    public int getEndIndex() {
        return endIndex;
    }
}
