package org.cccs.easql.util;

import org.cccs.easql.Column;
import org.cccs.easql.Table;
import org.cccs.easql.domain.ColumnMapping;
import org.cccs.easql.domain.ExtractionMapping;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.cccs.easql.util.ClassUtils.*;

/**
 * User: boycook
 * Date: 13/09/2011
 * Time: 17:49
 */
public class ClassCache {

    private static Map<Class, String[]> columnNames;
    private static Map<Class, ColumnMapping[]> columns;
    private static Map<Class, ExtractionMapping[]> extractionColumns;
    private static Map<Class, ExtractionMapping[]> allExtractionColumns;
    private static Map<Class, String> tables;
    private static Map<Class, Column> primaryColumns;
    private static Map<Class, String> primaryColumnNames;
    private static Map<Class, String> uniqueColumnNames;

    static {
        columnNames = new HashMap<Class, String[]>();
        columns = new HashMap<Class, ColumnMapping[]>();
        extractionColumns = new HashMap<Class, ExtractionMapping[]>();
        allExtractionColumns = new HashMap<Class, ExtractionMapping[]>();
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

    public static ColumnMapping[] getColumns(Class c) {
        ColumnMapping[] tempColumns = columns.get(c);
        if (tempColumns == null) {
            tempColumns = getColumnsForClass(c);
            columns.put(c, tempColumns);
        }
        return tempColumns;
    }

    public static ExtractionMapping[] getExtractionColumns(Class c) {
        ExtractionMapping[] tempColumns = extractionColumns.get(c);
        if (tempColumns == null) {
            tempColumns = getExtractionColumnsForClass(c);
            extractionColumns.put(c, tempColumns);
        }
        return tempColumns;
    }

    public static ExtractionMapping[] getAllColumns(Class c) {
        ExtractionMapping[] tempColumns = allExtractionColumns.get(c);
        if (tempColumns == null) {
            tempColumns = getAllColumnsForClass(c);
            allExtractionColumns.put(c, tempColumns);
        }
        return tempColumns;
    }

    // Single value caches:

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

    public static String getTableName(Class c) {
        String name = tables.get(c);
        if (name == null) {
            Table table = (Table) c.getAnnotation(Table.class);
            name = table != null && isNotEmpty(table.name()) ? table.name() : c.getSimpleName();
            tables.put(c, name);
        }
        return name;
    }
}
