package com.ivan1pl.witchcraft.jdbc.exception;

import java.sql.SQLException;

/**
 * Exception thrown when a query wrapper cannot be built.
 */
public class InvalidQueryException extends SQLException {
    /**
     * Constructor.
     */
    public InvalidQueryException() {
        super();
    }

    /**
     * Constructor.
     * @param message message
     */
    public InvalidQueryException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * @param cause cause
     */
    public InvalidQueryException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     * @param message message
     * @param cause cause
     */
    public InvalidQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
