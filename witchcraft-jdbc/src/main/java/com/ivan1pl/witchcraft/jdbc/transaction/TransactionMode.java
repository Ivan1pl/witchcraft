package com.ivan1pl.witchcraft.jdbc.transaction;

/**
 * Represents different transaction behaviours.
 */
public enum TransactionMode {
    /**
     * Use currently active transaction, throw an exception if none are active.
     */
    REQUIRED_ACTIVE,
    /**
     * Suspend current transaction (if exists) and start a new one.
     */
    REQUIRED_NEW,
    /**
     * Use currently active transaction, create a new one if none are active.
     */
    REQUIRED,
    /**
     * Do not use transactions, throw an exception if any are active.
     */
    NONE_ACTIVE,
    /**
     * Do not use transactions, suspend any active.
     */
    NONE
}
