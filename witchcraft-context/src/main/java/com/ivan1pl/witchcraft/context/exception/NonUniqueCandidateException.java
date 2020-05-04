package com.ivan1pl.witchcraft.context.exception;

/**
 * Exception thrown when an object of some type is requested from the context and there is more than one object of that
 * type.
 */
public class NonUniqueCandidateException extends RuntimeException {
    /**
     * Constructor.
     */
    public NonUniqueCandidateException() {
        super();
    }

    /**
     * Constructor.
     * @param message message
     */
    public NonUniqueCandidateException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * @param cause cause
     */
    public NonUniqueCandidateException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     * @param message message
     * @param cause cause
     */
    public NonUniqueCandidateException(String message, Throwable cause) {
        super(message, cause);
    }
}
