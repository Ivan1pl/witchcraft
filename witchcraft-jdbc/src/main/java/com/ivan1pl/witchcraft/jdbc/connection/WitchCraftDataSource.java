package com.ivan1pl.witchcraft.jdbc.connection;

import com.ivan1pl.witchcraft.context.annotations.ConfigurationValues;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import com.ivan1pl.witchcraft.jdbc.exception.InitializationException;
import org.apache.commons.dbcp2.BasicDataSourceFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * WitchCraft's managed {@link DataSource} wrapper.
 */
@Managed
public class WitchCraftDataSource implements DataSource {
    private final DataSource dataSource;

    /**
     * Constructor.
     * @param properties data source configuration properties
     * @throws InitializationException when data source initialization fails
     */
    public WitchCraftDataSource(
            @ConfigurationValues(
                    prefix = "witchcraft.jdbc",
                    keys = {"defaultAutoCommit", "defaultReadOnly", "defaultTransactionIsolation", "defaultCatalog",
                            "defaultSchema", "cacheState", "driverClassName", "lifo", "maxTotal", "maxIdle", "minIdle",
                            "initialSize", "maxWaitMillis", "testOnCreate", "testOnBorrow", "testOnReturn",
                            "timeBetweenEvictionRunsMillis", "numTestsPerEvictionRun", "minEvictableIdleTimeMillis",
                            "softMinEvictableIdleTimeMillis", "evictionPolicyClassName", "testWhileIdle", "password",
                            "url", "username", "validationQuery", "validationQueryTimeout", "jmxName",
                            "connectionFactoryClassName", "connectionInitSqls", "accessToUnderlyingConnectionAllowed",
                            "removeAbandonedOnBorrow", "removeAbandonedOnMaintenance", "removeAbandonedTimeout",
                            "logAbandoned", "abandonedUsageTracking", "poolPreparedStatements",
                            "maxOpenPreparedStatements", "connectionProperties", "maxConnLifetimeMillis",
                            "logExpiredConnections", "rollbackOnReturn", "enableAutoCommitOnReturn",
                            "defaultQueryTimeout", "fastFailValidation"}
            ) Properties properties) throws InitializationException {
        try {
            this.dataSource = BasicDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            throw new InitializationException("Failed to create data source", e);
        }
    }

    /**
     * Attempts to establish a connection with the data source that this {@link DataSource} object represents.
     * @return a connection to the data source
     * @throws SQLException if a database access error occurs
     */
    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Attempts to establish a connection with the data source that this {@link DataSource} object represents.
     * @param username the database user on whose behalf the connection is being made
     * @param password the user's password
     * @return a connection to the data source
     * @throws SQLException if a database access error occurs
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return dataSource.getConnection(username, password);
    }

    /**
     * Retrieves the log writer for this {@link DataSource} object.
     *
     * The log writer is a character output stream to which all logging and tracing messages for this data source will
     * be printed. This includes messages printed by the methods of this object, messages printed by methods of other
     * objects manufactured by this object, and so on. Messages printed to a data source specific log writer are not
     * printed to the log writer associated with the {@link java.sql.DriverManager} class. When a {@link DataSource}
     * object is created, the log writer is initially {@code null}; in other words, the default is for logging to be
     * disabled.
     *
     * @return the log writer for this data source or null if logging is disabled
     * @throws SQLException if a database access error occurs
     */
    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return dataSource.getLogWriter();
    }

    /**
     * Sets the log writer for this {@link DataSource} object to the given {@link PrintWriter} object.
     *
     * The log writer is a character output stream to which all logging and tracing messages for this data source will
     * be printed. This includes messages printed by the methods of this object, messages printed by methods of other
     * objects manufactured by this object, and so on. Messages printed to a data source specific log writer are not
     * printed to the log writer associated with the {@link java.sql.DriverManager} class. When a {@link DataSource}
     * object is created, the log writer is initially {@code null}; in other words, the default is for logging to be
     * disabled.
     *
     * @param out the new log writer; to disable logging, set to null
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        dataSource.setLogWriter(out);
    }

    /**
     * Sets the maximum time in seconds that this data source will wait while attempting to connect to a database.
     * A value of zero specifies that the timeout is the default system timeout if there is one; otherwise, it specifies
     * that there is no timeout. When a {@link DataSource} object is created, the login timeout is initially zero.
     * @param seconds the data source login time limit
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        dataSource.setLoginTimeout(seconds);
    }

    /**
     * Gets the maximum time in seconds that this data source can wait while attempting to connect to a database.
     * A value of zero means that the timeout is the default system timeout if there is one; otherwise, it means that
     * there is no timeout. When a {@link DataSource} object is created, the login timeout is initially zero.
     * @return the data source login time limit
     * @throws SQLException if a database access error occurs
     */
    @Override
    public int getLoginTimeout() throws SQLException {
        return dataSource.getLoginTimeout();
    }

    /**
     * Return the parent Logger of all the Loggers used by this data source. This should be the Logger farthest from the
     * root Logger that is still an ancestor of all of the Loggers used by this data source. Configuring this Logger
     * will affect all of the log messages generated by the data source. In the worst case, this may be the root Logger.
     * @return the parent Logger for this data source
     * @throws SQLFeatureNotSupportedException if the data source does not use {@code java.util.logging}
     */
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return dataSource.getParentLogger();
    }

    /**
     * Returns an object that implements the given interface to allow access to non-standard methods, or standard
     * methods not exposed by the proxy. If the receiver implements the interface then the result is the receiver or a
     * proxy for the receiver. If the receiver is a wrapper and the wrapped object implements the interface then the
     * result is the wrapped object or a proxy for the wrapped object. Otherwise return the the result of calling
     * {@code unwrap} recursively on the wrapped object or a proxy for that result. If the receiver is not a wrapper
     * and does not implement the interface, then an {@link SQLException} is thrown.
     * @param iface A Class defining an interface that the result must implement
     * @return an object that implements the interface. May be a proxy for the actual implementing object
     * @throws SQLException If no object found that implements the interface
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(dataSource)) {
            return (T) dataSource;
        }
        return dataSource.unwrap(iface);
    }

    /**
     * Returns true if this either implements the interface argument or is directly or indirectly a wrapper for an
     * object that does. Returns false otherwise. If this implements the interface then return true, else if this is a
     * wrapper then return the result of recursively calling {@code isWrapperFor} on the wrapped object. If this does
     * not implement the interface and is not a wrapper, return false. This method should be implemented as a low-cost
     * operation compared to unwrap so that callers can use this method to avoid expensive unwrap calls that may fail.
     * If this method returns true then calling unwrap with the same argument should succeed.
     * @param iface a Class defining an interface
     * @return true if this implements the interface or directly or indirectly wraps an object that does
     * @throws SQLException if an error occurs while determining whether this is a wrapper for an object with the given
     *                      interface
     */
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (iface.isInstance(dataSource)) {
            return true;
        }
        return dataSource.isWrapperFor(iface);
    }
}
