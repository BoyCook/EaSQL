package org.cccs.easql;

import java.lang.reflect.Field;
import java.util.Collection;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * User: boycook
 * Date: 30/06/2011
 * Time: 18:42
 */
public final class Utils {

    public static String getPrimaryColumn(Class c) {
        Field[] fields = c.getFields();
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column != null && column.primaryKey()) {
                return isNotEmpty(column.name()) ? column.name() : field.getName();
            }
        }
        return null;
    }

    public static String getColumnName(Field field) {
        Column column = field.getAnnotation(Column.class);
        return isNotEmpty(column.name()) ? column.name() : field.getName();
    }

    public static String getColumnType(Field field) {
        if (Number.class.isAssignableFrom(field.getType()) || field.getType().equals(Long.TYPE) || field.getType().equals(Integer.TYPE)) {
            return "INTEGER";
        } else if (String.class.isAssignableFrom(field.getType())) {
            return "VARCHAR";
        } else if (Boolean.class.isAssignableFrom(field.getType())) {
            return "BOOLEAN";
        }

        return null;
    }

    @SuppressWarnings({"unchecked"})
    public static String getTableName(Class c) {
        Table table = (Table) c.getAnnotation(Table.class);
        return table != null ? table.name() : c.getSimpleName();
    }

    public static Object getColumnValue(Field field, Object o) {
        Object columnValue = null;

        try {
            columnValue = field.get(o);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return columnValue;
    }

    public static String value(Object value) {
        if (value instanceof Number) {
            return value.toString();
        } else if (value instanceof Boolean) {
            return Boolean.valueOf(value.toString()) ? "1" : "0";
        } else {
            return "'" + value.toString() + "'";
        }
    }

    public static String getJoinColumnName(String joinTable, String joinColumn) {
        return joinTable + "." + joinColumn + " as " + joinTable + "_" + joinColumn;
    }
}
