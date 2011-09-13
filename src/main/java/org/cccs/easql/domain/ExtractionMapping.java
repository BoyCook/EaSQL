package org.cccs.easql.domain;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.cccs.easql.util.ObjectUtils.getNewObject;


/**
 * User: boycook
 * Date: 29/06/2011
 * Time: 00:32
 */
public class ExtractionMapping {

    public final Field field;
    public final Method method;
    public final String name;
    public Object object;

    public ExtractionMapping(Field field, Method method, String name) {
        this(field, method, name, null);
    }

    public ExtractionMapping(Field field, Method method, String name, Object object) {
        this.field = field;
        this.method = method;
        this.name = name;
        this.object = object;
    }
}
