package com.ivan1pl.witchcraft.jdbc.statement;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Callback consuming a statement.
 * @param <T> callback return type
 */
@FunctionalInterface
public interface StatementCallback<T> {
    /**
     * Apply callback method.
     * @param statement statement to be consumed
     * @return instance of the requested type
     * @throws SQLException when an sql exception occurs inside the callback
     */
    T apply(Statement statement) throws SQLException;
}
