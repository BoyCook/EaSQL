package org.cccs.easql.execution;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: boycook
 * Date: 04/09/2011
 * Time: 17:07
 * <p/>
 * This is for testing purposes only, to check that values are updated as expected
 */
public class SimpleExtractor implements ResultSetExtractor<Collection<?>> {
    private final Class<?> classType;

    public SimpleExtractor(Class<?> classType) {
        this.classType = classType;
    }

    @Override
    public Collection<?> extractData(ResultSet rs) throws SQLException, DataAccessException {
//        ExtractionMapping[] columns = getAllColumns(classType);
//        System.out.println(format("There are [%d] columns for [%s]", columns.length, classType.getName()));
//
//        while (rs.next()) {
//            for (ExtractionMapping column : columns) {
//                int index = rs.findColumn(column.name);
//
//                if (column.field.getType().equals(String.class)) {
//                    System.out.println(format("[%s] is [%s]", column.name, rs.getString(index)));
//                } else if (column.field.getType().equals(Long.TYPE)) {
//                    System.out.println(format("[%s] is [%d]", column.name, rs.getLong(index)));
//                } else if (column.field.getType().equals(Integer.TYPE)) {
//                    System.out.println(format("[%s] is [%d]", column.name, rs.getInt(index)));
//                } else if (column.field.getType().equals(Boolean.TYPE)) {
//                    System.out.println(format("[%s] is [%b]", column.name, rs.getBoolean(index)));
//                } else {
//                    System.out.println(format("[%s] is [%d]", column.name, rs.getLong(index)));
//                }
//            }
//        }

        return new ArrayList();
    }
}
