package com.ivan1pl.witchcraft.jdbc.exception;

import java.sql.SQLException;

/**
 * Exception thrown when a method requires an active transactions but there are none.
 */
public class TransactionNotActiveException extends SQLException {
    /**
     * Constructor.
     */
    public TransactionNotActiveException() {
        super();
    }

    /**
     * Constructor.
     * @param message message
     */
    public TransactionNotActiveException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * @param cause cause
     */
    public TransactionNotActiveException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     * @param message message
     * @param cause cause
     */
    public TransactionNotActiveException(String message, Throwable cause) {
        super(message, cause);
    }
}
