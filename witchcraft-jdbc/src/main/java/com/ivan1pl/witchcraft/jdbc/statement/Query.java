package com.ivan1pl.witchcraft.jdbc.statement;

import com.ivan1pl.witchcraft.jdbc.exception.InvalidQueryException;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * SQL Query wrapper.
 */
public class Query implements Cloneable {
    /**
     * Set of characters that qualify as parameter separators,
     * indicating that a parameter name in a SQL String has ended.
     */
    private static final String PARAMETER_SEPARATORS = "\"':&,;()|=+-*%/\\<>^";

    /**
     * SQL string.
     */
    private String sql;

    /**
     * List of all parameters detected in the query.
     */
    private final List<Parameter> parameters = new LinkedList<>();

    /**
     * List of all unnamed parameter occurrences in the query.
     */
    private final List<Integer> unnamedParameters = new LinkedList<>();

    /**
     * Parameter values.
     */
    private final Map<String, ParameterValue> parameterValues = new HashMap<>();

    /**
     * Unnamed parameter values.
     */
    private final Map<Integer, ParameterValue> unnamedParameterValues = new HashMap<>();

    /**
     * Maximum number of rows that can be returned from the database.
     */
    private int maxRows;

    /**
     * Create query wrapper.
     * @param sql SQL string
     * @throws InvalidQueryException when the query is invalid
     */
    public Query(String sql) throws InvalidQueryException {
        this.sql = sql;
        buildParameterIndex();
    }

    /**
     * Extract parameter names from the SQL string.
     */
    private void buildParameterIndex() throws InvalidQueryException {
        int currentIndex = sql.indexOf('?');
        while (currentIndex >= 0 && currentIndex < sql.length()) {
            // Skip "??", "?|", "?&" (postgres)
            if (currentIndex < sql.length() - 1 &&
                    (sql.charAt(currentIndex + 1) == '?' || sql.charAt(currentIndex + 1) == '|' ||
                            sql.charAt(currentIndex + 1) == '&')) {
                currentIndex++;
            } else {
                unnamedParameters.add(currentIndex);
            }
            currentIndex = sql.indexOf('?', currentIndex + 1);
        }
        currentIndex = sql.indexOf(':');
        while (currentIndex >= 0 && currentIndex < sql.length() - 1) {
            // Skip escaped ":"
            if (currentIndex > 0 && sql.charAt(currentIndex - 1) == '\\') {
                sql = sql.substring(0, currentIndex - 1) + sql.substring(currentIndex);
            } else {
                int paramEndIndex = currentIndex + 1;
                while (paramEndIndex < sql.length() && !Character.isWhitespace(sql.charAt(paramEndIndex)) &&
                        PARAMETER_SEPARATORS.indexOf(sql.charAt(paramEndIndex)) < 0) {
                    paramEndIndex++;
                }
                paramEndIndex--;
                if (paramEndIndex - currentIndex > 0) {
                    parameters.add(new Parameter(
                            sql.substring(currentIndex + 1, paramEndIndex + 1), currentIndex, paramEndIndex));
                    sql = sql.substring(0, currentIndex) + '?' + sql.substring(paramEndIndex + 1);
                } else {
                    currentIndex++;
                }
            }

            currentIndex = sql.indexOf(':', currentIndex + 1);
        }

        if (!parameters.isEmpty() && !unnamedParameters.isEmpty()) {
            throw new InvalidQueryException("Query cannot have both named and unnamed parameters at the same time");
        }
    }

    /**
     * Sets the designated parameter to the given {@link ParameterValue} object. The driver converts this to a value
     * of the SQL type stored withing the {@link ParameterValue} object when it sends it to the database.
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x a {@link ParameterValue} object that maps the SQL value of the type stored within
     */
    public void setParameter(int parameterIndex, ParameterValue x) {
        unnamedParameterValues.put(parameterIndex, x);
    }

    /**
     * Sets the designated parameter to the given {@link ParameterValue} object. The driver converts this to a value
     * of the SQL type stored withing the {@link ParameterValue} object when it sends it to the database.
     * @param parameterName parameter name
     * @param x a {@link ParameterValue} object that maps the SQL value of the type stored within
     */
    public void setParameter(String parameterName, ParameterValue x) {
        parameterValues.put(parameterName, x);
    }

    /**
     * Sets the designated parameter to the given object. The driver converts this to a value of the SQL type passed in
     * {@code type} when it sends it to the database.
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x an object that maps the SQL value of the type passed in {@code type} parameter
     * @param type SQL type mapped by the designated parameter
     */
    public void setParameter(int parameterIndex, Object x, int type) {
        setParameter(parameterIndex, new ParameterValue(x, type));
    }

    /**
     * Sets the designated parameter to the given object. The driver converts this to a value of the SQL type passed in
     * {@code type} when it sends it to the database.
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x an object that maps the SQL value of the type passed in {@code type} parameter
     * @param type SQL type mapped by the designated parameter
     */
    public void setParameter(int parameterIndex, Object x, ParameterType type) {
        setParameter(parameterIndex, x, type.getType());
    }

    /**
     * Sets the designated parameter to the given object. The driver converts this to a value of the SQL type passed in
     * {@code type} when it sends it to the database.
     * @param parameterName parameter name
     * @param x an object that maps the SQL value of the type passed in {@code type} parameter
     * @param type SQL type mapped by the designated parameter
     */
    public void setParameter(String parameterName, Object x, int type) {
        setParameter(parameterName, new ParameterValue(x, type));
    }

    /**
     * Sets the designated parameter to the given object. The driver converts this to a value of the SQL type passed in
     * {@code type} when it sends it to the database.
     * @param parameterName parameter name
     * @param x an object that maps the SQL value of the type passed in {@code type} parameter
     * @param type SQL type mapped by the designated parameter
     */
    public void setParameter(String parameterName, Object x, ParameterType type) {
        setParameter(parameterName, x, type.getType());
    }

    /**
     * Sets the designated parameter to the given {@link Array} object. The driver converts this to an SQL {@code ARRAY}
     * value when it sends it to the database.
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x an {@link Array} object that maps an SQL {@code ARRAY} value
     */
    public void setParameter(int parameterIndex, Array x) {
        setParameter(parameterIndex, x, Types.ARRAY);
    }

    /**
     * Sets the designated parameter to the given {@link Array} object. The driver converts this to an SQL {@code ARRAY}
     * value when it sends it to the database.
     * @param parameterName parameter name
     * @param x an {@link Array} object that maps an SQL {@code ARRAY} value
     */
    public void setParameter(String parameterName, Array x) {
        setParameter(parameterName, x, Types.ARRAY);
    }

    /**
     * Sets the designated parameter to the given {@link BigDecimal} object. The driver converts this to an SQL
     * {@code NUMERIC} value when it sends it to the database.
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     */
    public void setParameter(int parameterIndex, BigDecimal x) {
        setParameter(parameterIndex, x, Types.NUMERIC);
    }

    /**
     * Sets the designated parameter to the given {@link BigDecimal} object. The driver converts this to an SQL
     * {@code NUMERIC} value when it sends it to the database.
     * @param parameterName parameter name
     * @param x the parameter value
     */
    public void setParameter(String parameterName, BigDecimal x) {
        setParameter(parameterName, x, Types.NUMERIC);
    }

    /**
     * Sets the designated parameter to the given {@link Blob} object. The driver converts this to an SQL {@code BLOB}
     * value when it sends it to the database.
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x a {@link Blob} object that maps an SQL {@code BLOB} value
     */
    public void setParameter(int parameterIndex, Blob x) {
        setParameter(parameterIndex, x, Types.BLOB);
    }

    /**
     * Sets the designated parameter to the given {@link Blob} object. The driver converts this to an SQL {@code BLOB}
     * value when it sends it to the database.
     * @param parameterName parameter name
     * @param x a {@link Blob} object that maps an SQL {@code BLOB} value
     */
    public void setParameter(String parameterName, Blob x) {
        setParameter(parameterName, x, Types.BLOB);
    }

    /**
     * Sets the designated parameter to the given Java {@code byte} value. The driver converts this to an SQL
     * {@code TINYINT} value when it sends it to the database.
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     */
    public void setParameter(int parameterIndex, byte x) {
        setParameter(parameterIndex, x, Types.TINYINT);
    }

    /**
     * Sets the designated parameter to the given Java {@code byte} value. The driver converts this to an SQL
     * {@code TINYINT} value when it sends it to the database.
     * @param parameterName parameter name
     * @param x the parameter value
     */
    public void setParameter(String parameterName, byte x) {
        setParameter(parameterName, x, Types.TINYINT);
    }

    /**
     * Sets the designated parameter to the given Java array of bytes. The driver converts this to an SQL
     * {@code VARBINARY} or {@code LONGVARBINARY} (depending on the argument's size relative to the driver's limits on
     * {@code VARBINARY} values) when it sends it to the database.
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     */
    @SuppressWarnings("deprecation")
    public void setParameter(int parameterIndex, byte[] x) {
        setParameter(parameterIndex, x, ParameterType.AUTO);
    }

    /**
     * Sets the designated parameter to the given Java array of bytes. The driver converts this to an SQL
     * {@code VARBINARY} or {@code LONGVARBINARY} (depending on the argument's size relative to the driver's limits on
     * {@code VARBINARY} values) when it sends it to the database.
     * @param parameterName parameter name
     * @param x the parameter value
     */
    @SuppressWarnings("deprecation")
    public void setParameter(String parameterName, byte[] x) {
        setParameter(parameterName, x, ParameterType.AUTO);
    }

    /**
     * Sets the designated parameter to the given {@link Clob} object. The driver converts this to an SQL {@code CLOB}
     * value when it sends it to the database.
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x a {@link Clob} object that maps an SQL {@code CLOB} value
     */
    public void setParameter(int parameterIndex, Clob x) {
        setParameter(parameterIndex, x, Types.CLOB);
    }

    /**
     * Sets the designated parameter to the given {@link Clob} object. The driver converts this to an SQL {@code CLOB}
     * value when it sends it to the database.
     * @param parameterName parameter name
     * @param x a {@link Clob} object that maps an SQL {@code CLOB} value
     */
    public void setParameter(String parameterName, Clob x) {
        setParameter(parameterName, x, Types.CLOB);
    }

    /**
     * Sets the designated parameter to the given {@link Date} value using the default time zone of the virtual machine
     * that is running the application. The driver converts this to an SQL {@code DATE} value when it sends it to the
     * database.
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     */
    public void setParameter(int parameterIndex, Date x) {
        setParameter(parameterIndex, x, Types.DATE);
    }

    /**
     * Sets the designated parameter to the given {@link Date} value using the default time zone of the virtual machine
     * that is running the application. The driver converts this to an SQL {@code DATE} value when it sends it to the
     * database.
     * @param parameterName parameter name
     * @param x the parameter value
     */
    public void setParameter(String parameterName, Date x) {
        setParameter(parameterName, x, Types.DATE);
    }

    /**
     * Sets the designated parameter to the given Java {@code double} value. The driver converts this to an SQL
     * {@code DOUBLE} value when it sends it to the database.
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     */
    public void setParameter(int parameterIndex, double x) {
        setParameter(parameterIndex, x, Types.DOUBLE);
    }

    /**
     * Sets the designated parameter to the given Java {@code double} value. The driver converts this to an SQL
     * {@code DOUBLE} value when it sends it to the database.
     * @param parameterName parameter name
     * @param x the parameter value
     */
    public void setParameter(String parameterName, double x) {
        setParameter(parameterName, x, Types.DOUBLE);
    }

    /**
     * Sets the designated parameter to the given Java {@code float} value. The driver converts this to an SQL
     * {@code REAL} value when it sends it to the database.
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     */
    public void setParameter(int parameterIndex, float x) {
        setParameter(parameterIndex, x, Types.REAL);
    }

    /**
     * Sets the designated parameter to the given Java {@code float} value. The driver converts this to an SQL
     * {@code REAL} value when it sends it to the database.
     * @param parameterName parameter name
     * @param x the parameter value
     */
    public void setParameter(String parameterName, float x) {
        setParameter(parameterName, x, Types.REAL);
    }

    /**
     * Sets the designated parameter to the given Java {@code int} value. The driver converts this to an SQL
     * {@code INTEGER} value when it sends it to the database.
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     */
    public void setParameter(int parameterIndex, int x) {
        setParameter(parameterIndex, x, Types.INTEGER);
    }

    /**
     * Sets the designated parameter to the given Java {@code int} value. The driver converts this to an SQL
     * {@code INTEGER} value when it sends it to the database.
     * @param parameterName parameter name
     * @param x the parameter value
     */
    public void setParameter(String parameterName, int x) {
        setParameter(parameterName, x, Types.INTEGER);
    }

    /**
     * Sets the designated parameter to the given Java {@code long} value. The driver converts this to an SQL
     * {@code BIGINT} value when it sends it to the database.
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     */
    public void setParameter(int parameterIndex, long x) {
        setParameter(parameterIndex, x, Types.BIGINT);
    }

    /**
     * Sets the designated parameter to the given Java {@code long} value. The driver converts this to an SQL
     * {@code BIGINT} value when it sends it to the database.
     * @param parameterName parameter name
     * @param x the parameter value
     */
    public void setParameter(String parameterName, long x) {
        setParameter(parameterName, x, Types.BIGINT);
    }

    /**
     * Sets the designated parameter to the given {@link NClob} object. The driver converts this to an SQL {@code NCLOB}
     * value when it sends it to the database.
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x a {@link NClob} object that maps an SQL {@code NCLOB} value
     */
    public void setParameter(int parameterIndex, NClob x) {
        setParameter(parameterIndex, x, Types.NCLOB);
    }

    /**
     * Sets the designated parameter to the given {@link NClob} object. The driver converts this to an SQL {@code NCLOB}
     * value when it sends it to the database.
     * @param parameterName parameter name
     * @param x a {@link NClob} object that maps an SQL {@code NCLOB} value
     */
    public void setParameter(String parameterName, NClob x) {
        setParameter(parameterName, x, Types.NCLOB);
    }

    /**
     * Sets the designated parameter to the given {@code REF(<structured-type>)} value. The driver converts this to an
     * SQL {@code REF} value when it sends it to the database.
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x an SQL {@code REF} value
     */
    public void setParameter(int parameterIndex, Ref x) {
        setParameter(parameterIndex, x, Types.REF);
    }

    /**
     * Sets the designated parameter to the given {@code REF(<structured-type>)} value. The driver converts this to an
     * SQL {@code REF} value when it sends it to the database.
     * @param parameterName parameter name
     * @param x an SQL {@code REF} value
     */
    public void setParameter(String parameterName, Ref x) {
        setParameter(parameterName, x, Types.REF);
    }

    /**
     * Sets the designated parameter to the given {@link RowId} object. The driver converts this to an SQL
     * {@code ROWID} value when it sends it to the database.
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     */
    public void setParameter(int parameterIndex, RowId x) {
        setParameter(parameterIndex, x, Types.ROWID);
    }

    /**
     * Sets the designated parameter to the given {@link RowId} object. The driver converts this to an SQL
     * {@code ROWID} value when it sends it to the database.
     * @param parameterName parameter name
     * @param x the parameter value
     */
    public void setParameter(String parameterName, RowId x) {
        setParameter(parameterName, x, Types.ROWID);
    }

    /**
     * Sets the designated parameter to the given Java {@code short} value. The driver converts this to an SQL
     * {@code SMALLINT} value when it sends it to the database.
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     */
    public void setParameter(int parameterIndex, short x) {
        setParameter(parameterIndex, x, Types.SMALLINT);
    }

    /**
     * Sets the designated parameter to the given Java {@code short} value. The driver converts this to an SQL
     * {@code SMALLINT} value when it sends it to the database.
     * @param parameterName parameter name
     * @param x the parameter value
     */
    public void setParameter(String parameterName, short x) {
        setParameter(parameterName, x, Types.SMALLINT);
    }

    /**
     * Sets the designated parameter to the given {@link SQLXML} object. The driver converts this to an SQL {@code XML}
     * value when it sends it to the database.
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x a {@link SQLXML} object that maps an SQL {@code XML} value
     */
    public void setParameter(int parameterIndex, SQLXML x) {
        setParameter(parameterIndex, x, Types.SQLXML);
    }

    /**
     * Sets the designated parameter to the given {@link SQLXML} object. The driver converts this to an SQL {@code XML}
     * value when it sends it to the database.
     * @param parameterName parameter name
     * @param x a {@link SQLXML} object that maps an SQL {@code XML} value
     */
    public void setParameter(String parameterName, SQLXML x) {
        setParameter(parameterName, x, Types.SQLXML);
    }

    /**
     * Sets the designated parameter to the given Java {@link String} value. The driver converts this to an SQL
     * {@code VARCHAR} or {@code LONGVARCHAR} (depending on the argument's size relative to the driver's limits on
     * {@code VARCHAR} values) when it sends it to the database.
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     */
    @SuppressWarnings("deprecation")
    public void setParameter(int parameterIndex, String x) {
        setParameter(parameterIndex, x, ParameterType.AUTO);
    }

    /**
     * Sets the designated parameter to the given Java {@link String} value. The driver converts this to an SQL
     * {@code VARCHAR} or {@code LONGVARCHAR} (depending on the argument's size relative to the driver's limits on
     * {@code VARCHAR} values) when it sends it to the database.
     * @param parameterName parameter name
     * @param x the parameter value
     */
    @SuppressWarnings("deprecation")
    public void setParameter(String parameterName, String x) {
        setParameter(parameterName, x, ParameterType.AUTO);
    }

    /**
     * Sets the designated parameter to the given {@link Time} object. The driver converts this to an SQL
     * {@code TIME} value when it sends it to the database.
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     */
    public void setParameter(int parameterIndex, Time x) {
        setParameter(parameterIndex, x, Types.TIME);
    }

    /**
     * Sets the designated parameter to the given {@link Time} object. The driver converts this to an SQL
     * {@code TIME} value when it sends it to the database.
     * @param parameterName parameter name
     * @param x the parameter value
     */
    public void setParameter(String parameterName, Time x) {
        setParameter(parameterName, x, Types.TIME);
    }

    /**
     * Sets the designated parameter to the given {@link Timestamp} object. The driver converts this to an SQL
     * {@code TIMESTAMP} value when it sends it to the database.
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     */
    public void setParameter(int parameterIndex, Timestamp x) {
        setParameter(parameterIndex, x, Types.TIMESTAMP);
    }

    /**
     * Sets the designated parameter to the given {@link Timestamp} object. The driver converts this to an SQL
     * {@code TIMESTAMP} value when it sends it to the database.
     * @param parameterName parameter name
     * @param x the parameter value
     */
    public void setParameter(String parameterName, Timestamp x) {
        setParameter(parameterName, x, Types.TIMESTAMP);
    }

    /**
     * Sets the designated parameter to the given {@link URL} object. The driver converts this to an SQL
     * {@code DATALINK} value when it sends it to the database.
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the {@link URL} object to be set
     */
    public void setParameter(int parameterIndex, URL x) {
        setParameter(parameterIndex, x, Types.DATALINK);
    }

    /**
     * Sets the designated parameter to the given {@link URL} object. The driver converts this to an SQL
     * {@code DATALINK} value when it sends it to the database.
     * @param parameterName parameter name
     * @param x the {@link URL} object to be set
     */
    public void setParameter(String parameterName, URL x) {
        setParameter(parameterName, x, Types.DATALINK);
    }

    /**
     * Get Query SQL string.
     * @return SQL string
     */
    String getSql() {
        return sql;
    }

    /**
     * Get named parameters.
     * @return list of named parameters
     */
    List<Parameter> getParameters() {
        return parameters;
    }

    /**
     * Get unnamed parameters.
     * @return list of unnamed parameters
     */
    List<Integer> getUnnamedParameters() {
        return unnamedParameters;
    }

    /**
     * Get named parameter values.
     * @return named parameter values
     */
    Map<String, ParameterValue> getParameterValues() {
        return parameterValues;
    }

    /**
     * Get unnamed parameter values.
     * @return unnamed parameter values
     */
    Map<Integer, ParameterValue> getUnnamedParameterValues() {
        return unnamedParameterValues;
    }

    /**
     * Get maximum number of rows that can be returned from the database.
     * @return maximum number of rows
     */
    public int getMaxRows() {
        return maxRows;
    }

    /**
     * Set maximum number of rows that can be returned from the database.
     * @param maxRows maximum number of rows
     */
    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    /**
     * Clone this query.
     * @return a shallow copy of this Query object
     */
    public Query clone() {
        try {
            return (Query) super.clone();
        } catch (CloneNotSupportedException e) {
            //should never happen
            throw new RuntimeException(e);
        }
    }
}
