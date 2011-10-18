package org.cccs.easql.util;

import org.apache.commons.beanutils.PropertyUtils;
import org.cccs.easql.domain.TableColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import static org.cccs.easql.util.ClassUtils.getIdColumn;

/**
 * User: boycook
 * Date: 11/07/2011
 * Time: 23:58
 */
public final class ObjectUtils {

    private static final Logger log = LoggerFactory.getLogger(ObjectUtils.class);

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

    public static Object getValue(TableColumn column, Object o) {
        if (column.getObject().getClass().equals(Method.class)) {
            return getValue((Method) column.getObject(), o);
        } else if (column.getObject().getClass().equals(Field.class)) {
            return getValue((Field) column.getObject(), o);
        } else {
            return null;
        }
    }

    public static Object getValue(Field field, Object o) {
        Object value = null;
        try {
            value = field.get(o);
        } catch (IllegalAccessException e) {
            log.error("Error getting object value", e);
        }
        return value;
    }

    public static Object getValue(Method method, Object o) {
        Object value = null;
        try {
            value = method.invoke(o);
        } catch (IllegalAccessException e) {
            log.error("Error getting object value", e);
        } catch (InvocationTargetException e) {
            log.error("Error getting object value", e);
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
        return getValue(getIdColumn(o.getClass()), o);
    }

    public static void setValue(Field field, Object o, Object value) {
        try {
            field.set(o, value);
        } catch (IllegalAccessException e) {
            log.error("Error setting object value", e);
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

    public static Object getNewObject(Class c) {
        Object o = null;
        //Not for primitives
        if (!c.equals(Integer.TYPE) && !c.equals(Long.TYPE)) {
            try {
                o = c.getConstructor().newInstance();
            } catch (InvocationTargetException e) {
                log.error("Error getting new object", e);
            } catch (NoSuchMethodException e) {
                log.error("Error getting new object", e);
            } catch (InstantiationException e) {
                log.error("Error getting new object", e);
            } catch (IllegalAccessException e) {
                log.error("Error getting new object", e);
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
