package com.ivan1pl.witchcraft.jdbc.statement;

import com.ivan1pl.witchcraft.jdbc.connection.ConnectionCallback;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Data source wrapper, providing statement api with named parameters.
 */
public class StatementTemplate {
    private final DataSource dataSource;

    /**
     * Create wrapper instance.
     * @param dataSource data source
     */
    public StatementTemplate(DataSource dataSource) {
        Objects.requireNonNull(dataSource);
        this.dataSource = dataSource;
    }

    /**
     * Execute connection callback.
     * @param connectionCallback callback to execute
     * @param <T> callback return type
     * @return instance of the requested type
     * @throws SQLException when an exception occurs during the connection acquisition or inside the callback
     */
    private <T> T execute(ConnectionCallback<T> connectionCallback) throws SQLException {
        Objects.requireNonNull(connectionCallback);
        try (Connection connection = dataSource.getConnection()) {
            return connectionCallback.apply(connection);
        }
    }
}
