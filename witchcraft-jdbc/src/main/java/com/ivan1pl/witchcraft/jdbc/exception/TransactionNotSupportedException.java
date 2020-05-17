package com.ivan1pl.witchcraft.jdbc.exception;

import java.sql.SQLException;

/**
 * Exception thrown when a method does not support transactions but there is an active transaction.
 */
public class TransactionNotSupportedException extends SQLException {
    /**
     * Constructor.
     */
    public TransactionNotSupportedException() {
        super();
    }

    /**
     * Constructor.
     * @param message message
     */
    public TransactionNotSupportedException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * @param cause cause
     */
    public TransactionNotSupportedException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     * @param message message
     * @param cause cause
     */
    public TransactionNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }
}
