package com.ivan1pl.witchcraft.jdbc.exception;

/**
 * Exception thrown when database connection initialization or data source configuration fails.
 */
public class InitializationException extends Exception {
    /**
     * Constructor.
     */
    public InitializationException() {
        super();
    }

    /**
     * Constructor.
     * @param message message
     */
    public InitializationException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * @param cause cause
     */
    public InitializationException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     * @param message message
     * @param cause cause
     */
    public InitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
