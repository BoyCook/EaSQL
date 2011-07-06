package org.cccs.easql;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static java.lang.String.format;
import static org.cccs.easql.ReflectiveSQLGenerator.getColumns;
import static org.cccs.easql.Utils.getObject;
import static org.cccs.easql.Utils.setValue;

/**
 * User: boycook
 * Date: 22/06/2011
 * Time: 14:20
 */
public class ReflectiveExtractor implements ResultSetExtractor<Collection<?>> {
    private final Class<?> classType;
    private final DBField[] columns;

    public ReflectiveExtractor(final Class<?> type, final DBField[] columns) {
        this.classType = type;
        this.columns = columns;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public Collection<?> extractData(ResultSet rs) throws SQLException, DataAccessException {
        final Collection results = new ArrayList();

        while (rs.next()) {
            for (DBField column: columns) {
                setColumnValue(rs, column, column.object);
                if (getClassType().equals(column.object.getClass()) && !results.contains(column.object)) {
                    results.add(column.object);
                }
            }
        }

        return results;
    }

    private void setColumnValue(ResultSet rs, DBField column, Object o) throws SQLException {
        int index = rs.findColumn(column.columnName);

        if (column.field.getType().equals(String.class)) {
            setValue(column.object.getClass(), column.field.getName(), o, rs.getString(index));
        } else if (column.field.getType().equals(Long.TYPE)) {
            setValue(column.object.getClass(), column.field.getName(), o, rs.getLong(index));
        } else if (column.field.getType().equals(Integer.TYPE)) {
            setValue(column.object.getClass(), column.field.getName(), o, rs.getInt(index));
        } else if (column.field.getType().equals(Boolean.TYPE)) {
            setValue(column.object.getClass(), column.field.getName(), o, rs.getBoolean(index));
        } else {
            System.out.println(format("[%s] has unknown class: [%s]", column.columnName, column.field.getType().getName()));
        }
    }

    private Class getClassType() {
        return this.classType;
    }
}
