package com.ivan1pl.witchcraft.jdbc.exception;

import java.sql.SQLException;

/**
 * Exception thrown when the result set contains more elements than requested.
 */
public class TooManyResultsException extends SQLException {
    /**
     * Constructor.
     */
    public TooManyResultsException() {
        super();
    }

    /**
     * Constructor.
     * @param message message
     */
    public TooManyResultsException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * @param cause cause
     */
    public TooManyResultsException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     * @param message message
     * @param cause cause
     */
    public TooManyResultsException(String message, Throwable cause) {
        super(message, cause);
    }
}
