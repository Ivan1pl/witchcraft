package com.ivan1pl.witchcraft.context.exception;

/**
 * Exception thrown when WitchCraft is unable to instantiate managed object due to unsatisfied dependencies.
 */
public class UnsatisfiedDependencyException extends Exception {
    /**
     * Constructor.
     */
    public UnsatisfiedDependencyException() {
        super();
    }

    /**
     * Constructor.
     * @param message message
     */
    public UnsatisfiedDependencyException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * @param cause cause
     */
    public UnsatisfiedDependencyException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     * @param message message
     * @param cause cause
     */
    public UnsatisfiedDependencyException(String message, Throwable cause) {
        super(message, cause);
    }
}
