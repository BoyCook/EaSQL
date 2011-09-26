package org.cccs.easql.util;

import org.cccs.easql.Cardinality;
import org.cccs.easql.Column;
import org.cccs.easql.Relation;
import org.cccs.easql.domain.ColumnMapping;
import org.cccs.easql.domain.ExtractionMapping;
import org.cccs.easql.domain.RelationMapping;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;

import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.cccs.easql.cache.ClassCache.getColumnMappings;
import static org.cccs.easql.cache.ClassCache.getExtractionColumns;
import static org.cccs.easql.util.ObjectUtils.getNewObject;
import static org.cccs.easql.util.ObjectUtils.setValue;

/**
 * User: boycook
 * Date: 30/06/2011
 * Time: 18:42
 */
public final class ClassUtils {

    public static String[] getColumnNamesForClass(Class c) {
        Collection<String> columns = new ArrayList<String>();
        for (Field field : c.getFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                columns.add(getColumnName(column, field));
            }
        }
        for (Method method : c.getMethods()) {
            Column column = method.getAnnotation(Column.class);
            if (column != null) {
                columns.add(getColumnName(column, method));
            }
        }
        return columns.toArray(new String[columns.size()]);
    }

    public static ColumnMapping[] getColumnMappingsForClass(Class c) {
        Collection<ColumnMapping> columns = new ArrayList<ColumnMapping>();
        for (Field field : c.getFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                columns.add(new ColumnMapping(field.getName(), getColumnName(column, field), column, field, null));
            }
        }
        for (Method method : c.getMethods()) {
            Column column = method.getAnnotation(Column.class);
            if (column != null) {
                columns.add(new ColumnMapping(stripName(method), getColumnName(column, method), column, null, method));
            }
        }
        return columns.toArray(new ColumnMapping[columns.size()]);
    }

    public static ExtractionMapping[] getExtractionColumnsForClass(Class c) {
        Collection<ExtractionMapping> columns = new ArrayList<ExtractionMapping>();
        for (Field field : c.getFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                columns.add(new ExtractionMapping(field.getName(), getColumnName(column, field), field.getType()));
            }
        }
        for (Method method : c.getMethods()) {
            Column column = method.getAnnotation(Column.class);
            if (column != null) {
                columns.add(new ExtractionMapping(stripName(method), getColumnName(column, method), method.getReturnType()));
            }
        }
        return columns.toArray(new ExtractionMapping[columns.size()]);
    }

    //TODO: handle methods
    public static ExtractionMapping[] getAllColumnsForClass(Class c) {
        Collection<ExtractionMapping> columns = new ArrayList<ExtractionMapping>();
        for (Field field : c.getFields()) {
            Column column = field.getAnnotation(Column.class);
            Relation relation = field.getAnnotation(Relation.class);
            if (column != null) {
                columns.add(new ExtractionMapping(field.getName(), getColumnName(column, field), field.getType()));
            }
            if (relation != null && relation.cardinality().equals(Cardinality.MANY_TO_ONE)) {
                columns.add(new ExtractionMapping(field.getName(), relation.key(), field.getType()));
            }
        }
        return columns.toArray(new ExtractionMapping[columns.size()]);
    }

    public static ExtractionMapping[] generateExtractionMappings(Class c, boolean loadRelations) {
        Collection<ExtractionMapping> columns = new ArrayList<ExtractionMapping>();
        Object o = getNewObject(c);

        ColumnMapping[] columnMappings = getColumnMappings(c);
        RelationMapping[] relationMappings = getRelations(c, Cardinality.MANY_TO_ONE);

        for (ColumnMapping mapping : columnMappings) {
            columns.add(new ExtractionMapping(mapping.property, mapping.name, mapping.getType(), o));
        }

        if (loadRelations) {
            for (RelationMapping mapping : relationMappings) {
                //Get ExtractionMapping for related object
                ExtractionMapping[] relatedColumns = getExtractionColumns(mapping.getType());
                Object relatedO = getNewObject(mapping.getType());
                //Add correct object to mapping
                for (ExtractionMapping relatedColumn : relatedColumns) {
                    columns.add(new ExtractionMapping(relatedColumn.property, mapping.relation.name() + "_" + relatedColumn.name, relatedColumn.type, relatedO));
                    setValue(mapping.property, o, relatedO);
                }
            }
        }

        return columns.toArray(new ExtractionMapping[columns.size()]);
    }

    //TODO: cache
    public static RelationMapping[] getRelations(final Class c, final Cardinality cardinality) {
        Collection<RelationMapping> relations = new ArrayList<RelationMapping>();
        for (Field field : c.getFields()) {
            Relation relation = field.getAnnotation(Relation.class);
            if (relation != null && relation.cardinality().equals(cardinality)) {
                relations.add(new RelationMapping(field.getName(), relation, field, null));
            }
        }
        for (Method method : c.getMethods()) {
            Relation relation = method.getAnnotation(Relation.class);
            if (relation != null && relation.cardinality().equals(cardinality)) {
                relations.add(new RelationMapping(stripName(method), relation, null, method));
            }
        }
        return relations.toArray(new RelationMapping[relations.size()]);
    }

    //TODO: consider usage of this
    public static <T extends java.lang.annotation.Annotation> T[] getAnnotatedFields(final Class c, final Class<T> a) {
        Collection<T> annotations = new ArrayList<T>();
        for (Field field : c.getFields()) {
            T annotation = field.getAnnotation(a);
            if (annotation != null) {
                annotations.add(annotation);
            }
        }
        return (T[]) annotations.toArray();
    }

    public static Column getPrimaryColumnForClass(Class c) {
        for (Field field : c.getFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null && column.primaryKey()) {
                return column;
            }
        }
        for (Method method : c.getMethods()) {
            Column column = method.getAnnotation(Column.class);
            if (column != null && column.primaryKey()) {
                return column;
            }
        }
        return null;
    }

    public static String getPrimaryNameForClass(Class c) {
        for (Field field : c.getFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null && column.primaryKey()) {
                return isNotEmpty(column.name()) ? column.name() : field.getName();
            }
        }
        for (Method method : c.getMethods()) {
            Column column = method.getAnnotation(Column.class);
            if (column != null && column.primaryKey()) {
                return isNotEmpty(column.name()) ? column.name() : method.getName();
            }
        }
        return null;
    }

    public static String getUniqueNameForClass(Class c) {
        for (Field field : c.getFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null && column.unique()) {
                return isNotEmpty(column.name()) ? column.name() : field.getName();
            }
        }
        for (Method method : c.getMethods()) {
            Column column = method.getAnnotation(Column.class);
            if (column != null && column.unique()) {
                return isNotEmpty(column.name()) ? column.name() : method.getName();
            }
        }
        return null;
    }

    public static boolean hasRelations(Class c, Cardinality cardinality) {
        for (Field field : c.getFields()) {
            Relation relation = field.getAnnotation(Relation.class);
            if (relation != null && relation.cardinality().equals(cardinality)) {
                return true;
            }
        }
        for (Method method : c.getMethods()) {
            Relation relation = method.getAnnotation(Relation.class);
            if (relation != null && relation.cardinality().equals(cardinality)) {
                return true;
            }
        }
        return false;
    }

    public static String getColumnName(Column column, Field field) {
        return column != null && isNotEmpty(column.name()) ? column.name() : field.getName();
    }

    public static String getColumnName(Column column, Method method) {
        return column != null && isNotEmpty(column.name()) ? column.name() : method.getName();
    }

    public static String getColumnType(ColumnMapping column) {
        if (column.getMethod() != null) {
            return getColumnType(column.getMethod().getReturnType());
        } else if (column.getField() != null) {
            return getColumnType(column.getField().getType());
        }
        return null;
    }

    public static String getColumnType(Class type) {
        if (Number.class.isAssignableFrom(type) || type.equals(Long.TYPE) || type.equals(Integer.TYPE)) {
            return "INTEGER";
        } else if (String.class.isAssignableFrom(type)) {
            return "VARCHAR";
        } else if (Boolean.class.isAssignableFrom(type)) {
            return "BOOLEAN";
        }
        return null;
    }

    public static Field getRelatedField(Class c, Class r) {
        for (Field field : r.getFields()) {
            if (field.getType().equals(c)) {
                return field;
            }
        }
        return null;
    }

    public static Relation getRelation(Class c, Class r) {
        for (Field field : r.getFields()) {
            if (field.getType().equals(c)) {
                return field.getAnnotation(Relation.class);
            }
        }
        for (Method method : r.getMethods()) {
            if (method.getReturnType().equals(c)) {
                return method.getAnnotation(Relation.class);
            }
        }
        return null;
    }

    public static Class getGenericType(Field field) {
        ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
        return (Class<?>) stringListType.getActualTypeArguments()[0];
    }

    public static Class getGenericType(Method method) {
        ParameterizedType stringListType = (ParameterizedType) method.getGenericReturnType();
        return (Class<?>) stringListType.getActualTypeArguments()[0];
    }

    public static boolean isDisplayableMethod(final Method method) {
        boolean displayable =
                (method.getName().startsWith("get") ||
                        method.getName().startsWith("is") ||
                        method.getName().startsWith("has"))
                        && (!method.getName().equals("getClass"))
                        && (!ReflectionUtils.isHashCodeMethod(method));
        return displayable && method.getParameterTypes().length == 0;
    }

    public static String stripName(final Method method) {
        return stripName(method.getName());
    }

    public static String stripName(final String name) {
        if (name.indexOf("get") == 0) {
            return lowerFirst(name.substring(name.indexOf("get") + 3));
        } else if (name.indexOf("is") == 0) {
            return lowerFirst(name.substring(name.indexOf("is") + 2));
        } else if (name.indexOf("has") == 0) {
            return lowerFirst(name.substring(name.indexOf("has") + 3));
        }
        return name;
    }

    public static String lowerFirst(String word) {
        return word.substring(0, 1).toLowerCase() + word.substring(1);
    }
}
