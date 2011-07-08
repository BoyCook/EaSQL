package org.cccs.easql.execution;

import org.cccs.easql.Cardinality;
import org.cccs.easql.Column;
import org.cccs.easql.Relation;

import java.lang.reflect.Field;
import java.util.Map;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.cccs.easql.util.ReflectionUtils.*;

/**
 * User: boycook
 * Date: 15/06/2011
 * Time: 15:39
 */
//TODO: consider making this a BaseClass
@SuppressWarnings({"unchecked"})
public final class ReflectiveSQLGenerator {

    //Template SQL
    private final static String INSERT_TEMPLATE = "INSERT INTO %s (%s) VALUES (%s);";
    private final static String SELECT_TEMPLATE = "SELECT %s FROM %s";
    private final static String SELECT_TEMPLATE_RELATIONS = "SELECT %s FROM %s %s";
    private final static String UPDATE_TEMPLATE = "UPDATE %s set %s;";
    private final static String DELETE_TEMPLATE = "DELETE FROM %s WHERE %s;";
    private final static String CREATE_TEMPLATE = "CREATE TABLE %s (%s);";
    private final static String SEQUENCE_TEMPLATE = "(SELECT NEXT VALUE FOR %s FROM %s)";

    public static String generateInsertSQL(Object o) {
        Class c = o.getClass();
        String tableName = getTableName(c);

        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();

        Field[] fields = c.getFields();
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            Relation relation = field.getAnnotation(Relation.class);

            if (column != null) {
                String columnName = getColumnName(field);
                Object columnValue = getFieldValue(field, o);

                if (column.mandatory() && columnValue == null) {
                    throw new IllegalArgumentException(columnName + " must be specified");
                } else if (column.primaryKey()) {
                    if (isNotEmpty(column.sequence())) {
                        appendInsertValue(columns, values, columnName, format(SEQUENCE_TEMPLATE, column.sequence(), tableName));
                    } else if (columnValue == null) {
                        throw new IllegalArgumentException("Primary key for " + columnName + " must be specified");
                    } else {
                        appendInsertValue(columns, values, columnName, value(columnValue));
                    }
                } else if (columnValue != null) {
                    appendInsertValue(columns, values, columnName, value(columnValue));
                }
            } else if (relation != null) {
                if (relation.cardinality().equals(Cardinality.MANY_TO_ONE)) {
                    appendInsertValue(columns, values, relation.key(), "%s");
                }
            }
        }

        return format(INSERT_TEMPLATE, tableName, columns.toString(), values.toString());
    }

    public static String generateSelectSQL(Class c) {
        return generateSelectSQL(c, false);
    }

    public static String generateSelectSQL(Class c, boolean loadRelations) {
        Field[] fields = c.getFields();
        String tableName = getTableName(c);
        StringBuilder select = new StringBuilder();
        StringBuilder joins = new StringBuilder();

        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            Relation relation = field.getAnnotation(Relation.class);

            if (column != null) {
                appendColumn(select, getColumnName(field));
            } else if (relation != null) {
                if (relation.cardinality().equals(Cardinality.MANY_TO_ONE)) {
                    if (loadRelations) {
                        String[] joinColumns = getColumnNames(field.getType());
                        String joinTable = getTableName(field.getType());
                        for (String joinColumn : joinColumns) {
                            appendColumn(select, getJoinColumnName(relation.name(), joinColumn));
                        }
                        String primaryColumn = getPrimaryColumn(field.getType());
                        joins.append(format("LEFT OUTER JOIN %s %s ON %s.%s = %s.%s", joinTable, relation.name(), tableName, primaryColumn, relation.name(), primaryColumn));
                    } else {
                        appendColumn(select, relation.key());
                    }
                }
            }
        }

        if (loadRelations) {
            return format(SELECT_TEMPLATE_RELATIONS, select.toString(), tableName, joins.toString());
        } else {
            return format(SELECT_TEMPLATE, select.toString(), tableName);
        }
    }

    public static String generateUpdateSQL(Object o) {
        Class c = o.getClass();
        StringBuilder values = new StringBuilder();

        Field[] fields = c.getFields();
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);

            if (column != null) {
                String columnName = getColumnName(field);
                Object columnValue = getFieldValue(field, o);

                if (column.mandatory() && columnValue == null) {
                    throw new IllegalArgumentException(columnName + " must be specified");
                } else if (columnValue != null && !column.primaryKey()) {
                    if (values.length() > 0) {
                        values.append(", ");
                    }

                    values.append(columnName);
                    values.append(" = ");
                    values.append(value(columnValue));
                }
            }
        }

        return format(UPDATE_TEMPLATE, getTableName(c), values.toString());
    }

    public static String generateDeleteSQL(Object o) {
        Class c = o.getClass();
        Field[] fields = c.getFields();
        String where = "";
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);

            if (column != null && column.primaryKey()) {
                Object columnValue = getFieldValue(field, o);
                where = getColumnName(field) + " = " + columnValue ;
            }
        }

        return format(DELETE_TEMPLATE, getTableName(c), where);
    }

    public static String generateCreateSQL(Class c) {
        Field[] fields = c.getFields();
        StringBuilder columns = new StringBuilder();

        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            Relation relation = field.getAnnotation(Relation.class);

            if (column != null) {
                if (column.primaryKey()) {
                    appendColumn(columns, format("%s identity NOT NULL PRIMARY KEY", getColumnName(field)));
                } else {
                    appendColumn(columns, format("%s %s", getColumnName(field), getColumnType(field)));
                }
            } else if (relation != null && relation.cardinality().equals(Cardinality.MANY_TO_ONE)) {
                appendColumn(columns, format("%s %s", relation.key(), "INTEGER"));
            }
        }

        return format(CREATE_TEMPLATE, getTableName(c), columns.toString());
    }

    public static String generateDropSQL(Object o) {
        throw new UnsupportedOperationException("Drop is not yet supported");
    }

    public static String generateWhere(Map<String, String> whereClauses) {
        StringBuilder where = new StringBuilder();

        for (String key : whereClauses.keySet()) {
            String value = whereClauses.get(key);

            if (where.length() == 0) {
                where.append(" WHERE ");
            } else {
                where.append(" AND ");
            }

            where.append(key);
            where.append(" = '");
            where.append(value);
            where.append("' ");
        }

        return where.toString();
    }

    private static void appendColumn(StringBuilder select, String column) {
        if (select.length() > 0) {
            select.append(", ");
        }
        select.append(column);
    }

    private static void appendInsertValue(StringBuilder columns, StringBuilder values, String column, String value) {
        if (columns.length() > 0) {
            columns.append(", ");
            values.append(", ");
        }
        columns.append(column);
        values.append(value);
    }
}
