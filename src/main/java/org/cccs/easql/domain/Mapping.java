package org.cccs.easql.domain;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * User: boycook
 * Date: 13/09/2011
 * Time: 18:39
 */
public interface Mapping {
    public Field getField();
    public Method getMethod();
}
