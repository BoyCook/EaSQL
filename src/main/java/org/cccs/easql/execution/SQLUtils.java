package org.cccs.easql.execution;

import org.cccs.easql.Cardinality;
import org.cccs.easql.Relation;
import org.cccs.easql.domain.ColumnMapping;
import org.cccs.easql.domain.LinkTable;
import org.cccs.easql.domain.RelationMapping;
import org.cccs.easql.domain.Sequence;

import java.util.Map;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.cccs.easql.util.ClassCache.*;
import static org.cccs.easql.util.ClassUtils.getColumnType;
import static org.cccs.easql.util.ClassUtils.getRelations;
import static org.cccs.easql.util.ObjectUtils.*;

/**
 * User: boycook
 * Date: 15/06/2011
 * Time: 15:39
 */
//TODO: consider making this a BaseClass
public final class SQLUtils {

    //Template SQL
    private final static String INSERT_TEMPLATE = "INSERT INTO %s (%s) VALUES (%s);";
    private final static String SELECT_TEMPLATE = "SELECT %s FROM %s";
    private final static String SELECT_TEMPLATE_RELATIONS = "SELECT %s FROM %s %s";
    private final static String UPDATE_TEMPLATE = "UPDATE %s set %s WHERE %s;";
    private final static String DELETE_TEMPLATE = "DELETE FROM %s;";
    private final static String DELETE_OBJECT_TEMPLATE = "DELETE FROM %s WHERE %s;";
    private final static String CREATE_TEMPLATE = "CREATE TABLE %s (%s);";
    private final static String SELECT_SEQUENCE_TEMPLATE = "SELECT NEXT VALUE FOR %s FROM %s";
    private final static String CREATE_SEQUENCE_TEMPLATE = "CREATE SEQUENCE %s AS BIGINT START WITH %d INCREMENT BY %d;";
    private final static String OUTER_JOIN = "LEFT OUTER JOIN %s %s ON %s.%s = %s.%s";
    private final static String INNER_JOIN = "INNER JOIN %s %s ON %s.%s = %s.%s AND %s.%s = %d";

    public static String generateInsertSQL(Object o) {
        String tableName = getTableName(o.getClass());

        StringBuilder insertColumns = new StringBuilder();
        StringBuilder values = new StringBuilder();

        final ColumnMapping[] columnMappings = getColumns(o.getClass());
        final RelationMapping[] relations = getRelations(o.getClass(), Cardinality.MANY_TO_ONE);

        for (ColumnMapping column : columnMappings) {
            Object columnValue = getValue(column, o);
            if (column.column.primaryKey()) {
                if (isNotEmpty(column.column.sequence())) {
                    appendInsertValue(insertColumns, values, column.name, Schema.getSequence(column.column.sequence()).getValue());
                } else {
                    appendInsertValue(insertColumns, values, column.name, value(columnValue));
                }
            } else if (columnValue != null) {
                appendInsertValue(insertColumns, values, column.name, value(columnValue));
            }
        }

        for (RelationMapping relation : relations) {
            appendInsertValue(insertColumns, values, relation.relation.key(), "%s");
        }

        return format(INSERT_TEMPLATE, tableName, insertColumns.toString(), values.toString());
    }

    public static String generateSelectSQL(LinkTable linkTable) {
        return format(SELECT_TEMPLATE, "*", linkTable.name);
    }

    public static String generateSelectSQL(Class c) {
        return generateSelectSQL(c, false);
    }

    public static String generateSelectSQL(final Class c, boolean loadRelations) {
        String tableName = getTableName(c);
        StringBuilder select = new StringBuilder();
        StringBuilder joins = new StringBuilder();

        final String[] columns = getColumnNames(c);
        final RelationMapping[] relations = getRelations(c, Cardinality.MANY_TO_ONE);

        for (String column : columns) {
            appendColumn(select, column);
        }

        for (RelationMapping relation : relations) {
            if (loadRelations) {
                String[] joinColumns = getColumnNames(relation.getField().getType());
                String joinTable = getTableName(relation.getField().getType());
                for (String joinColumn : joinColumns) {
                    appendColumn(select, getJoinColumnName(relation.relation.name(), joinColumn));
                }
                String primaryColumn = getPrimaryColumnName(relation.getField().getType());
                joins.append(format(OUTER_JOIN, joinTable, relation.relation.name(), tableName, relation.relation.key(), relation.relation.name(), primaryColumn));
            } else {
                appendColumn(select, relation.relation.key());
            }
        }

        if (loadRelations) {
            return format(SELECT_TEMPLATE_RELATIONS, select.toString(), tableName, joins.toString());
        } else {
            return format(SELECT_TEMPLATE, select.toString(), tableName);
        }
    }

    public static String generateSelectSQLForManyToMany(Class aClass, Relation relation, long id) {
        final StringBuilder sql = new StringBuilder();
        sql.append(format(SELECT_TEMPLATE, arrayAsString(getColumnNames(aClass)), getTableName(aClass) + " a "));

        if (relation.end().equals(Relation.End.LEFT)) {
            sql.append(format(INNER_JOIN, relation.linkTable(), "b", "a", getPrimaryColumnName(aClass), "b", relation.linkedBy()[1], "b", relation.linkedBy()[0], id));
        } else {
            sql.append(format(INNER_JOIN, relation.linkTable(), "b", "a", getPrimaryColumnName(aClass), "b", relation.linkedBy()[0], "b", relation.linkedBy()[1], id));
        }

        sql.append(";");
        return sql.toString();
    }

    public static String generateUpdateSQL(Object o) {
        Class c = o.getClass();
        StringBuilder values = new StringBuilder();
        final ColumnMapping[] columnMappings = getColumns(o.getClass());

        for (ColumnMapping column : columnMappings) {
            Object columnValue = getValue(column, o);
            if (columnValue != null && !column.column.primaryKey()) {
                appendUpdateValue(values, column.name, value(columnValue));
            }
        }

        String where = getPrimaryColumnName(o.getClass()) + " = " + getPrimaryValueAsLong(o);
        return format(UPDATE_TEMPLATE, getTableName(c), values.toString(), where);
    }

    public static String generateUpdateSQLForRelation(Object o, Relation relation) {
        StringBuilder values = new StringBuilder();
        values.append(relation.key());
        values.append(" = ");
        values.append("%s");
        String where = getPrimaryColumnName(o.getClass()) + " = " + getPrimaryValueAsLong(o);
        return format(UPDATE_TEMPLATE, getTableName(o.getClass()), values.toString(), where);
    }

    public static String generateDeleteSQL(Object o) {
        Class c = o.getClass();
        String where = getPrimaryColumnName(o.getClass()) + " = " + getPrimaryValue(o);
        return format(DELETE_OBJECT_TEMPLATE, getTableName(c), where);
    }

    public static String generateDeleteSQL(Class c) {
        return format(DELETE_TEMPLATE, getTableName(c));
    }

    public static String generateSequenceSQL(Sequence sequence) {
        return format(CREATE_SEQUENCE_TEMPLATE, sequence.getName(), sequence.getStartsWith(), sequence.getIncrementBy());
    }

    public static String generateCreateSQL(LinkTable table) {
        StringBuilder columns = new StringBuilder();
        columns.append(table.leftKey);
        columns.append(" INTEGER, ");
        columns.append(table.rightKey);
        columns.append(" INTEGER");
        return format(CREATE_TEMPLATE, table.name, columns.toString());
    }

    public static String generateCreateSQL(Class c) {
        StringBuilder columns = new StringBuilder();

        final ColumnMapping[] columnMappings = getColumns(c);
        final RelationMapping[] relations = getRelations(c, Cardinality.MANY_TO_ONE);

        for (ColumnMapping column : columnMappings) {
            if (column.column.primaryKey()) {
                appendColumn(columns, format("%s identity NOT NULL PRIMARY KEY", column.name));
            } else {
                appendColumn(columns, format("%s %s", column.name, getColumnType(column)));
            }
        }

        for (RelationMapping relation : relations) {
            appendColumn(columns, format("%s %s", relation.relation.key(), "INTEGER"));
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

    private static String getJoinColumnName(String joinTable, String joinColumn) {
        return joinTable + "." + joinColumn + " as " + joinTable + "_" + joinColumn;
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

    private static void appendUpdateValue(StringBuilder values, String column, String value) {
        if (values.length() > 0) {
            values.append(", ");
        }
        values.append(column);
        values.append(" = ");
        values.append(value);
    }
}
