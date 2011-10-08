package org.cccs.easql.util;

import org.cccs.easql.domain.TableColumn;

import static org.cccs.easql.cache.ClassCache.getTable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

/**
 * User: boycook
 * Date: 15/09/2011
 * Time: 14:26
 */
public abstract class BaseUtilsTest {

    protected void assertColumnNames(Class c, int size) {
        assertThat(getTable(c).columnNames.length, is(equalTo(size)));
    }

    protected void assertColumns(Class c, int size) {
        assertThat(getTable(c).columns.length, is(equalTo(size)));
    }

    protected void assertColumnType(TableColumn column, String type) {
        assertThat(column.getDBType(), is(equalTo(type)));
    }

    protected void assertKey(Class c, String name) {
        assertThat(getTable(c).key.getName(), is(equalTo(name)));
    }

    protected void assertTableName(Class c, String name) {
        assertThat(getTable(c).getName(), is(equalTo(name)));
    }

    protected void assertId(Class c, String id) {
        assertThat(getTable(c).id.getName(), is(equalTo(id)));
    }

    protected void assertSequence(Class c, String name) {
        assertThat(getTable(c).id.getGeneratedValue().generator(), is(equalTo(name)));
    }

    protected void assertOneToOne(Class c, int cnt) {
        assertThat(getTable(c).one2one.length, is(equalTo(cnt)));
    }

    protected void assertOneToMany(Class c, int cnt) {
        assertThat(getTable(c).one2many.length, is(equalTo(cnt)));
    }

    protected void assertManyToOne(Class c, int cnt) {
        assertThat(getTable(c).many2one.length, is(equalTo(cnt)));
    }

    protected void assertManyToMany(Class c, int cnt) {
        assertThat(getTable(c).many2many.length, is(equalTo(cnt)));
    }

//    protected void assertColumnName(Method method, String name) {
//        assertThat(getColumnName(method.getAnnotation(Column.class), method), is(equalTo(name)));
//    }
//
//    protected void assertColumnName(Field field, String name) {
//        assertThat(getColumnName(field.getAnnotation(Column.class), field), is(equalTo(name)));
//    }
//
//    protected void assertRelations(Class c, Class cardinality, int cnt) {
//        assertThat(getTable(c). getRelations(c, cardinality).length, is(equalTo(cnt)));
//    }
//
//    protected void assertColumnType(Method method, String type) {
//        assertThat(getColumnType(method.getReturnType()), is(equalTo(type)));
//    }
}
