package org.cccs.easql.cache;

import org.cccs.easql.domain.DBTable;

import java.util.HashMap;
import java.util.Map;

import static org.cccs.easql.util.ClassUtils.getTableForClass;

/**
 * User: boycook
 * Date: 13/09/2011
 * Time: 17:49
 */
public class ClassCache {

    private static Map<Class, DBTable> tables;

    static {
        tables = new HashMap<Class, DBTable>();
    }

    public static DBTable getTable(Class c) {
        DBTable table = tables.get(c);
        if (table == null) {
            table = getTableForClass(c);
            tables.put(c, table);
        }
        return table;
    }

}
