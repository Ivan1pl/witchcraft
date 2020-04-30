package com.ivan1pl.witchcraft.context.exception;

/**
 * Exception thrown when dependency injection context failed to initialize.
 */
public class InitializationFailedException extends Exception {
    /**
     * Constructor.
     */
    public InitializationFailedException() {
        super();
    }

    /**
     * Constructor.
     * @param message message
     */
    public InitializationFailedException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * @param cause cause
     */
    public InitializationFailedException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     * @param message message
     * @param cause cause
     */
    public InitializationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
