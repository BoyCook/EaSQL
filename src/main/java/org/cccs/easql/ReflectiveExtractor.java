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
            Object o = getObject();
            for (DBField column: columns) {
                setColumnValue(rs, column, o);
            }
            results.add(o);
        }

        return results;
    }

    private void setColumnValue(ResultSet rs, DBField column, Object o) throws SQLException {
        int index = rs.findColumn(column.columnName);

        if (column.field.getType().equals(String.class)) {
            setValue(getClassType(), column.field.getName(), o, rs.getString(index));
        } else if (column.field.getType().equals(Long.TYPE)) {
            setValue(getClassType(), column.field.getName(), o, rs.getLong(index));
        } else if (column.field.getType().equals(Integer.TYPE)) {
            setValue(getClassType(), column.field.getName(), o, rs.getInt(index));
        } else if (column.field.getType().equals(Boolean.TYPE)) {
            setValue(getClassType(), column.field.getName(), o, rs.getBoolean(index));
        } else {
            System.out.println(format("[%s] has unknown class: [%s]", column.columnName, column.field.getType().getName()));
        }
    }

    private void setValue(Class c, String fieldName, Object o, Object value) {
        Field field = null;
        try {
            field = c.getField(fieldName);
            field.set(o, value);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Object getObject() {
        Object o = null;
        try {
            o = classType.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return o;
    }

    private Class getClassType() {
        return this.classType;
    }
}
