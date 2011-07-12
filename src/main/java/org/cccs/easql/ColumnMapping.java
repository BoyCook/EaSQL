package org.cccs.easql;

import java.lang.reflect.Field;
import static org.cccs.easql.util.ObjectUtils.getObject;


/**
 * User: boycook
 * Date: 29/06/2011
 * Time: 00:32
 */
public class ColumnMapping {

    public final Field field;
    public final String columnName;
    public final String dataType;
    public Object object;

    public ColumnMapping(Field field, String columnName, String dataType) {
        this(field, columnName, dataType, getObject(field.getType()));
    }

    public ColumnMapping(Field field, String columnName, String dataType, Object object) {
        this.field = field;
        this.columnName = columnName;
        this.dataType = dataType;
        this.object = object;
    }
}
