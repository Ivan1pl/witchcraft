package com.ivan1pl.witchcraft.context.exception;

/**
 * Exception thrown when there is a cycle in dependency graph.
 */
public class DependencyCycleException extends Exception {
    /**
     * Constructor.
     */
    public DependencyCycleException() {
        super();
    }

    /**
     * Constructor.
     * @param message message
     */
    public DependencyCycleException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * @param cause cause
     */
    public DependencyCycleException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     * @param message message
     * @param cause cause
     */
    public DependencyCycleException(String message, Throwable cause) {
        super(message, cause);
    }
}
