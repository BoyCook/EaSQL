package org.cccs.easql.util;

import org.cccs.easql.Cardinality;
import org.cccs.easql.ColumnMapping;
import org.cccs.easql.domain.Cat;
import org.cccs.easql.domain.Country;
import org.cccs.easql.domain.Dog;
import org.cccs.easql.domain.Person;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.cccs.easql.util.ClassUtils.*;
import static org.cccs.easql.util.ObjectUtils.getRelationFields;
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
    public void getRelationsShouldWork() {
        assertRelations(Person.class, Cardinality.ONE_TO_MANY, true);
        assertRelations(Person.class, Cardinality.MANY_TO_ONE, false);
        assertRelations(Dog.class, Cardinality.ONE_TO_MANY, false);
        assertRelations(Dog.class, Cardinality.MANY_TO_ONE, true);
        assertRelations(Cat.class, Cardinality.ONE_TO_MANY, false);
        assertRelations(Cat.class, Cardinality.MANY_TO_ONE, true);
    }

    private void assertRelations(Class c, Cardinality cardinality, boolean has) {
        assertThat(hasRelations(c, cardinality), is(has));
    }

    @Test
    public void getRelationFieldsShouldWork() {
        assertRelationFields(Person.class, Cardinality.ONE_TO_MANY, 2);
        assertRelationFields(Person.class, Cardinality.MANY_TO_ONE, 0);
        assertRelationFields(Dog.class, Cardinality.ONE_TO_MANY, 0);
        assertRelationFields(Dog.class, Cardinality.MANY_TO_ONE, 1);
        assertRelationFields(Cat.class, Cardinality.ONE_TO_MANY, 0);
        assertRelationFields(Cat.class, Cardinality.MANY_TO_ONE, 1);
    }

    private void assertRelationFields(Class c, Cardinality cardinality, int cnt) {
        assertThat(getRelationFields(c, cardinality).length, is(equalTo(cnt)));
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
