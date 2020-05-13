package com.ivan1pl.witchcraft.jdbc.statement;

import com.ivan1pl.witchcraft.jdbc.exception.EmptyResultSetException;
import com.ivan1pl.witchcraft.jdbc.exception.MissingParameterException;
import com.ivan1pl.witchcraft.jdbc.exception.TooManyResultsException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
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
     * Run query.
     * @param query query to run
     * @param rowMapper row mapper
     * @param <T> requested element type
     * @return list of rows mapped by {@code rowMapper}
     * @throws SQLException when the query fails to run or its result cannot be mapped to Java object
     */
    public <T> List<T> query(Query query, RowMapper<T> rowMapper) throws SQLException {
        return execute(query, PreparedStatement.NO_GENERATED_KEYS, null, preparedStatement -> {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<T> result = new LinkedList<>();
            int index = 0;
            while (resultSet.next()) {
                result.add(rowMapper.mapRow(index++, resultSet));
            }
            return result;
        });
    }

    /**
     * Run query.
     * @param query query to run
     * @param requestedType requested SQL type
     * @param <T> requested element type
     * @return list of rows mapped to {@code requestedType}
     * @throws SQLException when the query fails to run or its result cannot be mapped to Java object
     */
    public <T> List<T> query(Query query, Class<T> requestedType) throws SQLException {
        return execute(query, PreparedStatement.NO_GENERATED_KEYS, null, preparedStatement -> {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<T> result = new LinkedList<>();
            while (resultSet.next()) {
                result.add(resultSet.getObject(1, requestedType));
            }
            return result;
        });
    }

    /**
     * Run query.
     * @param query query to run
     * @return result set containing rows returned from the database
     * @throws SQLException when the query fails to run
     */
    public ResultSet query(Query query) throws SQLException {
        return execute(query, PreparedStatement.NO_GENERATED_KEYS, null, PreparedStatement::executeQuery);
    }

    /**
     * Query for a single object from the database.
     * @param query query to run
     * @param rowMapper row mapper
     * @param <T> requested element type
     * @return a single row mapped by {@code rowMapper}
     * @throws SQLException when the query fails to run or its result cannot be mapped to Java object
     */
    public <T> T queryForObject(Query query, RowMapper<T> rowMapper) throws SQLException {
        query = query.clone();
        query.setMaxRows(2);
        List<T> result = query(query, rowMapper);
        if (result.isEmpty()) {
            throw new EmptyResultSetException("No data found");
        } else if (result.size() != 1) {
            throw new TooManyResultsException("Too many rows returned from query. Expected exactly 1");
        } else {
            return result.get(0);
        }
    }

    /**
     * Query for a single object from the database.
     * @param query query to run
     * @param requestedType requested SQL type
     * @param <T> requested element type
     * @return a single row mapped to {@code requestedType}
     * @throws SQLException when the query fails to run or its result cannot be mapped to Java object
     */
    public <T> T queryForObject(Query query, Class<T> requestedType) throws SQLException {
        query = query.clone();
        query.setMaxRows(2);
        List<T> result = query(query, requestedType);
        if (result.isEmpty()) {
            throw new EmptyResultSetException("No data found");
        } else if (result.size() != 1) {
            throw new TooManyResultsException("Too many rows returned from query. Expected exactly 1");
        } else {
            return result.get(0);
        }
    }

    /**
     * Execute insert, update or delete query.
     * @param query query to run
     * @return updated rows count
     * @throws SQLException when the query fails to run
     */
    public int update(Query query) throws SQLException {
        return execute(query, PreparedStatement.NO_GENERATED_KEYS, null, PreparedStatement::executeUpdate);
    }

    /**
     * Execute insert, update or delete query.
     * @param query query to run
     * @param rowMapper row mapper
     * @param autoGeneratedColumnNames columns containing automatically generated values; required for some JDBCs
     *                                 (e.g. Oracle)
     * @param <T> requested element type
     * @return list of rows containing automatically generated values mapped by {@code rowMapper}
     * @throws SQLException when the query fails to run or its result cannot be mapped to Java object
     */
    public <T> List<T> update(Query query, RowMapper<T> rowMapper, String... autoGeneratedColumnNames)
            throws SQLException {
        return execute(query, PreparedStatement.RETURN_GENERATED_KEYS, autoGeneratedColumnNames, preparedStatement -> {
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            List<T> result = new LinkedList<>();
            int index = 0;
            while (resultSet.next()) {
                result.add(rowMapper.mapRow(index++, resultSet));
            }
            return result;
        });
    }

    /**
     * Execute insert, update or delete query.
     * @param query query to run
     * @param requestedType requested SQL type
     * @param autoGeneratedColumnNames columns containing automatically generated values; required for some JDBCs
     *                                 (e.g. Oracle)
     * @param <T> requested element type
     * @return ist of rows containing automatically generated values mapped to {@code requestedType}
     * @throws SQLException when the query fails to run or its result cannot be mapped to Java object
     */
    public <T> List<T> update(Query query, Class<T> requestedType, String... autoGeneratedColumnNames)
            throws SQLException {
        return execute(query, PreparedStatement.RETURN_GENERATED_KEYS, autoGeneratedColumnNames, preparedStatement -> {
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            List<T> result = new LinkedList<>();
            while (resultSet.next()) {
                result.add(resultSet.getObject(1, requestedType));
            }
            return result;
        });
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

    /**
     * Execute prepared statement callback.
     * @param query query to execute
     * @param autoGeneratedKeys whether to return automatically generated keys
     * @param autoGeneratedColumnNames automatically generated column names
     * @param preparedStatementCallback callback to execute
     * @param <T> callback return type
     * @return instance of the requested type
     * @throws SQLException when an exception occurs during statement preparation or inside the callback
     */
    private <T> T execute(Query query, int autoGeneratedKeys, String[] autoGeneratedColumnNames,
                          PreparedStatementCallback<T> preparedStatementCallback)
            throws SQLException {
        Objects.requireNonNull(preparedStatementCallback);
        Objects.requireNonNull(query);
        try (PreparedStatement preparedStatement =
                     prepareStatement(query, autoGeneratedKeys, autoGeneratedColumnNames)) {
            return preparedStatementCallback.apply(preparedStatement);
        }
    }

    /**
     * Build a {@link PreparedStatement} object from given {@link Query} definition.
     * @param query {@link Query} definition
     * @param autoGeneratedKeys whether to return automatically generated keys
     * @param autoGeneratedColumnNames automatically generated column names
     * @return prepared statement
     * @throws SQLException when an SQL exception occurs within the database driver or a parameter was not passed to a
     *                      query
     */
    private PreparedStatement prepareStatement(final Query query, final int autoGeneratedKeys,
                                               String[] autoGeneratedColumnNames) throws SQLException {
        return execute(connection -> {
            String sqlToRun = query.getSql();
            List<Parameter> namedParameters = query.getParameters();
            List<ParameterValue> parameterValues = new LinkedList<>();
            if (!namedParameters.isEmpty()) {
                for (Parameter parameter : namedParameters) {
                    ParameterValue parameterValue = query.getParameterValues().get(parameter.getName());
                    if (parameterValue == null) {
                        throw new MissingParameterException(
                                String.format("Parameter '%s' was not set for query", parameter.getName()));
                    }
                    parameterValues.add(parameterValue);
                }
            } else {
                int i = 1;
                for (Integer parameter : query.getUnnamedParameters()) {
                    ParameterValue parameterValue = query.getUnnamedParameterValues().get(i++);
                    if (parameterValue == null) {
                        throw new MissingParameterException(
                                String.format("Parameter '%d' was not set for query", i - 1));
                    }
                    parameterValues.add(parameterValue);
                }
            }

            PreparedStatement preparedStatement =
                    autoGeneratedColumnNames == null || autoGeneratedColumnNames.length == 0 ?
                            connection.prepareStatement(sqlToRun, autoGeneratedKeys) :
                            connection.prepareStatement(sqlToRun, autoGeneratedColumnNames);

            int index = 1;
            for (ParameterValue parameterValue : parameterValues) {
                if (parameterValue.getType() == ParameterType.AUTO.getType()) {
                    preparedStatement.setObject(index++, parameterValue.getValue());
                } else {
                    preparedStatement.setObject(index++, parameterValue.getValue(), parameterValue.getType());
                }
            }

            return preparedStatement;
        });
    }
}
