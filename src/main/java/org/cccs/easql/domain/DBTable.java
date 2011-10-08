package org.cccs.easql.domain;

import static org.cccs.easql.util.ClassUtils.getTableName;

/**
 * User: boycook
 * Date: 29/09/2011
 * Time: 12:37
 */
public class DBTable {

    public final Class c;
    public final TableColumn id;
    public final TableColumn key;
    public final String[] columnNames;
    public final TableColumn[] columns;
    public final TableColumn[] one2one;
    public final TableColumn[] one2many;
    public final TableColumn[] many2one;
    public final TableColumn[] many2many;

    public DBTable(Class c, TableColumn id, TableColumn key, String[] columnNames, TableColumn[] columns, TableColumn[] one2one, TableColumn[] one2many, TableColumn[] many2one, TableColumn[] many2many) {
        this.c = c;
        this.id = id;
        this.key = key;
        this.columnNames = columnNames;
        this.columns = columns;
        this.one2one = one2one;
        this.one2many = one2many;
        this.many2one = many2one;
        this.many2many = many2many;
    }

    public String getName() {
        return getTableName(this.c);
    }
}
