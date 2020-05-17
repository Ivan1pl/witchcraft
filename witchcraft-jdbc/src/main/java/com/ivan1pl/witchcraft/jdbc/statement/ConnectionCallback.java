package com.ivan1pl.witchcraft.jdbc.statement;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Callback consuming a connection.
 * @param <T> callback return type
 */
@FunctionalInterface
public interface ConnectionCallback<T> {
    /**
     * Apply callback method.
     * @param connection connection to be consumed
     * @return instance of the requested type
     * @throws SQLException when an sql exception occurs inside the callback
     */
    T apply(Connection connection) throws SQLException;
}
