package com.ivan1pl.witchcraft.jdbc.statement;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Interface for mapping query result rows to POJOs.
 * @param <T> requested result type
 */
public interface RowMapper<T> {
    /**
     * Map row.
     * @param rowIndex row index
     * @param resultSet columns returned from the query
     * @return mapped object
     * @throws SQLException when query result cannot be mapped to Java object
     */
    T mapRow(int rowIndex, ResultSet resultSet) throws SQLException;
}
