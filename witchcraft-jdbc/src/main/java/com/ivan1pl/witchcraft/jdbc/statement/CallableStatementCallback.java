package com.ivan1pl.witchcraft.jdbc.statement;

import java.sql.CallableStatement;
import java.sql.SQLException;

/**
 * Callback consuming a callable statement.
 * @param <T> callback return type
 */
@FunctionalInterface
public interface CallableStatementCallback<T> {
    /**
     * Apply callback method.
     * @param callableStatement callable statement to be consumed
     * @return instance of the requested type
     * @throws SQLException when an sql exception occurs inside the callback
     */
    T apply(CallableStatement  callableStatement) throws SQLException;
}
