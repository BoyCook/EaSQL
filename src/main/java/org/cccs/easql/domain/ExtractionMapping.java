package org.cccs.easql.domain;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * User: boycook
 * Date: 29/06/2011
 * Time: 00:32
 */
public class ExtractionMapping {

//    public final String property;
    public final Field field;
    public final Method getter;
    public final Method setter;
    public final String name;
    public Object object;

    public ExtractionMapping(Field field, String name) {
        this(field, null, null, name, null);
    }

    public ExtractionMapping(Field field, String name, Object object) {
        this(field, null, null, name, object);
    }

    public ExtractionMapping(Method getter, Method setter, String name) {
        this(null, getter, setter, name, null);
    }

    public ExtractionMapping(Method getter, Method setter, String name, Object object) {
        this(null, getter, setter, name, object);
    }

    public ExtractionMapping(Field field, Method getter, Method setter, String name, Object object) {
        this.field = field;
        this.getter = getter;
        this.setter = setter;
        this.name = name;
        this.object = object;
    }
}
