package org.cccs.easql.domain;

import org.cccs.easql.Column;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * User: boycook
 * Date: 13/09/2011
 * Time: 15:57
 */
public class ColumnMapping extends BaseMapping implements Mapping {

    public final String property;
    public final String name;
    public final Column column;

    public ColumnMapping(String property, String name, Column column, Field field, Method method) {
        super(field, method);
        this.property = property;
        this.name = name;
        this.column = column;
    }
}
