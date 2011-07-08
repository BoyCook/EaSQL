package org.cccs.easql.util;

import com.sun.org.apache.regexp.internal.RE;
import org.cccs.easql.*;
import org.cccs.easql.domain.Pair;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * User: boycook
 * Date: 30/06/2011
 * Time: 18:42
 */
public final class ReflectionUtils {

    private static Map<Class, String[]> columnNames;
    private static Map<Class, DBField[]> columns;

    static {
        columnNames = new HashMap<Class, String[]>();
        columns = new HashMap<Class, DBField[]>();
    }

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

    public static Object getPrimaryValue(Object o) {
        Class c = o.getClass();
        Field[] fields = c.getFields();
        Object primary = null;
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column != null && column.primaryKey()) {
                primary = getFieldValue(field, o);
            }
        }
        return primary;
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

    public static Object getFieldValue(Field field, Object o) {
        Object value = null;

        try {
            value = field.get(o);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return value;
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

    public static DBField[] getExtractionMappings(Class c, boolean loadRelations)  {
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
                            setObjectValue(field.getName(), o, relatedO);
                        }
                    } else {
                        columns.add(new DBField(field, relation.key(), "", o));
                    }
                }
            }
        }
        return columns.toArray(new DBField[columns.size()]);
    }

    public static void setObjectValue(String fieldName, Object o, Object value) {
        Class c = o.getClass();
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

    public static String[] getColumnNames(Class c) {
        String[] columns = columnNames.get(c);
        if (columns == null) {
            columns = getColumnNamesForClass(c);
            columnNames.put(c, columns);
        }
        return columns;
    }

    private static String[] getColumnNamesForClass(Class c) {
        Collection<String> columns = new ArrayList<String>();
        Field[] fields = c.getFields();
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                columns.add(getColumnName(field));
            }
        }
        return columns.toArray(new String[columns.size()]);
    }

    public static DBField[] getColumns(Class c) {
        DBField[] tempColumns = columns.get(c);
        if (tempColumns == null) {
            tempColumns = getColumnsForClass(c);
            columns.put(c, tempColumns);
        }
        return tempColumns;
    }

    private static DBField[] getColumnsForClass(Class c) {
        Collection<DBField> columns = new ArrayList<DBField>();
        Field[] fields = c.getFields();
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                columns.add(new DBField(field, getColumnName(field), ""));
            }
        }
        return columns.toArray(new DBField[columns.size()]);
    }

    @SuppressWarnings({"unchecked"})
    public static Object[] getRelatedValues(Object o) {
        Class c = o.getClass();
        Field[] fields = c.getFields();
        Collection values = new ArrayList();

        for (Field field : fields) {
            Relation relation = field.getAnnotation(Relation.class);
            if (relation != null && relation.cardinality().equals(Cardinality.MANY_TO_ONE)) {
                values.add(getFieldValue(field, c));
            }
        }
        return values.toArray();
    }

    public static Object[] getRelations(Object o) {
        Collection<Object> relations = new ArrayList<Object>();
        Class c = o.getClass();
        Field[] fields = c.getFields();
        for (Field field : fields) {
            Relation relation = field.getAnnotation(Relation.class);
            if (relation != null && relation.cardinality().equals(Cardinality.MANY_TO_ONE)) {
                relations.add(getFieldValue(field, o));
            }
        }
        return relations.toArray(new Object[relations.size()]);
    }

    @Deprecated
    public static Pair[] getRelations(Class c) {
        Collection<Pair> relations = new ArrayList<Pair>();
        Field[] fields = c.getFields();
        for (Field field : fields) {
            Relation relation = field.getAnnotation(Relation.class);
            if (relation != null) {
                relations.add(new Pair(relation, field));
            }
        }
        return relations.toArray(new Pair[relations.size()]);
    }
}
