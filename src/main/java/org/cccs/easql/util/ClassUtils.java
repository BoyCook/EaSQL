package org.cccs.easql.util;

import org.cccs.easql.Cardinality;
import org.cccs.easql.Column;
import org.cccs.easql.Relation;
import org.cccs.easql.Table;
import org.cccs.easql.domain.ColumnMapping;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.cccs.easql.util.ObjectUtils.getNewObject;
import static org.cccs.easql.util.ObjectUtils.setObjectValue;

/**
 * User: boycook
 * Date: 30/06/2011
 * Time: 18:42
 */
public final class ClassUtils {

    private static Map<Class, String[]> columnNames;
    private static Map<Class, ColumnMapping[]> columns;
    private static Map<Class, ColumnMapping[]> allColumns;
    private static Map<Class, String> tables;
    private static Map<Class, Column> primaryColumns;
    private static Map<Class, String> primaryColumnNames;
    private static Map<Class, String> uniqueColumnNames;

    static {
        columnNames = new HashMap<Class, String[]>();
        columns = new HashMap<Class, ColumnMapping[]>();
        allColumns = new HashMap<Class, ColumnMapping[]>();
        tables = new HashMap<Class, String>();
        primaryColumns = new HashMap<Class, Column>();
        primaryColumnNames = new HashMap<Class, String>();
        uniqueColumnNames = new HashMap<Class, String>();
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
        for (Field field : c.getFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                columns.add(getColumnName(field));
            }
        }
        return columns.toArray(new String[columns.size()]);
    }

    public static ColumnMapping[] getColumns(Class c) {
        ColumnMapping[] tempColumns = columns.get(c);
        if (tempColumns == null) {
            tempColumns = getColumnsForClass(c);
            columns.put(c, tempColumns);
        }
        return tempColumns;
    }

    private static ColumnMapping[] getColumnsForClass(Class c) {
        Collection<ColumnMapping> columns = new ArrayList<ColumnMapping>();
        for (Field field : c.getFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                columns.add(new ColumnMapping(field, getColumnName(field)));
            }
        }
        return columns.toArray(new ColumnMapping[columns.size()]);
    }

    public static ColumnMapping[] getAllColumns(Class c) {
        ColumnMapping[] tempColumns = allColumns.get(c);
        if (tempColumns == null) {
            tempColumns = getAllColumnsForClass(c);
            allColumns.put(c, tempColumns);
        }
        return tempColumns;
    }

    private static ColumnMapping[] getAllColumnsForClass(Class c) {
        Collection<ColumnMapping> columns = new ArrayList<ColumnMapping>();
        for (Field field : c.getFields()) {
            Column column = field.getAnnotation(Column.class);
            Relation relation = field.getAnnotation(Relation.class);
            if (column != null) {
                columns.add(new ColumnMapping(field, getColumnName(field)));
            }
            if (relation != null && relation.cardinality().equals(Cardinality.MANY_TO_ONE)) {
                columns.add(new ColumnMapping(field, relation.key()));
            }
        }
        return columns.toArray(new ColumnMapping[columns.size()]);
    }


    public static String getPrimaryColumnName(Class c) {
        String column = primaryColumnNames.get(c);
        if (column == null) {
            column = getPrimaryNameForClass(c);
            primaryColumnNames.put(c, column);
        }
        return column;
    }

    public static String getUniqueColumnName(Class c) {
        String column = uniqueColumnNames.get(c);
        if (column == null) {
            column = getUniqueNameForClass(c);
            uniqueColumnNames.put(c, column);
        }
        return column;
    }

    public static Column getPrimaryColumn(Class c) {
        Column column = primaryColumns.get(c);
        if (column == null) {
            column = getPrimaryColumnForClass(c);
            primaryColumns.put(c, column);
        }
        return column;
    }

    private static Column getPrimaryColumnForClass(Class c) {
        for (Field field : c.getFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null && column.primaryKey()) {
                return column;
            }
        }
        return null;
    }

    private static String getPrimaryNameForClass(Class c) {
        for (Field field : c.getFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null && column.primaryKey()) {
                return isNotEmpty(column.name()) ? column.name() : field.getName();
            }
        }
        return null;
    }

    private static String getUniqueNameForClass(Class c) {
        for (Field field : c.getFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null && column.unique()) {
                return isNotEmpty(column.name()) ? column.name() : field.getName();
            }
        }
        return null;
    }

    public static boolean hasRelations(Class c, Cardinality cardinality) {
        for (Field field : c.getFields()) {
            Relation relation = field.getAnnotation(Relation.class);
            if (relation != null && relation.cardinality().equals(cardinality)) {
                return true;
            }
        }
        return false;
    }

    public static String getColumnName(Field field) {
        Column column = field.getAnnotation(Column.class);
        return column != null && isNotEmpty(column.name()) ? column.name() : field.getName();
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

    public static Field[] getRelationFields(Class c, Cardinality cardinality) {
        Collection<Field> relations = new ArrayList<Field>();
        for (Field field : c.getFields()) {
            Relation relation = field.getAnnotation(Relation.class);
            if (relation != null && relation.cardinality().equals(cardinality)) {
                relations.add(field);
            }
        }
        return relations.toArray(new Field[relations.size()]);
    }

    public static Field getRelatedField(Class c, Class r) {
        for (Field field : r.getFields()) {
            if (field.getType().equals(c)) {
                return field;
            }
        }
        return null;
    }

    @SuppressWarnings({"unchecked"})
    public static String getTableName(Class c) {
        String name = tables.get(c);
        if (name == null) {
            Table table = (Table) c.getAnnotation(Table.class);
            name = table != null && isNotEmpty(table.name()) ? table.name() : c.getSimpleName();
            tables.put(c, name);
        }
        return name;
    }

    //TODO: consider refactor
    public static ColumnMapping[] getExtractionMappings(Class c, boolean loadRelations) {
        Collection<ColumnMapping> columns = new ArrayList<ColumnMapping>();
        Object o = getNewObject(c);

        for (Field field : c.getFields()) {
            Column column = field.getAnnotation(Column.class);
            Relation relation = field.getAnnotation(Relation.class);
            if (column != null) {
                columns.add(new ColumnMapping(field, getColumnName(field), "", o));
            } else if (relation != null) {
                if (relation.cardinality().equals(Cardinality.MANY_TO_ONE)) {
                    if (loadRelations) {
                        ColumnMapping[] relatedColumns = getColumns(field.getType());
                        Object relatedO = getNewObject(field.getType());

                        for (ColumnMapping relatedColumn : relatedColumns) {
                            columns.add(new ColumnMapping(relatedColumn.field, relation.name() + "_" + relatedColumn.columnName, "", relatedO));
                            setObjectValue(field, o, relatedO);
                        }
                    }
                }
            }
        }
        return columns.toArray(new ColumnMapping[columns.size()]);
    }

    public static Class getGenericType(Field field) {
        ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
        return (Class<?>) stringListType.getActualTypeArguments()[0];
    }
}
