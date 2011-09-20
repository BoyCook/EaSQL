package org.cccs.easql.util;

import org.apache.commons.beanutils.PropertyUtils;
import org.cccs.easql.Cardinality;
import org.cccs.easql.Column;
import org.cccs.easql.Relation;
import org.cccs.easql.domain.ExtractionMapping;
import org.cccs.easql.domain.Mapping;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: boycook
 * Date: 11/07/2011
 * Time: 23:58
 */
public final class ObjectUtils {

    public static String arrayAsString(String [] values) {
        final StringBuilder string = new StringBuilder();
        for (String value : values) {
            if (string.length() > 0) {
                string.append(", ");
            }
            string.append(value);
        }
        return string.toString();
    }

    public static Object getValue(Mapping column, Object o) {
        if (column.getMethod() != null) {
            return getMethodValue(column.getMethod(), o);
        } else if (column.getField() != null) {
            return getFieldValue(column.getField(), o);
        } else {
            return null;
        }
    }

    public static Object getFieldValue(Field field, Object o) {
        Object value = null;
        try {
            value = field.get(o);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static Object getMethodValue(Method method, Object o) {
        Object value = null;
        try {
            value = method.invoke(o);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
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

    public static long getPrimaryValueAsLong(Object o) {
        return (Long) getPrimaryValue(o);
    }

    public static Object getPrimaryValue(Object o) {
        Class c = o.getClass();
        Object primary = null;
        for (Field field : c.getFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null && column.primaryKey()) {
                primary = getFieldValue(field, o);
            }
        }
        for (Method method : c.getMethods()) {
            Column column = method.getAnnotation(Column.class);
            if (column != null && column.primaryKey()) {
                primary = getMethodValue(method, o);
            }
        }
        return primary;
    }

    public static void setValue(Field field, Object o, Object value) {
        try {
            field.set(o, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void setValue(Method method, Object o, Object value) {
        try {
            method.invoke(o, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void setValue(ExtractionMapping mapping, Object value) {
        try {
            final PropertyDescriptor property = PropertyUtils.getPropertyDescriptor(mapping.object, mapping.property);
            if (property != null) {
                property.getWriteMethod().invoke(mapping.object, value);
            } else {
                System.out.println(mapping.property + " is not a property, trying field");
                setFieldValue(mapping, value);
            }
        } catch (IllegalAccessException e) {
            System.out.println(mapping.property + " is not a property, trying field");
            setFieldValue(mapping, value);
        } catch (InvocationTargetException e) {
            System.out.println(mapping.property + " is not a property, trying field");
            setFieldValue(mapping, value);
        } catch (NoSuchMethodException e) {
            System.out.println(mapping.property + " is not a property, trying field");
            setFieldValue(mapping, value);
        }
    }

    public static void setValue(String name, Object object, Object value) {
        try {
            final PropertyDescriptor property = PropertyUtils.getPropertyDescriptor(object, name);
            property.getWriteMethod().invoke(object, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private static void setFieldValue(ExtractionMapping mapping, Object value) {
        try {
            Field field = value.getClass().getField(mapping.property);
            setValue(field, mapping.object, value);
        } catch (NoSuchFieldException e1) {
            System.out.println(mapping.property + " is not a field");
        }
    }

    public static Object[] getRelatedValues(Object o, Cardinality cardinality) {
        Collection<Object> relations = new ArrayList<Object>();
        Class c = o.getClass();
        Field[] fields = c.getFields();
        for (Field field : fields) {
            Relation relation = field.getAnnotation(Relation.class);
            if (relation != null && relation.cardinality().equals(cardinality)) {
                relations.add(getFieldValue(field, o));
            }
        }
        return relations.toArray(new Object[relations.size()]);
    }

    public static Object getNewObject(Class c) {
        Object o = null;
        if (!c.equals(Integer.TYPE) && !c.equals(Long.TYPE)) {
            try {
                o = c.getConstructor().newInstance();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return o;
    }
}
