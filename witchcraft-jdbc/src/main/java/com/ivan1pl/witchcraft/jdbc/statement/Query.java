package com.ivan1pl.witchcraft.jdbc.statement;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * SQL Query wrapper.
 */
public class Query {
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
     * Create query wrapper.
     * @param sql SQL string
     */
    public Query(String sql) {
        this.sql = sql;
        buildParameterIndex();
    }

    /**
     * Extract parameter names from the SQL string.
     */
    private void buildParameterIndex() {
        int currentIndex = sql.indexOf(':');
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
                    currentIndex = paramEndIndex;
                } else {
                    currentIndex++;
                }
            }

            currentIndex = sql.indexOf(':', currentIndex + 1);
        }
        currentIndex = sql.indexOf('?');
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
}
