package org.cccs.easql.util;

import org.cccs.easql.ColumnMapping;
import org.cccs.easql.domain.Cat;
import org.cccs.easql.domain.Country;
import org.cccs.easql.domain.Dog;
import org.cccs.easql.domain.Person;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.cccs.easql.util.ClassUtils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

/**
 * User: boycook
 * Date: 12/07/2011
 * Time: 10:02
 */
public class TestClassUtils {

    @Test
    public void getColumnNamesShouldWork() {
        assertColumnNames(Person.class, 4);
        assertColumnNames(Dog.class, 2);
        assertColumnNames(Cat.class, 2);
    }

    private void assertColumnNames(Class c, int size) {
        String[] columnNames = getColumnNames(c);
        assertThat(columnNames.length, is(equalTo(size)));
    }

    @Test
    public void getColumnsShouldWork() {
        assertColumns(Person.class, 4);
        assertColumns(Dog.class, 2);
        assertColumns(Cat.class, 2);
    }

    private void assertColumns(Class c, int size) {
        ColumnMapping[] columns = getColumns(c);
        assertThat(columns.length, is(equalTo(size)));
    }

    @Test
    public void getPrimaryColumnShouldWork() {
        assertPrimaryColumn(Person.class, "id");
        assertPrimaryColumn(Dog.class, "id");
        assertPrimaryColumn(Cat.class, "id");
    }

    private void assertPrimaryColumn(Class c, String name) {
        assertThat(getPrimaryColumn(c), is(equalTo(name)));
    }

    @Test
    public void hasOneToManyShouldWork() {
        assertOneToMany(Person.class, true);
        assertOneToMany(Dog.class, false);
        assertOneToMany(Cat.class, false);
    }

    private void assertOneToMany(Class c, boolean has) {
        assertThat(hasOneToMany(c), is(has));
    }

    @Test
    public void getColumnNameShouldWork() throws NoSuchFieldException {
        Field personId = Person.class.getField("id");
        assertColumnName(personId, "id");

        Field cntId = Country.class.getField("id");
        assertColumnName(cntId, "cntId");
    }

    private void assertColumnName(Field field, String name) {
        assertThat(getColumnName(field), is(equalTo(name)));
    }

    @Test
    public void getColumnTypeShouldWork() throws NoSuchFieldException {
        Field personId = Person.class.getField("id");
        Field personName = Person.class.getField("name");
        assertColumnType(personId, "INTEGER");
        assertColumnType(personName, "VARCHAR");
    }

    private void assertColumnType(Field field, String type) {
        assertThat(getColumnType(field), is(equalTo(type)));
    }

    @Test
    public void getTableNameShouldWork() {
        assertTableName(Person.class, "Person");
        assertTableName(Country.class, "countries");
    }

    private void assertTableName(Class c, String name) {
        assertThat(getTableName(c), is(equalTo(name)));
    }

    @Test
    public void getExtractionMappingsShouldWork() {
        //TODO: implement this
    }

    @Test
    public void getGenericTypeShouldWork() throws NoSuchFieldException {
        Field dogs = Person.class.getField("dogs");
        Field cats = Person.class.getField("cats");
        assertGenericType(dogs, Dog.class);
        assertGenericType(cats, Cat.class);
    }

    private void assertGenericType(Field field, Class c) {
        assertThat(getGenericType(field), is(equalTo(c)));
    }
}
