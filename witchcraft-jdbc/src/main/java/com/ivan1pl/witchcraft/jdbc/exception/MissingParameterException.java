package com.ivan1pl.witchcraft.jdbc.exception;

import java.sql.SQLException;

/**
 * Exception thrown when a query parameter was not set and an attempt is made to execute that query.
 */
public class MissingParameterException extends SQLException {
    /**
     * Constructor.
     */
    public MissingParameterException() {
        super();
    }

    /**
     * Constructor.
     * @param message message
     */
    public MissingParameterException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * @param cause cause
     */
    public MissingParameterException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     * @param message message
     * @param cause cause
     */
    public MissingParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}
