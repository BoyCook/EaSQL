package org.cccs.easql.domain;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinTable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import static org.cccs.easql.util.ClassUtils.stripName;

/**
 * User: boycook
 * Date: 29/09/2011
 * Time: 12:24
 */
public class TableColumn {

    //This is a Field or Method
    private final Object object;

    public TableColumn(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public Class getType() {
        if (getObject().getClass().equals(Method.class)) {
            return ((Method) getObject()).getReturnType();
        } else if (getObject().getClass().equals(Field.class)) {
            return ((Field) getObject()).getType();
        }
        return null;
    }

    public Class getGenericType() {
        if (getObject().getClass().equals(Method.class)) {
            return org.cccs.easql.util.ClassUtils.getGenericType((Method) getObject());
        } else if (getObject().getClass().equals(Field.class)) {
            return org.cccs.easql.util.ClassUtils.getGenericType((Field) getObject());
        }
        return null;
    }

    public String getDBType() {
        return org.cccs.easql.util.ClassUtils.getColumnType(getType());
    }

    //TODO make variable
    public String getName() {
        return org.cccs.easql.util.ClassUtils.getColumnName(getColumn(), getMember());
    }

    public String getProperty() {
        if (getObject().getClass().equals(Method.class)) {
            return stripName(((Method) getObject()).getName());
        } else if (getObject().getClass().equals(Field.class)) {
            return ((Field) getObject()).getName();
        }
        return null;
    }

    public Object getValue(Object o) {
        if (getObject().getClass().equals(Method.class)) {
            return org.cccs.easql.util.ObjectUtils.getValue((Method) getObject(), o);
        } else if (getObject().getClass().equals(Field.class)) {
            return org.cccs.easql.util.ObjectUtils.getValue((Field) getObject(), o);
        } else {
            return null;
        }
    }

    public Member getMember() {
        return (Member) object;
    }

    public AccessibleObject getAccessibleObject() {
        return (AccessibleObject) object;
    }

    public Column getColumn() {
        return (Column) getAnnotation(Column.class);
    }

    public JoinTable getJoinTable() {
        return (JoinTable) getAnnotation(JoinTable.class);
    }

    public GeneratedValue getGeneratedValue() {
        return (GeneratedValue) getAnnotation(GeneratedValue.class);
    }

    @SuppressWarnings({"unchecked"})
    private Annotation getAnnotation(Class c) {
        return getAccessibleObject().getAnnotation(c);
    }
}
