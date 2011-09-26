package org.cccs.easql.util;

import org.cccs.easql.Cardinality;
import org.cccs.easql.Column;
import org.cccs.easql.domain.ExtractionMapping;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.cccs.easql.cache.ClassCache.*;
import static org.cccs.easql.util.ClassUtils.*;
import static org.cccs.easql.util.ClassUtils.getColumnType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

/**
 * User: boycook
 * Date: 15/09/2011
 * Time: 14:26
 */
public abstract class BaseTest {

    protected void assertColumnNames(Class c, int size) {
        String[] columnNames = getColumnNames(c);
        assertThat(columnNames.length, is(equalTo(size)));
    }

    protected void assertColumns(Class c, int size) {
        ExtractionMapping[] columns = getExtractionColumns(c);
        assertThat(columns.length, is(equalTo(size)));
    }

    protected void assertColumnType(Field field, String type) {
        assertThat(getColumnType(field.getType()), is(equalTo(type)));
    }

    protected void assertPrimaryColumn(Class c, String name) {
        assertThat(getPrimaryColumnName(c), is(equalTo(name)));
    }

    protected void assertRelationsExist(Class c, Cardinality cardinality, boolean has) {
        assertThat(hasRelations(c, cardinality), is(has));
    }

    protected void assertColumnName(Method method, String name) {
        assertThat(getColumnName(method.getAnnotation(Column.class), method), is(equalTo(name)));
    }

    protected void assertColumnName(Field field, String name) {
        assertThat(getColumnName(field.getAnnotation(Column.class), field), is(equalTo(name)));
    }

    protected void assertRelations(Class c, Cardinality cardinality, int cnt) {
        assertThat(getRelations(c, cardinality).length, is(equalTo(cnt)));
    }

    protected void assertColumnType(Method method, String type) {
        assertThat(getColumnType(method.getReturnType()), is(equalTo(type)));
    }

    protected void assertTableName(Class c, String name) {
        assertThat(getTableName(c), is(equalTo(name)));
    }
}
