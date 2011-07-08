package org.cccs.easql.execution;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.cccs.easql.DBField;
import static java.lang.String.format;
import static org.cccs.easql.util.ReflectionUtils.getExtractionMappings;
import static org.cccs.easql.util.ReflectionUtils.setObjectValue;

/**
 * User: boycook
 * Date: 22/06/2011
 * Time: 14:20
 */
public class ReflectiveExtractor implements ResultSetExtractor<Collection<?>> {
    private final Class<?> classType;
    private final boolean loadRelations;

    public ReflectiveExtractor(final Class<?> type, boolean loadRelations) {
        this.classType = type;
        this.loadRelations = loadRelations;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public Collection<?> extractData(ResultSet rs) throws SQLException, DataAccessException {
        final Collection results = new ArrayList();
        while (rs.next()) {
            DBField[] dbFields = getExtractionMappings(getClassType(), loadRelations);

            for (DBField column: dbFields) {
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
            setObjectValue(column.field.getName(), o, rs.getString(index));
        } else if (column.field.getType().equals(Long.TYPE)) {
            setObjectValue(column.field.getName(), o, rs.getLong(index));
        } else if (column.field.getType().equals(Integer.TYPE)) {
            setObjectValue(column.field.getName(), o, rs.getInt(index));
        } else if (column.field.getType().equals(Boolean.TYPE)) {
            setObjectValue(column.field.getName(), o, rs.getBoolean(index));
        } else {
            System.out.println(format("[%s] has unknown class: [%s]", column.columnName, column.field.getType().getName()));
        }
    }

    private Class getClassType() {
        return this.classType;
    }
}
