package com.ivan1pl.witchcraft.jdbc.transaction;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * Non-closeable connection wrapper.
 */
public class TransactionConnectionWrapper implements Connection {
    /**
     * Wrapped connection.
     */
    private final Connection connection;

    /**
     * Wrap connection.
     * @param connection connection to wrap
     */
    public TransactionConnectionWrapper(Connection connection) {
        this.connection = connection;
    }

    /**
     * Creates a {@link Statement} object for sending SQL statements to the database. SQL statements without parameters
     * are normally executed using Statement objects. If the same SQL statement is executed many times, it may be more
     * efficient to use a {@link PreparedStatement} object.
     * <p>
     * Result sets created using the returned {@link Statement} object will by default be type {@code TYPE_FORWARD_ONLY}
     * and have a concurrency level of {@code CONCUR_READ_ONLY}. The holdability of the created result sets can be
     * determined by calling {@link #getHoldability()}.
     *
     * @return a new default {@link Statement} object
     * @throws SQLException if a database access error occurs or this method is called on a closed connection
     */
    @Override
    public Statement createStatement() throws SQLException {
        return connection.createStatement();
    }

    /**
     * Creates a {@link PreparedStatement} object for sending parameterized SQL statements to the database.
     * <p>
     * A SQL statement with or without IN parameters can be pre-compiled and stored in a {@link PreparedStatement}
     * object. This object can then be used to efficiently execute this statement multiple times.
     * <p>
     * <strong>Note:</strong> This method is optimized for handling parametric SQL statements that benefit from
     * precompilation. If the driver supports precompilation, the method {@code prepareStatement} will send the
     * statement to the database for precompilation. Some drivers may not support precompilation. In this case, the
     * statement may not be sent to the database until the {@link PreparedStatement} object is executed. This has no
     * direct effect on users; however, it does affect which methods throw certain {@link SQLException} objects.
     * <p>
     * Result sets created using the returned {@link PreparedStatement} object will by default be type
     * {@code TYPE_FORWARD_ONLY} and have a concurrency level of {@code CONCUR_READ_ONLY}. The holdability of the
     * created result sets can be determined by calling {@link #getHoldability()}.
     *
     * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
     * @return a new default {@link PreparedStatement} object containing the pre-compiled SQL statement
     * @throws SQLException if a database access error occurs or this method is called on a closed connection
     */
    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    /**
     * Creates a {@link CallableStatement} object for calling database stored procedures. The {@link CallableStatement}
     * object provides methods for setting up its IN and OUT parameters, and methods for executing the call to a stored
     * procedure.
     * <p>
     * <strong>Note:</strong> This method is optimized for handling stored procedure call statements. Some drivers may
     * send the call statement to the database when the method {@code prepareCall} is done; others may wait until the
     * {@link CallableStatement} object is executed. This has no direct effect on users; however, it does affect which
     * method throws certain {@code SQLExceptions}.
     * <p>
     * Result sets created using the returned {@link CallableStatement} object will by default be type
     * {@code TYPE_FORWARD_ONLY} and have a concurrency level of {@code CONCUR_READ_ONLY}. The holdability of the
     * created result sets can be determined by calling {@link #getHoldability()}.
     *
     * @param sql an SQL statement that may contain one or more '?' parameter placeholders. Typically this statement is
     *            specified using JDBC call escape syntax
     * @return a new default {@link CallableStatement} object containing the pre-compiled SQL statement
     * @throws SQLException if a database access error occurs or this method is called on a closed connection
     */
    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return connection.prepareCall(sql);
    }

    /**
     * Converts the given SQL statement into the system's native SQL grammar. A driver may convert the JDBC SQL grammar
     * into its system's native SQL grammar prior to sending it. This method returns the native form of the statement
     * that the driver would have sent.
     *
     * @param sql an SQL statement that may contain one or more '?' parameter placeholders
     * @return the native form of this statement
     * @throws SQLException if a database access error occurs or this method is called on a closed connection
     */
    @Override
    public String nativeSQL(String sql) throws SQLException {
        return connection.nativeSQL(sql);
    }

    /**
     * Sets this connection's auto-commit mode to the given state. If a connection is in auto-commit mode, then all its
     * SQL statements will be executed and committed as individual transactions. Otherwise, its SQL statements are
     * grouped into transactions that are terminated by a call to either the method commit or the method rollback. By
     * default, new connections are in auto-commit mode.
     * <p>
     * The commit occurs when the statement completes. The time when the statement completes depends on the type of SQL
     * Statement:
     * <ul>
     * <li>For DML statements, such as Insert, Update or Delete, and DDL statements, the statement is complete as soon
     *     as it has finished executing.
     * <li>For Select statements, the statement is complete when the associated result set is closed.
     * <li>For CallableStatement objects or for statements that return multiple results, the statement is complete when
     *     all of the associated result sets have been closed, and all update counts and output parameters have been
     *     retrieved.
     * </ul><p>
     * <strong>NOTE:</strong> If this method is called during a transaction and the auto-commit mode is changed, the
     * transaction is committed. If {@code setAutoCommit} is called and the auto-commit mode is not changed, the call is
     * a no-op.
     *
     * @param autoCommit {@code true} to enable auto-commit mode; {@code false} to disable it
     * @throws SQLException if a database access error occurs, setAutoCommit(true) is called while participating in a
     *                      distributed transaction, or this method is called on a closed connection
     */
    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        connection.setAutoCommit(autoCommit);
    }

    /**
     * Retrieves the current auto-commit mode for this {@link Connection} object.
     *
     * @return the current state of this {@link Connection} object's auto-commit mode
     * @throws SQLException if a database access error occurs or this method is called on a closed connection
     */
    @Override
    public boolean getAutoCommit() throws SQLException {
        return connection.getAutoCommit();
    }

    /**
     * Makes all changes made since the previous commit/rollback permanent and releases any database locks currently
     * held by this {@link Connection} object. This method should be used only when auto-commit mode has been disabled.
     *
     * @throws SQLException if a database access error occurs, this method is called while participating in a
     *                      distributed transaction, if this method is called on a closed connection or this
     *                      {@link Connection} object is in auto-commit mode
     */
    @Override
    public void commit() throws SQLException {
        connection.commit();
    }

    /**
     * Undoes all changes made in the current transaction and releases any database locks currently held by this
     * {@link Connection} object. This method should be used only when auto-commit mode has been disabled.
     *
     * @throws SQLException if a database access error occurs, this method is called while participating in a
     *                      distributed transaction, this method is called on a closed connection or this
     *                      {@link Connection} object is in auto-commit mode
     */
    @Override
    public void rollback() throws SQLException {
        connection.rollback();
    }

    /**
     * Wrapped connection cannot be closed. This method is a no-op.
     */
    @Override
    public void close() {
        //no-op
    }

    /**
     * Wrapped connection cannot be closed. This method always returns {@code false}.
     *
     * @return {@code false}
     */
    @Override
    public boolean isClosed() {
        return false;
    }

    /**
     * Retrieves a {@link DatabaseMetaData} object that contains metadata about the database to which this
     * {@link Connection} object represents a connection. The metadata includes information about the database's tables,
     * its supported SQL grammar, its stored procedures, the capabilities of this connection, and so on.
     *
     * @return a {@link DatabaseMetaData} object for this {@link Connection} object
     * @throws SQLException if a database access error occurs or this method is called on a closed connection
     */
    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return connection.getMetaData();
    }

    /**
     * Puts this connection in read-only mode as a hint to the driver to enable database optimizations.
     * <p>
     * <strong>Note:</strong> This method cannot be called during a transaction.
     *
     * @param readOnly {@code true} enables read-only mode; {@code false} disables it
     * @throws SQLException if a database access error occurs, this method is called on a closed connection or this
     *                      method is called during a transaction
     */
    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        connection.setReadOnly(readOnly);
    }

    /**
     * Retrieves whether this {@link Connection} object is in read-only mode.
     *
     * @return {@code true} if this {@link Connection} object is read-only; {@code false} otherwise
     * @throws SQLException if a database access error occurs or this method is called on a closed connection
     */
    @Override
    public boolean isReadOnly() throws SQLException {
        return connection.isReadOnly();
    }

    /**
     * Sets the given catalog name in order to select a subspace of this {@link Connection} object's database in which
     * to work.
     * <p>
     * If the driver does not support catalogs, it will silently ignore this request.
     * <p>
     * Calling {@code setCatalog} has no effect on previously created or prepared {@link Statement} objects. It is
     * implementation defined whether a DBMS prepare operation takes place immediately when the {@link Connection}
     * method {@code prepareStatement} or {@code prepareCall} is invoked. For maximum portability, {@code setCatalog}
     * should be called before a {@link Statement} is created or prepared.
     *
     * @param catalog the name of a catalog (subspace in this {@link Connection} object's database) in which to work
     * @throws SQLException if a database access error occurs or this method is called on a closed connection
     */
    @Override
    public void setCatalog(String catalog) throws SQLException {
        connection.setCatalog(catalog);
    }

    /**
     * Retrieves this {@link Connection} object's current catalog name.
     *
     * @return the current catalog name or {@code null} if there is none
     * @throws SQLException if a database access error occurs or this method is called on a closed connection
     */
    @Override
    public String getCatalog() throws SQLException {
        return connection.getCatalog();
    }

    /**
     * Attempts to change the transaction isolation level for this {@link Connection} object to the one given. The
     * constants defined in the interface {@link Connection} are the possible transaction isolation levels.
     * <p>
     * <strong>Note:</strong> If this method is called during a transaction, the result is implementation-defined.
     *
     * @param level one of the following {@link Connection} constants: {@link Connection#TRANSACTION_READ_UNCOMMITTED},
     *              {@link Connection#TRANSACTION_READ_COMMITTED}, {@link Connection#TRANSACTION_REPEATABLE_READ}, or
     *              {@link Connection#TRANSACTION_SERIALIZABLE}. (Note that {@link Connection#TRANSACTION_NONE} cannot
     *              be used because it specifies that transactions are not supported.)
     * @throws SQLException if a database access error occurs, this method is called on a closed connection or the given
     *                      parameter is not one of the {@link Connection} constants
     */
    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        connection.setTransactionIsolation(level);
    }

    /**
     * Retrieves this {@link Connection} object's current transaction isolation level.
     *
     * @return the current transaction isolation level, which will be one of the following constants:
     *         {@link Connection#TRANSACTION_READ_UNCOMMITTED}, {@link Connection#TRANSACTION_READ_COMMITTED},
     *         {@link Connection#TRANSACTION_REPEATABLE_READ}, {@link Connection#TRANSACTION_SERIALIZABLE}, or
     *         {@link Connection#TRANSACTION_NONE}.
     * @throws SQLException if a database access error occurs or this method is called on a closed connection
     */
    @Override
    public int getTransactionIsolation() throws SQLException {
        return connection.getTransactionIsolation();
    }

    /**
     * Retrieves the first warning reported by calls on this {@link Connection} object. If there is more than one
     * warning, subsequent warnings will be chained to the first one and can be retrieved by calling the method
     * {@link SQLWarning#getNextWarning} on the warning that was retrieved previously.
     * <p>
     * This method may not be called on a closed connection; doing so will cause an {@link SQLException} to be thrown.
     * <p>
     * <strong>Note:</strong> Subsequent warnings will be chained to this {@link SQLWarning}.
     *
     * @return the first {@link SQLWarning} object or {@code null} if there are none
     * @throws SQLException if a database access error occurs or this method is called on a closed connection
     */
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return connection.getWarnings();
    }

    /**
     * Clears all warnings reported for this {@link Connection} object. After a call to this method, the method
     * {@link #getWarnings} returns null until a new warning is reported for this {@link Connection} object.
     *
     * @throws SQLException if a database access error occurs or this method is called on a closed connection
     */
    @Override
    public void clearWarnings() throws SQLException {
        connection.clearWarnings();
    }

    /**
     * Creates a {@link Statement} object that will generate {@link ResultSet} objects with the given type and
     * concurrency. This method is the same as the {@code createStatement} method above, but it allows the default
     * result set type and concurrency to be overridden. The holdability of the created result sets can be determined by
     * calling {@link #getHoldability()}.
     *
     * @param resultSetType a result set type; one of {@link ResultSet#TYPE_FORWARD_ONLY},
     *                      {@link ResultSet#TYPE_SCROLL_INSENSITIVE}, or {@link ResultSet#TYPE_SCROLL_SENSITIVE}
     * @param resultSetConcurrency a concurrency type; one of {@link ResultSet#CONCUR_READ_ONLY} or
     *                             {@link ResultSet#CONCUR_UPDATABLE}
     * @return a new {@link Statement} object that will generate {@link ResultSet} objects with the given type and
     *         concurrency
     * @throws SQLException if a database access error occurs, this method is called on a closed connection or the given
     *                      parameters are not {@link ResultSet} constants indicating type and concurrency
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method or this method is not
     *                                         supported for the specified result set type and result set concurrency
     */
    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return connection.createStatement(resultSetType, resultSetConcurrency);
    }

    /**
     * Creates a {@link PreparedStatement} object that will generate {@link ResultSet} objects with the given type and
     * concurrency. This method is the same as the {@code prepareStatement} method above, but it allows the default
     * result set type and concurrency to be overridden. The holdability of the created result sets can be determined by
     * calling {@link #getHoldability()}.
     *
     * @param sql a {@link String} object that is the SQL statement to be sent to the database; may contain one or more
     *            '?' IN parameters
     * @param resultSetType a result set type; one of {@link ResultSet#TYPE_FORWARD_ONLY},
     *                      {@link ResultSet#TYPE_SCROLL_INSENSITIVE}, or {@link ResultSet#TYPE_SCROLL_SENSITIVE}
     * @param resultSetConcurrency a concurrency type; one of {@link ResultSet#CONCUR_READ_ONLY} or
     *                             {@link ResultSet#CONCUR_UPDATABLE}
     * @return a new {@link PreparedStatement} object containing the pre-compiled SQL statement that will produce
     *         {@link ResultSet} objects with the given type and concurrency
     * @throws SQLException if a database access error occurs, this method is called on a closed connection or the given
     *                      parameters are not {@link ResultSet} constants indicating type and concurrency
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method or this method is not
     *                                         supported for the specified result set type and result set concurrency
     */
    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    /**
     * Creates a {@link CallableStatement} object that will generate {@link ResultSet} objects with the given type and
     * concurrency. This method is the same as the {@code prepareCall} method above, but it allows the default result
     * set type and concurrency to be overridden. The holdability of the created result sets can be determined by
     * calling {@link #getHoldability()}.
     *
     * @param sql a {@link String} object that is the SQL statement to be sent to the database; may contain one or more
     *            '?' IN parameters
     * @param resultSetType a result set type; one of {@link ResultSet#TYPE_FORWARD_ONLY},
     *                      {@link ResultSet#TYPE_SCROLL_INSENSITIVE}, or {@link ResultSet#TYPE_SCROLL_SENSITIVE}
     * @param resultSetConcurrency a concurrency type; one of {@link ResultSet#CONCUR_READ_ONLY} or
     *                             {@link ResultSet#CONCUR_UPDATABLE}
     * @return a new {@link CallableStatement} object containing the pre-compiled SQL statement that will produce
     *         {@link ResultSet} objects with the given type and concurrency
     * @throws SQLException if a database access error occurs, this method is called on a closed connection or the given
     *                      parameters are not {@link ResultSet} constants indicating type and concurrency
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method or this method is not
     *                                         supported for the specified result set type and result set concurrency
     */
    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return connection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    /**
     * Retrieves the {@link Map} object associated with this {@link Connection} object. Unless the application has added
     * an entry, the type map returned will be empty.
     * <p>
     * You must invoke setTypeMap after making changes to the {@link Map} object returned from {@code getTypeMap} as a
     * JDBC driver may create an internal copy of the {@link Map} object passed to {@code setTypeMap}:
     * <pre>{@code
     *       Map<String,Class<?>> myMap = con.getTypeMap();
     *       myMap.put("mySchemaName.ATHLETES", Athletes.class);
     *       con.setTypeMap(myMap);
     * }</pre>
     *
     * @return the {@link Map} object associated with this {@link Connection} object
     * @throws SQLException if a database access error occurs or this method is called on a closed connection
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
     */
    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return connection.getTypeMap();
    }

    /**
     * Installs the given {@code TypeMap} object as the type map for this {@link Connection} object. The type map will
     * be used for the custom mapping of SQL structured types and distinct types.
     * <p>
     * You must set the the values for the {@code TypeMap} prior to calling {@code setMap} as a JDBC driver may create
     * an internal copy of the {@code TypeMap}:
     * <pre>{@code
     *       Map<String,Class<?>> myMap = con.getTypeMap();
     *       myMap.put("mySchemaName.ATHLETES", Athletes.class);
     *       con.setTypeMap(myMap);
     * }</pre>
     *
     * @param map the {@link Map} object to install as the replacement for this {@link Connection} object's default type
     *            map
     * @throws SQLException if a database access error occurs, this method is called on a closed connection or the given
     *                      parameter is not a {@link Map} object
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
     */
    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        connection.setTypeMap(map);
    }

    /**
     * Changes the default holdability of {@link ResultSet} objects created using this {@link Connection} object to the
     * given holdability. The default holdability of {@link ResultSet} objects can be be determined by invoking
     * {@link DatabaseMetaData#getResultSetHoldability()}.
     *
     * @param holdability a {@link ResultSet} holdability constant; one of {@link ResultSet#HOLD_CURSORS_OVER_COMMIT} or
     *                    {@link ResultSet#CLOSE_CURSORS_AT_COMMIT}
     * @throws SQLException if a database access occurs, this method is called on a closed connection, or the given
     *                      parameter is not a {@link ResultSet} constant indicating holdability
     * @throws SQLFeatureNotSupportedException if the given holdability is not supported
     */
    @Override
    public void setHoldability(int holdability) throws SQLException {
        connection.setHoldability(holdability);
    }

    /**
     * Retrieves the current holdability of {@link ResultSet} objects created using this Connection object.
     *
     * @return the holdability, one of {@link ResultSet#HOLD_CURSORS_OVER_COMMIT} or
     *         {@link ResultSet#CLOSE_CURSORS_AT_COMMIT}
     * @throws SQLException if a database access error occurs or this method is called on a closed connection
     */
    @Override
    public int getHoldability() throws SQLException {
        return connection.getHoldability();
    }

    /**
     * Creates an unnamed savepoint in the current transaction and returns the new {@link Savepoint} object that
     * represents it.
     * <p>
     * if {@code setSavepoint} is invoked outside of an active transaction, a transaction will be started at this newly
     * created savepoint.
     *
     * @return the new {@link Savepoint} object
     * @throws SQLException if a database access error occurs, this method is called while participating in a
     *                      distributed transaction, this method is called on a closed connection or this
     *                      {@link Connection} object is currently in auto-commit mode
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
     */
    @Override
    public Savepoint setSavepoint() throws SQLException {
        return connection.setSavepoint();
    }

    /**
     * Creates a savepoint with the given name in the current transaction and returns the new {@link Savepoint} object
     * that represents it.
     * <p>
     * if {@code setSavepoint} is invoked outside of an active transaction, a transaction will be started at this newly
     * created savepoint.
     *
     * @param name a {@link String} containing the name of the savepoint
     * @return the new {@link Savepoint} object
     * @throws SQLException if a database access error occurs, this method is called while participating in a
     *                      distributed transaction, this method is called on a closed connection or this
     *                      {@link Connection} object is currently in auto-commit mode
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
     */
    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return connection.setSavepoint(name);
    }

    /**
     * Undoes all changes made after the given {@link Savepoint} object was set.
     * <p>
     * This method should be used only when auto-commit has been disabled.
     *
     * @param savepoint the {@link Savepoint} object to roll back to
     * @throws SQLException if a database access error occurs, this method is called while participating in a
     *                      distributed transaction, this method is called on a closed connection, the {@link Savepoint}
     *                      object is no longer valid, or this {@link Connection} object is currently in auto-commit
     *                      mode
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
     */
    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        connection.rollback(savepoint);
    }

    /**
     * Removes the specified {@link Savepoint} and subsequent {@link Savepoint} objects from the current transaction.
     * Any reference to the savepoint after it have been removed will cause an {@link SQLException} to be thrown.
     *
     * @param savepoint the {@link Savepoint} object to be removed
     * @throws SQLException if a database access error occurs, this method is called on a closed connection or the given
     *                      {@link Savepoint} object is not a valid savepoint in the current transaction
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
     */
    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        connection.releaseSavepoint(savepoint);
    }

    /**
     * Creates a {@link Statement} object that will generate {@link ResultSet} objects with the given type, concurrency,
     * and holdability. This method is the same as the {@code createStatement} method above, but it allows the default
     * result set type, concurrency, and holdability to be overridden.
     *
     * @param resultSetType one of the following {@link ResultSet} constants: {@link ResultSet#TYPE_FORWARD_ONLY},
     *                      {@link ResultSet#TYPE_SCROLL_INSENSITIVE}, or {@link ResultSet#TYPE_SCROLL_SENSITIVE}
     * @param resultSetConcurrency one of the following {@link ResultSet} constants: {@link ResultSet#CONCUR_READ_ONLY}
     *                             or {@link ResultSet#CONCUR_UPDATABLE}
     * @param resultSetHoldability one of the following {@link ResultSet} constants:
     *                             {@link ResultSet#HOLD_CURSORS_OVER_COMMIT} or
     *                             {@link ResultSet#CLOSE_CURSORS_AT_COMMIT}
     * @return a new {@link Statement} object that will generate {@link ResultSet} objects with the given type,
     *         concurrency, and holdability
     * @throws SQLException if a database access error occurs, this method is called on a closed connection or the given
     *                      parameters are not {@link ResultSet} constants indicating type, concurrency, and holdability
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method or this method is not
     *                                         supported for the specified result set type, result set holdability and
     *                                         result set concurrency
     */
    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        return connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    /**
     * Creates a {@link PreparedStatement} object that will generate {@link ResultSet} objects with the given type,
     * concurrency, and holdability.
     * <p>
     * This method is the same as the {@code prepareStatement} method above, but it allows the default result set type,
     * concurrency, and holdability to be overridden.
     *
     * @param sql a {@link String} object that is the SQL statement to be sent to the database; may contain one or more
     *            '?' IN parameters
     * @param resultSetType one of the following {@link ResultSet} constants: {@link ResultSet#TYPE_FORWARD_ONLY},
     *                      {@link ResultSet#TYPE_SCROLL_INSENSITIVE}, or {@link ResultSet#TYPE_SCROLL_SENSITIVE}
     * @param resultSetConcurrency one of the following {@link ResultSet} constants: {@link ResultSet#CONCUR_READ_ONLY}
     *                             or {@link ResultSet#CONCUR_UPDATABLE}
     * @param resultSetHoldability one of the following {@link ResultSet} constants:
     *                             {@link ResultSet#HOLD_CURSORS_OVER_COMMIT} or
     *                             {@link ResultSet#CLOSE_CURSORS_AT_COMMIT}
     * @return a new {@link PreparedStatement} object, containing the pre-compiled SQL statement, that will generate
     *         {@link ResultSet} objects with the given type, concurrency, and holdability
     * @throws SQLException if a database access error occurs, this method is called on a closed connection or the given
     *                      parameters are not {@link ResultSet} constants indicating type, concurrency, and holdability
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method or this method is not
     *                                         supported for the specified result set type, result set holdability and
     *                                         result set concurrency
     */
    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
                                              int resultSetHoldability) throws SQLException {
        return connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    /**
     * Creates a {@link CallableStatement} object that will generate {@link ResultSet} objects with the given type and
     * concurrency. This method is the same as the {@code prepareCall} method above, but it allows the default result
     * set type, result set concurrency type and holdability to be overridden.
     *
     * @param sql a {@link String} object that is the SQL statement to be sent to the database; may contain one or more
     *            '?' IN parameters
     * @param resultSetType one of the following {@link ResultSet} constants: {@link ResultSet#TYPE_FORWARD_ONLY},
     *                      {@link ResultSet#TYPE_SCROLL_INSENSITIVE}, or {@link ResultSet#TYPE_SCROLL_SENSITIVE}
     * @param resultSetConcurrency one of the following {@link ResultSet} constants: {@link ResultSet#CONCUR_READ_ONLY}
     *                             or {@link ResultSet#CONCUR_UPDATABLE}
     * @param resultSetHoldability one of the following {@link ResultSet} constants:
     *                             {@link ResultSet#HOLD_CURSORS_OVER_COMMIT} or
     *                             {@link ResultSet#CLOSE_CURSORS_AT_COMMIT}
     * @return a new {@link CallableStatement} object, containing the pre-compiled SQL statement, that will generate
     *         {@link ResultSet} objects with the given type, concurrency, and holdability
     * @throws SQLException if a database access error occurs, this method is called on a closed connection or the given
     *                      parameters are not {@link ResultSet} constants indicating type, concurrency, and holdability
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method or this method is not
     *                                         supported for the specified result set type, result set holdability and
     *                                         result set concurrency
     */
    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
                                         int resultSetHoldability) throws SQLException {
        return connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    /**
     * Creates a default {@link PreparedStatement} object that has the capability to retrieve auto-generated keys. The
     * given constant tells the driver whether it should make auto-generated keys available for retrieval. This
     * parameter is ignored if the SQL statement is not an {@code INSERT} statement, or an SQL statement able to return
     * auto-generated keys (the list of such statements is vendor-specific).
     * <p>
     * <strong>Note:</strong> This method is optimized for handling parametric SQL statements that benefit from
     * precompilation. If the driver supports precompilation, the method {@code prepareStatement} will send the
     * statement to the database for precompilation. Some drivers may not support precompilation. In this case, the
     * statement may not be sent to the database until the {@link PreparedStatement} object is executed. This has no
     * direct effect on users; however, it does affect which methods throw certain {@code SQLExceptions}.
     * <p>
     * Result sets created using the returned {@link PreparedStatement} object will by default be type
     * {@code TYPE_FORWARD_ONLY} and have a concurrency level of {@code CONCUR_READ_ONLY}. The holdability of the
     * created result sets can be determined by calling {@link #getHoldability()}.
     *
     * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
     * @param autoGeneratedKeys a flag indicating whether auto-generated keys should be returned; one of
     *                          {@link Statement#RETURN_GENERATED_KEYS} or {@link Statement#NO_GENERATED_KEYS}
     * @return a new {@link PreparedStatement} object, containing the pre-compiled SQL statement, that will have the
     *         capability of returning auto-generated keys
     * @throws SQLException if a database access error occurs, this method is called on a closed connection or the given
     *                      parameter is not a {@link Statement} constant indicating whether auto-generated keys should
     *                      be returned
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method with a constant of
     *                                         {@link Statement#RETURN_GENERATED_KEYS}
     */
    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return connection.prepareStatement(sql, autoGeneratedKeys);
    }

    /**
     * Creates a default {@link PreparedStatement} object capable of returning the auto-generated keys designated by the
     * given array. This array contains the indexes of the columns in the target table that contain the auto-generated
     * keys that should be made available. The driver will ignore the array if the SQL statement is not an
     * {@code INSERT} statement, or an SQL statement able to return auto-generated keys (the list of such statements is
     * vendor-specific).
     * <p>
     * An SQL statement with or without IN parameters can be pre-compiled and stored in a {@link PreparedStatement}
     * object. This object can then be used to efficiently execute this statement multiple times.
     * <p>
     * <strong>Note:</strong> This method is optimized for handling parametric SQL statements that benefit from
     * precompilation. If the driver supports precompilation, the method {@code prepareStatement} will send the
     * statement to the database for precompilation. Some drivers may not support precompilation. In this case, the
     * statement may not be sent to the database until the {@link PreparedStatement} object is executed. This has no
     * direct effect on users; however, it does affect which methods throw certain {@code SQLExceptions}.
     * <p>
     * Result sets created using the returned {@link PreparedStatement} object will by default be type
     * {@code TYPE_FORWARD_ONLY} and have a concurrency level of {@code CONCUR_READ_ONLY}. The holdability of the
     * created result sets can be determined by calling {@link #getHoldability()}.
     *
     * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
     * @param columnIndexes an array of column indexes indicating the columns that should be returned from the inserted
     *                      row or rows
     * @return a new {@link PreparedStatement} object, containing the pre-compiled statement, that is capable of
     *         returning the auto-generated keys designated by the given array of column indexes
     * @throws SQLException if a database access error occurs or this method is called on a closed connection
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
     */
    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return connection.prepareStatement(sql, columnIndexes);
    }

    /**
     * Creates a default {@link PreparedStatement} object capable of returning the auto-generated keys designated by the
     * given array. This array contains the names of the columns in the target table that contain the auto-generated
     * keys that should be returned. The driver will ignore the array if the SQL statement is not an {@code INSERT}
     * statement, or an SQL statement able to return auto-generated keys (the list of such statements is
     * vendor-specific).
     * <p>
     * An SQL statement with or without IN parameters can be pre-compiled and stored in a {@link PreparedStatement}
     * object. This object can then be used to efficiently execute this statement multiple times.
     * <p>
     * <strong>Note:</strong> This method is optimized for handling parametric SQL statements that benefit from
     * precompilation. If the driver supports precompilation, the method {@code prepareStatement} will send the
     * statement to the database for precompilation. Some drivers may not support precompilation. In this case, the
     * statement may not be sent to the database until the {@link PreparedStatement} object is executed. This has no
     * direct effect on users; however, it does affect which methods throw certain {@code SQLExceptions}.
     * <p>
     * Result sets created using the returned {@link PreparedStatement} object will by default be type
     * {@code TYPE_FORWARD_ONLY} and have a concurrency level of {@code CONCUR_READ_ONLY}. The holdability of the
     * created result sets can be determined by calling {@link #getHoldability()}.
     *
     * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
     * @param columnNames an array of column names indicating the columns that should be returned from the inserted row
     *                    or rows
     * @return a new {@link PreparedStatement} object, containing the pre-compiled statement, that is capable of
     *         returning the auto-generated keys designated by the given array of column names
     * @throws SQLException if a database access error occurs or this method is called on a closed connection
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
     */
    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return connection.prepareStatement(sql, columnNames);
    }

    /**
     * Constructs an object that implements the {@link Clob} interface. The object returned initially contains no data.
     * The {@code setAsciiStream}, {@code setCharacterStream} and {@code setString} methods of the {@link Clob}
     * interface may be used to add data to the {@link Clob}.
     *
     * @return An object that implements the {@link Clob} interface
     * @throws SQLException if an object that implements the {@link Clob} interface can not be constructed, this method
     *                      is called on a closed connection or a database access error occurs
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this data type
     */
    @Override
    public Clob createClob() throws SQLException {
        return connection.createClob();
    }

    /**
     * Constructs an object that implements the {@link Blob} interface. The object returned initially contains no data.
     * The {@code setBinaryStream} and {@code setBytes} methods of the {@link Blob} interface may be used to add data to
     * the {@link Blob}.
     *
     * @return An object that implements the {@link Blob} interface
     * @throws SQLException if an object that implements the {@link Blob} interface can not be constructed, this method
     *                      is called on a closed connection or a database access error occurs
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this data type
     */
    @Override
    public Blob createBlob() throws SQLException {
        return connection.createBlob();
    }

    /**
     * Constructs an object that implements the {@link NClob} interface. The object returned initially contains no data.
     * The {@code setAsciiStream}, {@code setCharacterStream} and {@code setString} methods of the {@link NClob}
     * interface may be used to add data to the {@link NClob}.
     *
     * @return An object that implements the {@link NClob} interface
     * @throws SQLException if an object that implements the {@link NClob} interface can not be constructed, this method
     *                      is called on a closed connection or a database access error occurs
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this data type
     */
    @Override
    public NClob createNClob() throws SQLException {
        return connection.createNClob();
    }

    /**
     * Constructs an object that implements the {@link SQLXML} interface. The object returned initially contains no
     * data. The {@code createXmlStreamWriter} object and {@code setString} method of the {@link SQLXML} interface may
     * be used to add data to the {@link SQLXML} object.
     *
     * @return An object that implements the {@link SQLXML} interface
     * @throws SQLException if an object that implements the {@link SQLXML} interface can not be constructed, this\
     *                      method is called on a closed connection or a database access error occurs
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this data type
     */
    @Override
    public SQLXML createSQLXML() throws SQLException {
        return connection.createSQLXML();
    }

    /**
     * Returns {@code true} if the connection has not been closed and is still valid. The driver shall submit a query on
     * the connection or use some other mechanism that positively verifies the connection is still valid when this
     * method is called.
     * <p>
     * The query submitted by the driver to validate the connection shall be executed in the context of the current
     * transaction.
     *
     * @param timeout The time in seconds to wait for the database operation used to validate the connection to
     *                complete. If the timeout period expires before the operation completes, this method returns
     *                {@code false}. A value of 0 indicates a timeout is not applied to the database operation
     * @return {@code true} if the connection is valid, {@code false} otherwise
     * @throws SQLException if the value supplied for {@code timeout} is less then 0
     */
    @Override
    public boolean isValid(int timeout) throws SQLException {
        return connection.isValid(timeout);
    }

    /**
     * Sets the value of the client info property specified by name to the value specified by value.
     * <p>
     * Applications may use the {@link DatabaseMetaData#getClientInfoProperties} method to determine the client info
     * properties supported by the driver and the maximum length that may be specified for each property.
     * <p>
     * The driver stores the value specified in a suitable location in the database. For example in a special register,
     * session parameter, or system table column. For efficiency the driver may defer setting the value in the database
     * until the next time a statement is executed or prepared. Other than storing the client information in the
     * appropriate place in the database, these methods shall not alter the behavior of the connection in anyway. The
     * values supplied to these methods are used for accounting, diagnostics and debugging purposes only.
     * <p>
     * The driver shall generate a warning if the client info name specified is not recognized by the driver.
     * <p>
     * If the value specified to this method is greater than the maximum length for the property the driver may either
     * truncate the value and generate a warning or generate a {@link SQLClientInfoException}. If the driver generates a
     * {@link SQLClientInfoException}, the value specified was not set on the connection.
     * <p>
     * The following are standard client info properties. Drivers are not required to support these properties however
     * if the driver supports a client info property that can be described by one of the standard properties, the
     * standard property name should be used.
     * <ul>
     * <li>ApplicationName - The name of the application currently utilizing the connection
     * <li>ClientUser - The name of the user that the application using the connection is performing work for. This may
     *     not be the same as the user name that was used in establishing the connection.
     * <li>ClientHostname - The hostname of the computer the application using the connection is running on.
     * </ul>
     *
     * @param name The name of the client info property to set
     * @param value The value to set the client info property to. If the value is null, the current value of the
     *              specified property is cleared
     * @throws SQLClientInfoException if the database server returns an error while setting the client info value on the
     *                                database server or this method is called on a closed connection
     */
    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        connection.setClientInfo(name, value);
    }

    /**
     * Sets the value of the connection's client info properties. The {@link Properties} object contains the names and
     * values of the client info properties to be set. The set of client info properties contained in the properties
     * list replaces the current set of client info properties on the connection. If a property that is currently set on
     * the connection is not present in the properties list, that property is cleared. Specifying an empty properties
     * list will clear all of the properties on the connection. See {@link #setClientInfo(String, String)} for more
     * information.
     * <p>
     * If an error occurs in setting any of the client info properties, a {@link SQLClientInfoException} is thrown. The
     * {@link SQLClientInfoException} contains information indicating which client info properties were not set. The
     * state of the client information is unknown because some databases do not allow multiple client info properties to
     * be set atomically. For those databases, one or more properties may have been set before the error occurred.
     *
     * @param properties the list of client info properties to set
     * @throws SQLClientInfoException if the database server returns an error while setting the clientInfo values on the
     *                                database server or this method is called on a closed connection
     */
    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        connection.setClientInfo(properties);
    }

    /**
     * Returns the value of the client info property specified by name. This method may return {@code null} if the
     * specified client info property has not been set and does not have a default value. This method will also return
     * {@code null} if the specified client info property name is not supported by the driver.
     * <p>
     * Applications may use the {@link DatabaseMetaData#getClientInfoProperties} method to determine the client info
     * properties supported by the driver.
     *
     * @param name The name of the client info property to retrieve
     * @return The value of the client info property specified
     * @throws SQLException if the database server returns an error when fetching the client info value from the
     *                      database or this method is called on a closed connection
     */
    @Override
    public String getClientInfo(String name) throws SQLException {
        return connection.getClientInfo(name);
    }

    /**
     * Returns a list containing the name and current value of each client info property supported by the driver. The
     * value of a client info property may be null if the property has not been set and does not have a default value.
     *
     * @return A {@link Properties} object that contains the name and current value of each of the client info
     *         properties supported by the driver
     * @throws SQLException if the database server returns an error when fetching the client info values from the
     *                      database or this method is called on a closed connection
     */
    @Override
    public Properties getClientInfo() throws SQLException {
        return connection.getClientInfo();
    }

    /**
     * Factory method for creating {@link Array} objects.
     * <p>
     * <strong>Note:</strong> When {@code createArrayOf} is used to create an array object that maps to a primitive data
     * type, then it is implementation-defined whether the {@link Array} object is an array of that primitive data type
     * or an array of {@link Object}.
     * <p>
     * <strong>Note:</strong> The JDBC driver is responsible for mapping the elements {@link Object} array to the
     * default JDBC SQL type defined in {@link Types} for the given class of {@link Object}. The default mapping is
     * specified in Appendix B of the JDBC specification. If the resulting JDBC type is not the appropriate type for the
     * given {@code typeName} then it is implementation defined whether an {@link SQLException} is thrown or the driver
     * supports the resulting conversion.
     *
     * @param typeName the SQL name of the type the elements of the array map to. The {@code typeName} is a
     *                 database-specific name which may be the name of a built-in type, a user-defined type or a
     *                 standard SQL type supported by this database. This is the value returned by
     *                 {@link Array#getBaseTypeName}
     * @param elements the elements that populate the returned object
     * @return an {@link Array} object whose elements map to the specified SQL type
     * @throws SQLException if a database error occurs, the JDBC type is not appropriate for the {@code typeName} and
     *                      the conversion is not supported, the {@code typeName} is {@code null} or this method is
     *                      called on a closed connection
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this data type
     */
    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return connection.createArrayOf(typeName, elements);
    }

    /**
     * Factory method for creating {@link Struct} objects.
     *
     * @param typeName the SQL type name of the SQL structured type that this {@link Struct} object maps to. The
     *                 {@code typeName} is the name of a user-defined type that has been defined for this database. It
     *                 is the value returned by {@link Struct#getSQLTypeName}
     * @param attributes the attributes that populate the returned object
     * @return a {@link Struct} object that maps to the given SQL type and is populated with the given attributes
     * @throws SQLException if a database error occurs, the {@code typeName} is {@code null} or this method is called on
     *                      a closed connection
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this data type
     */
    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return connection.createStruct(typeName, attributes);
    }

    /**
     * Sets the given schema name to access.
     * <p>
     * If the driver does not support schemas, it will silently ignore this request.
     * <p>
     * Calling {@code setSchema} has no effect on previously created or prepared {@link Statement} objects. It is
     * implementation defined whether a DBMS prepare operation takes place immediately when the {@link Connection}
     * method {@code prepareStatement} or {@code prepareCall} is invoked. For maximum portability, {@code setSchema}
     * should be called before a {@link Statement} is created or prepared.
     *
     * @param schema the name of a schema in which to work
     * @throws SQLException if a database access error occurs or this method is called on a closed connection
     */
    @Override
    public void setSchema(String schema) throws SQLException {
        connection.setSchema(schema);
    }

    /**
     * Retrieves this {@link Connection} object's current schema name.
     *
     * @return the current schema name or {@code null} if there is none
     * @throws SQLException if a database access error occurs or this method is called on a closed connection
     */
    @Override
    public String getSchema() throws SQLException {
        return connection.getSchema();
    }

    /**
     * Terminates an open connection. Calling abort results in:
     * <ul>
     * <li>The connection marked as closed
     * <li>Closes any physical connection to the database
     * <li>Releases resources used by the connection
     * <li>Insures that any thread that is currently accessing the connection will either progress to completion or
     *     throw an {@link SQLException}.
     * </ul><p>
     * Calling abort marks the connection closed and releases any resources. Calling abort on a closed connection is a
     * no-op.
     * <p>
     * It is possible that the aborting and releasing of the resources that are held by the connection can take an
     * extended period of time. When the abort method returns, the connection will have been marked as closed and the
     * {@link Executor} that was passed as a parameter to abort may still be executing tasks to release resources.
     * <p>
     * This method checks to see that there is an {@link SQLPermission} object before allowing the method to proceed. If
     * a {@link SecurityManager} exists and its {@code checkPermission} method denies calling abort, this method throws
     * a {@link SecurityException}.
     *
     * @param executor The {@link Executor} implementation which will be used by {@code abort}
     * @throws SQLException if a database access error occurs or the executor is {@code null}
     * @throws SecurityException if a security manager exists and its {@code checkPermission} method denies calling
     *                           abort
     */
    @Override
    public void abort(Executor executor) throws SQLException {
        connection.abort(executor);
    }

    /**
     * Sets the maximum period a {@link Connection} or objects created from the {@link Connection} will wait for the
     * database to reply to any one request. If any request remains unanswered, the waiting method will return with a
     * {@link SQLException}, and the {@link Connection} or objects created from the {@link Connection} will be marked as
     * closed. Any subsequent use of the objects, with the exception of the {@link #close}, {@link #isClosed} or
     * {@link Connection#isValid} methods, will result in a {@link SQLException}.
     * <p>
     * <strong>Note:</strong> This method is intended to address a rare but serious condition where network partitions
     * can cause threads issuing JDBC calls to hang uninterruptedly in socket reads, until the OS {@code TCP-TIMEOUT}
     * (typically 10 minutes). This method is related to the {@code abort()} method which provides an administrator
     * thread a means to free any such threads in cases where the JDBC connection is accessible to the administrator
     * thread. The {@code setNetworkTimeout} method will cover cases where there is no administrator thread, or it has
     * no access to the connection. This method is severe in it's effects, and should be given a high enough value so it
     * is never triggered before any more normal timeouts, such as transaction timeouts.
     * <p>
     * JDBC driver implementations may also choose to support the {@code setNetworkTimeout} method to impose a limit on
     * database response time, in environments where no network is present.
     * <p>
     * Drivers may internally implement some or all of their API calls with multiple internal driver-database
     * transmissions, and it is left to the driver implementation to determine whether the limit will be applied always
     * to the response to the API call, or to any single request made during the API call.
     * <p>
     * This method can be invoked more than once, such as to set a limit for an area of JDBC code, and to reset to the
     * default on exit from this area. Invocation of this method has no impact on already outstanding requests.
     * <p>
     * The {@link Statement#setQueryTimeout} timeout value is independent of the timeout value specified in
     * {@code setNetworkTimeout}. If the query timeout expires before the network timeout then the statement execution
     * will be canceled. If the network is still active the result will be that both the statement and connection are
     * still usable. However if the network timeout expires before the query timeout or if the statement timeout fails
     * due to network problems, the connection will be marked as closed, any resources held by the connection will be
     * released and both the connection and statement will be unusable.
     * <p>
     * When the driver determines that the {@code setNetworkTimeout} timeout value has expired, the JDBC driver marks
     * the connection closed and releases any resources held by the connection.
     * <p>
     * This method checks to see that there is an {@link SQLPermission} object before allowing the method to proceed. If
     * a {@link SecurityManager} exists and its {@code checkPermission} method denies calling {@code setNetworkTimeout},
     * this method throws a {@link SecurityException}.
     *
     * @param executor The {@link Executor} implementation which will be used by {@code setNetworkTimeout}
     * @param milliseconds The time in milliseconds to wait for the database operation to complete. If the JDBC driver
     *                     does not support milliseconds, the JDBC driver will round the value up to the nearest second.
     *                     If the timeout period expires before the operation completes, a {@link SQLException} will be
     *                     thrown. A value of 0 indicates that there is not timeout for database operations
     * @throws SQLException if a database access error occurs, this method is called on a closed connection, the
     *                      {@code executor} is {@code null}, or the value specified for seconds is less than 0
     * @throws SecurityException if a security manager exists and its {@code checkPermission} method denies calling
     *                           {@code setNetworkTimeout}
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
     */
    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        connection.setNetworkTimeout(executor, milliseconds);
    }

    /**
     * Retrieves the number of milliseconds the driver will wait for a database request to complete. If the limit is
     * exceeded, a {@link SQLException} is thrown.
     *
     * @return the current timeout limit in milliseconds; zero means there is no limit
     * @throws SQLException if a database access error occurs or this method is called on a closed {@link Connection}
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
     */
    @Override
    public int getNetworkTimeout() throws SQLException {
        return connection.getNetworkTimeout();
    }

    /**
     * Returns an object that implements the given interface to allow access to non-standard methods, or standard
     * methods not exposed by the proxy. If the receiver implements the interface then the result is the receiver or a
     * proxy for the receiver. If the receiver is a wrapper and the wrapped object implements the interface then the
     * result is the wrapped object or a proxy for the wrapped object. Otherwise return the the result of calling
     * {@code unwrap} recursively on the wrapped object or a proxy for that result. If the receiver is not a wrapper
     * and does not implement the interface, then an {@link SQLException} is thrown.
     *
     * @param iface A Class defining an interface that the result must implement
     * @return an object that implements the interface. May be a proxy for the actual implementing object
     * @throws SQLException If no object found that implements the interface
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(connection)) {
            return (T) connection;
        }
        return connection.unwrap(iface);
    }

    /**
     * Returns true if this either implements the interface argument or is directly or indirectly a wrapper for an
     * object that does. Returns false otherwise. If this implements the interface then return true, else if this is a
     * wrapper then return the result of recursively calling {@code isWrapperFor} on the wrapped object. If this does
     * not implement the interface and is not a wrapper, return false. This method should be implemented as a low-cost
     * operation compared to unwrap so that callers can use this method to avoid expensive unwrap calls that may fail.
     * If this method returns true then calling unwrap with the same argument should succeed.
     *
     * @param iface a Class defining an interface
     * @return true if this implements the interface or directly or indirectly wraps an object that does
     * @throws SQLException if an error occurs while determining whether this is a wrapper for an object with the given
     *                      interface
     */
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (iface.isInstance(connection)) {
            return true;
        }
        return connection.isWrapperFor(iface);
    }

    /**
     * Unwrap this connection wrapper.
     *
     * @return wrapped {@link Connection} instance
     */
    public Connection unwrap() {
        return connection;
    }
}
