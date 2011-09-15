package org.cccs.easql.execution;

import org.cccs.easql.domain.ExtractionMapping;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static org.cccs.easql.util.ClassUtils.generateExtractionMappings;
import static org.cccs.easql.util.ObjectUtils.setObjectValue;

/**
 * User: boycook
 * Date: 22/06/2011
 * Time: 14:20
 */
public class Extractor implements ResultSetExtractor<Collection<?>> {
    private final Class<?> classType;
    private final boolean loadRelations;

    public Extractor(final Class<?> type, boolean loadRelations) {
        this.classType = type;
        this.loadRelations = loadRelations;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public Collection<?> extractData(ResultSet rs) throws SQLException, DataAccessException {
        final Collection results = new ArrayList();
        while (rs.next()) {
            //Need to get fresh object per row
            ExtractionMapping[] dbFields = generateExtractionMappings(getClassType(), loadRelations);

            for (ExtractionMapping column: dbFields) {
                setColumnValue(rs, column, column.object);
                if (getClassType().equals(column.object.getClass()) && !results.contains(column.object)) {
                    results.add(column.object);
                }
            }
        }
        return results;
    }

    private void setColumnValue(ResultSet rs, ExtractionMapping column, Object o) throws SQLException {
        int index = rs.findColumn(column.name);

        if (column.field.getType().equals(String.class)) {
            setObjectValue(column.field, o, rs.getString(index));
        } else if (column.field.getType().equals(Long.TYPE)) {
            setObjectValue(column.field, o, rs.getLong(index));
        } else if (column.field.getType().equals(Integer.TYPE)) {
            setObjectValue(column.field, o, rs.getInt(index));
        } else if (column.field.getType().equals(Boolean.TYPE)) {
            setObjectValue(column.field, o, rs.getBoolean(index));
        }
    }

    private Class getClassType() {
        return this.classType;
    }
}
