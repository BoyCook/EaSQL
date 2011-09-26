package org.cccs.easql.domain;

import org.cccs.easql.Relation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * User: boycook
 * Date: 13/09/2011
 * Time: 11:42
 */
public class RelationMapping extends BaseMapping implements Mapping {

    public final String property;
    public final Relation relation;

    public RelationMapping(String property, Relation relation, Field field, Method method) {
        super(field, method);
        this.property = property;
        this.relation = relation;
    }

    public Class getGenericType() {
        if (getField() != null) {
            return org.cccs.easql.util.ClassUtils.getGenericType(getField());
        } else if (getMethod() != null) {
            return org.cccs.easql.util.ClassUtils.getGenericType(getMethod());
        }
        return null;
    }
}
