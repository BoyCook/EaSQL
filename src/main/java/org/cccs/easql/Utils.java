package org.cccs.easql;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.cccs.easql.ReflectiveSQLGenerator.getColumnNames;
import static org.cccs.easql.ReflectiveSQLGenerator.getColumns;
import static org.cccs.easql.Utils.getTableName;

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

    public static Object getObject(Class c) {
        Object o = null;
        try {
            o = c.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return o;
    }

    public static DBField[] getDBColumns(Class c, boolean loadRelations)  {
        Collection<DBField> columns = new ArrayList<DBField>();
        Field[] fields = c.getFields();
        Object o = getObject(c);

        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            Relation relation = field.getAnnotation(Relation.class);
            if (column != null) {
                if (loadRelations) {
                    columns.add(new DBField(field, getColumnName(field), "", o));
                } else {
                    columns.add(new DBField(field, getColumnName(field), "", o));
                }
            } else if (relation != null) {
                if (relation.cardinality().equals(Cardinality.MANY_TO_ONE)) {
                    if (loadRelations) {
                        DBField[] relatedColumns = getColumns(field.getType());
                        Object relatedO = getObject(field.getType());

                        for (DBField relatedColumn : relatedColumns) {
                            columns.add(new DBField(relatedColumn.field, relation.name() + "_" + relatedColumn.columnName, "", relatedO));
                            setValue(c, field.getName(), o, relatedO);
                        }
                    } else {
                        columns.add(new DBField(field, relation.key(), "", o));
                    }
                }
            }
        }
        return columns.toArray(new DBField[columns.size()]);
    }

    public static void setValue(Class c, String fieldName, Object o, Object value) {
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
}
