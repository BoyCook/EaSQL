package org.cccs.easql.util;

import org.cccs.easql.domain.DBTable;
import org.cccs.easql.domain.TableColumn;

import javax.persistence.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collection;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * User: boycook
 * Date: 30/06/2011
 * Time: 18:42
 */
public final class ClassUtils {

    public static synchronized DBTable getTableForClass(Class c) {
        TableColumn id = getIdColumn(c);
        TableColumn key = getKeyColumn(c);
        TableColumn[] columns = getTableColumnsForClass(c);
        TableColumn[] one2one = getRelations(c, OneToOne.class);
        TableColumn[] one2many = getRelations(c, OneToMany.class);
        TableColumn[] many2one = getRelations(c, ManyToOne.class);
        TableColumn[] many2many = getRelations(c, ManyToMany.class);

        Collection<String> columnNames = new ArrayList<String>();
        for (TableColumn column : columns) {
            columnNames.add(getColumnName(column.getColumn(), column.getMember()));
        }

        return new DBTable(c, id, key, columnNames.toArray(new String[columnNames.size()]), columns, one2one, one2many, many2one, many2many);
    }

    protected static boolean isSelectableColumn(AccessibleObject object) {
        Column column = object.getAnnotation(Column.class);
        Id id = object.getAnnotation(Id.class);
        OneToOne oneToOne = object.getAnnotation(OneToOne.class);
        OneToMany oneToMany = object.getAnnotation(OneToMany.class);
        ManyToOne manyToOne = object.getAnnotation(ManyToOne.class);
        ManyToMany manyToMany = object.getAnnotation(ManyToMany.class);
        return column != null && !(id != null || oneToOne != null || oneToMany != null || manyToOne != null || manyToMany != null);
    }

    public static TableColumn[] getTableColumnsForClass(Class c) {
        Collection<TableColumn> columns = new ArrayList<TableColumn>();
        for (Field field : c.getFields()) {
            if (isSelectableColumn(field)) {
                columns.add(new TableColumn(field));
            }
        }
        for (Method method : c.getMethods()) {
            if (isSelectableColumn(method)) {
                columns.add(new TableColumn(method));
            }
        }
        return columns.toArray(new TableColumn[columns.size()]);
    }

    @SuppressWarnings({"unchecked"})
    public static TableColumn[] getRelations(final Class c, final Class cardinality) {
        Collection<TableColumn> relations = new ArrayList<TableColumn>();
        for (Field field : c.getFields()) {
            Annotation relation = field.getAnnotation(cardinality);
            if (relation != null) {
                relations.add(new TableColumn(field));
            }
        }
        for (Method method : c.getMethods()) {
            Annotation relation = method.getAnnotation(cardinality);
            if (relation != null) {
                relations.add(new TableColumn(method));
            }
        }
        return relations.toArray(new TableColumn[relations.size()]);
    }

    public static TableColumn getIdColumn(Class c) {
        for (Field field : c.getFields()) {
            if (field.getAnnotation(Id.class) != null && field.getAnnotation(Column.class) != null) {
                return new TableColumn(field);
            }
        }
        for (Method method : c.getMethods()) {
            if (method.getAnnotation(Id.class) != null && method.getAnnotation(Column.class) != null) {
                return new TableColumn(method);
            }
        }
        return null;
    }

    public static TableColumn getKeyColumn(Class c) {
        for (Field field : c.getFields()) {
            Column column = field.getAnnotation(Column.class);
            if (field.getAnnotation(Id.class) == null && column != null && column.unique()) {
                return new TableColumn(field);
            }
        }
        for (Method method : c.getMethods()) {
            Column column = method.getAnnotation(Column.class);
            if (method.getAnnotation(Id.class) == null && column != null && column.unique()) {
                return new TableColumn(method);
            }
        }
        return null;
    }

    //TODO: cache
    @SuppressWarnings({"unchecked"})
    public static boolean hasRelations(final Class c, final Class cardinality) {
        for (Field field : c.getFields()) {
            if (field.getAnnotation(cardinality) != null) {
                return true;
            }
        }
        for (Method method : c.getMethods()) {
            if (method.getAnnotation(cardinality) != null) {
                return true;
            }
        }
        return false;
    }

    public static String getColumnName(Column column, Member member) {
        return column != null && isNotEmpty(column.name()) ? column.name() : member.getName();
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

    //TODO: deal with multiple mappings
    public static TableColumn getRelation(Class c, Class r) {
        for (Field field : r.getFields()) {
            if (field.getType().equals(c)) {
                return new TableColumn(field);
            }
        }
        for (Method method : r.getMethods()) {
            if (method.getReturnType().equals(c)) {
                return new TableColumn(method);
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

    @SuppressWarnings({"unchecked"})
    public static String getTableName(Class c) {
        Table table = (Table) c.getAnnotation(Table.class);
        return table != null && isNotEmpty(table.name()) ? table.name() : c.getSimpleName();
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
