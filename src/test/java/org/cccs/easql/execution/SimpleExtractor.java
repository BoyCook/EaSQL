package org.cccs.easql.execution;

import org.cccs.easql.domain.ColumnMapping;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static java.lang.String.format;
import static org.cccs.easql.util.ClassUtils.getAllColumns;

/**
 * User: boycook
 * Date: 04/09/2011
 * Time: 17:07
 *
 * This is for testing purposes only, to check that values are updated as expected
 */
public class SimpleExtractor implements ResultSetExtractor<Collection<?>> {
    private final Class<?> classType;

    public SimpleExtractor(Class<?> classType) {
        this.classType = classType;
    }

    @Override
    public Collection<?> extractData(ResultSet rs) throws SQLException, DataAccessException {
        ColumnMapping[] columns = getAllColumns(classType);
        System.out.println(format("There are [%d] columns for [%s]", columns.length, classType.getName()));

        while (rs.next()) {
            for (ColumnMapping column : columns) {
                int index = rs.findColumn(column.columnName);

                if (column.field.getType().equals(String.class)) {
                    System.out.println(format("[%s] is [%s]", column.columnName, rs.getString(index)));
                } else if (column.field.getType().equals(Long.TYPE)) {
                    System.out.println(format("[%s] is [%d]", column.columnName, rs.getLong(index)));
                } else if (column.field.getType().equals(Integer.TYPE)) {
                    System.out.println(format("[%s] is [%d]", column.columnName, rs.getInt(index)));
                } else if (column.field.getType().equals(Boolean.TYPE)) {
                    System.out.println(format("[%s] is [%b]", column.columnName, rs.getBoolean(index)));
                } else {
                    System.out.println(format("[%s] is [%d]", column.columnName, rs.getLong(index)));
                }
            }
        }

        return new ArrayList();
    }
}
