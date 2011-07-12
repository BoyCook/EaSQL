package org.cccs.easql.util;

import org.cccs.easql.Cardinality;
import org.cccs.easql.Column;
import org.cccs.easql.Relation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: boycook
 * Date: 11/07/2011
 * Time: 23:58
 */
public final class ObjectUtils {

    public static Object getFieldValue(Field field, Object o) {
        Object value = null;

        try {
            value = field.get(o);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return value;
    }

    public static String value(Object value) {
        if (value instanceof Number) {
            return value.toString();
        } else if (value instanceof Boolean) {
            return Boolean.valueOf(value.toString()) ? "1" : "0";
        } else {
            return "'" + value.toString() + "'";
        }
    }

    public static Object getPrimaryValue(Object o) {
        Class c = o.getClass();
        Field[] fields = c.getFields();
        Object primary = null;
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column != null && column.primaryKey()) {
                primary = getFieldValue(field, o);
            }
        }
        return primary;
    }

    public static void setObjectValue(Field field, Object o, Object value) {
        try {
            field.set(o, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Object[] getRelations(Object o) {
        Collection<Object> relations = new ArrayList<Object>();
        Class c = o.getClass();
        Field[] fields = c.getFields();
        for (Field field : fields) {
            Relation relation = field.getAnnotation(Relation.class);
            if (relation != null && relation.cardinality().equals(Cardinality.MANY_TO_ONE)) {
                relations.add(getFieldValue(field, o));
            }
        }
        return relations.toArray(new Object[relations.size()]);
    }

    public static Object getObject(Class c) {
        Object o = null;
        try {
            o = c.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return o;
    }
}
