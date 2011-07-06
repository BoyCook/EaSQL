package org.cccs.easql;

import java.lang.reflect.Field;

/**
 * User: boycook
 * Date: 29/06/2011
 * Time: 00:32
 */
public class DBField {

    public final Field field;
    public final Column column;
    public final String columnName;
    public final String dataType;

    public DBField(Field field, Column column, String columnName, String dataType) {
        this.field = field;
        this.column = column;
        this.columnName = columnName;
        this.dataType = dataType;
    }
}
