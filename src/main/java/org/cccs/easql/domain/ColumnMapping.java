package org.cccs.easql.domain;

import org.cccs.easql.Column;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * User: boycook
 * Date: 13/09/2011
 * Time: 15:57
 */
public class ColumnMapping implements Mapping {

    public final String property;
    public final String name;
    public final Column column;
    private final Field field;
    private final Method method;

    public ColumnMapping(String property, String name, Column column, Field field, Method method) {
        this.property = property;
        this.name = name;
        this.column = column;
        this.field = field;
        this.method = method;
    }

    @Override
    public Field getField() {
        return this.field;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public Class getType() {
        if (getField() != null) {
            return getField().getType();
        } else if (getMethod() != null) {
            return getMethod().getReturnType();
        }
        return null;
    }
}
