package org.cccs.easql.util;

import org.apache.commons.beanutils.PropertyUtils;
import org.cccs.easql.Cardinality;
import org.cccs.easql.domain.ColumnMapping;
import org.cccs.easql.domain.Mapping;
import org.cccs.easql.domain.RelationMapping;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import static org.cccs.easql.cache.ClassCache.getColumnMappings;
import static org.cccs.easql.util.ClassUtils.getRelations;

/**
 * User: boycook
 * Date: 11/07/2011
 * Time: 23:58
 */
public final class ObjectUtils {

    public static String arrayAsString(String[] values) {
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
            return getValue(column.getMethod(), o);
        } else if (column.getField() != null) {
            return getValue(column.getField(), o);
        } else {
            return null;
        }
    }

    public static Object getValue(Field field, Object o) {
        Object value = null;
        try {
            value = field.get(o);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static Object getValue(Method method, Object o) {
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
        Object value = null;
        ColumnMapping[] columns = getColumnMappings(o.getClass());
        for (ColumnMapping column : columns) {
            if (column.column.primaryKey()) {
                value = getValue(column, o);
            }
        }
        return value;
    }

    public static Object getUniqueValue(Object o) {
        Object value = null;
        ColumnMapping[] columns = getColumnMappings(o.getClass());
        for (ColumnMapping column : columns) {
            if (column.column.unique()) {
                value = getValue(column, o);
            }
        }
        return value;
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

    public static void setValue(String name, Object object, Object value) {
        try {
            setPropertyValue(name, object, value);
        } catch (NullPointerException e) {
            setFieldValue(name, object, value);
        } catch (InvocationTargetException e) {
            setFieldValue(name, object, value);
        } catch (NoSuchMethodException e) {
            setFieldValue(name, object, value);
        } catch (IllegalAccessException e) {
            setFieldValue(name, object, value);
        }
    }

    public static void setPropertyValue(String name, Object object, Object value) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        final PropertyDescriptor property = PropertyUtils.getPropertyDescriptor(object, name);
        property.getWriteMethod().invoke(object, value);
    }

    private static void setFieldValue(String name, Object object, Object value) {
        try {
            Field field = object.getClass().getField(name);
            setValue(field, object, value);
        } catch (NoSuchFieldException ignored) {
        }
    }

    public static Object[] getRelatedValues(Object o, Cardinality cardinality) {
        Collection<Object> values = new ArrayList<Object>();
        RelationMapping[] relations = getRelations(o.getClass(), cardinality);
        for (RelationMapping relation : relations) {
            values.add(getValue(relation, o));
        }
        return values.toArray(new Object[values.size()]);
    }

    public static Object getNewObject(Class c) {
        Object o = null;
        //Not for primitives
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

    @SuppressWarnings({"unchecked"})
    public static void compareLists(Collection original, Collection updated, Collection addRelation, Collection removeRelation) {
        for (Object o : original) {
            if (!updated.contains(o)) {
                //Remove relation
                removeRelation.add(o);
            }
        }

        for (Object o : updated) {
            if (!original.contains(o)) {
                //Add relation
                addRelation.add(o);
            }
        }
    }
}
