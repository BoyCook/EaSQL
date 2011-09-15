package org.cccs.easql.domain;

import org.cccs.easql.Relation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * User: boycook
 * Date: 13/09/2011
 * Time: 11:42
 */
public class RelationMapping implements Mapping {

    public final Relation relation;
    private final Field field;
    private final Method method;

    public RelationMapping(Relation relation, Field field, Method method) {
        this.relation = relation;
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
}