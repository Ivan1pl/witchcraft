package com.ivan1pl.witchcraft.jdbc.statement;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Callback consuming a prepared statement.
 * @param <T> callback return type
 */
@FunctionalInterface
public interface PreparedStatementCallback<T> {
    /**
     * Apply callback method.
     * @param preparedStatement prepared statement to be consumed
     * @return instance of the requested type
     * @throws SQLException when an sql exception occurs inside the callback
     */
    T apply(PreparedStatement preparedStatement) throws SQLException;
}
