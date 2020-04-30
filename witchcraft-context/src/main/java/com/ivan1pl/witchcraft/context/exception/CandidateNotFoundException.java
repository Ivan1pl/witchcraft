package com.ivan1pl.witchcraft.context.exception;

/**
 * Exception thrown when an object of some type is requested from the context and there are no objects of that type.
 */
public class CandidateNotFoundException extends RuntimeException {
    /**
     * Constructor.
     */
    public CandidateNotFoundException() {
        super();
    }

    /**
     * Constructor.
     * @param message message
     */
    public CandidateNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * @param cause cause
     */
    public CandidateNotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     * @param message message
     * @param cause cause
     */
    public CandidateNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
