package org.cccs.easql.execution;

import org.cccs.easql.domain.ExtractionMapping;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static org.cccs.easql.util.ObjectUtils.setValue;

/**
 * User: boycook
 * Date: 22/06/2011
 * Time: 14:20
 */
public class Extractor<T> implements ResultSetExtractor<Collection<T>> {
    private final Class<?> classType;
    private final MappingsGenerator mappingsGenerator;

    public Extractor(final Class<?> type, final MappingsGenerator mappingsGenerator) {
        this.classType = type;
        this.mappingsGenerator = mappingsGenerator;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public Collection<T> extractData(ResultSet rs) throws SQLException, DataAccessException {
        final Collection results = new ArrayList();
        while (rs.next()) {
            //Need to create new object per row via generator
            ExtractionMapping[] dbFields = mappingsGenerator.generate(classType);
            for (ExtractionMapping column : dbFields) {
                setColumnValue(rs, column);
                if (getClassType().equals(column.object.getClass()) && !results.contains(column.object)) {
                    results.add(column.object);
                }
            }
        }
        return results;
    }

    private void setColumnValue(ResultSet rs, ExtractionMapping column) throws SQLException {
        int index = rs.findColumn(column.name);
        if (column.type.equals(String.class)) {
            setValue(column.property, column.object, rs.getString(index));
        } else if (column.type.equals(Long.TYPE)) {
            setValue(column.property, column.object, rs.getLong(index));
        } else if (column.type.equals(Integer.TYPE)) {
            setValue(column.property, column.object, rs.getInt(index));
        } else if (column.type.equals(Boolean.TYPE)) {
            setValue(column.property, column.object, rs.getBoolean(index));
        }
    }

    private Class getClassType() {
        return this.classType;
    }
}
