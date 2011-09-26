package org.cccs.easql.domain;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * User: boycook
 * Date: 26/09/2011
 * Time: 12:52
 */
public class BaseMapping implements Mapping {

    private final Field field;
    private final Method method;

    public BaseMapping(Field field, Method method) {
        this.field = field;
        this.method = method;
    }

    public Field getField() {
        return this.field;
    }

    public Method getMethod() {
        return this.method;
    }

    public Class getType() {
        if (getField() != null) {
            return getField().getType();
        } else if (getMethod() != null) {
            return getMethod().getReturnType();
        }
        return null;
    }
}
