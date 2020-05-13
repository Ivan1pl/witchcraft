package com.ivan1pl.witchcraft.jdbc.exception;

import java.sql.SQLException;

/**
 * Exception thrown when the result set is empty and some elements were expected.
 */
public class EmptyResultSetException extends SQLException {
    /**
     * Constructor.
     */
    public EmptyResultSetException() {
        super();
    }

    /**
     * Constructor.
     * @param message message
     */
    public EmptyResultSetException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * @param cause cause
     */
    public EmptyResultSetException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     * @param message message
     * @param cause cause
     */
    public EmptyResultSetException(String message, Throwable cause) {
        super(message, cause);
    }
}
