package org.cccs.easql.util;

import org.cccs.easql.Cardinality;
import org.cccs.easql.Column;
import org.cccs.easql.domain.ExtractionMapping;
import org.cccs.easql.domain.RelationMapping;
import org.cccs.easql.domain.accessors.*;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.cccs.easql.util.ClassCache.*;
import static org.cccs.easql.util.ClassUtils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

/**
 * User: boycook
 * Date: 13/09/2011
 * Time: 18:03
 */
public class TestClassUtilsForMethods {

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
        ExtractionMapping[] columns = getExtractionColumns(c);
        assertThat(columns.length, is(equalTo(size)));
    }

    @Test
    public void getPrimaryColumnShouldWork() {
        assertPrimaryColumn(Person.class, "id");
        assertPrimaryColumn(Dog.class, "id");
        assertPrimaryColumn(Cat.class, "id");
    }

    private void assertPrimaryColumn(Class c, String name) {
        assertThat(getPrimaryColumnName(c), is(equalTo(name)));
    }

    @Test
    public void checkRelationsShouldWork() {
        assertRelationsExist(Person.class, Cardinality.ONE_TO_MANY, true);
        assertRelationsExist(Person.class, Cardinality.MANY_TO_ONE, false);
        assertRelationsExist(Dog.class, Cardinality.ONE_TO_MANY, false);
        assertRelationsExist(Dog.class, Cardinality.MANY_TO_ONE, true);
        assertRelationsExist(Cat.class, Cardinality.ONE_TO_MANY, false);
        assertRelationsExist(Cat.class, Cardinality.MANY_TO_ONE, true);
    }

    private void assertRelationsExist(Class c, Cardinality cardinality, boolean has) {
        assertThat(hasRelations(c, cardinality), is(has));
    }

    @Test
    public void getRelationsShouldWork() {
        assertRelations(Person.class, Cardinality.ONE_TO_MANY, 2);
        assertRelations(Person.class, Cardinality.MANY_TO_ONE, 0);
        assertRelations(Dog.class, Cardinality.ONE_TO_MANY, 0);
        assertRelations(Dog.class, Cardinality.MANY_TO_ONE, 1);
        assertRelations(Cat.class, Cardinality.ONE_TO_MANY, 0);
        assertRelations(Cat.class, Cardinality.MANY_TO_ONE, 1);

        RelationMapping[] relations = getRelations(Person.class, Cardinality.ONE_TO_MANY);
        RelationMapping dogs = relations[0];
        RelationMapping cats = relations[1];

        assertThat(dogs, is(notNullValue()));
        assertThat(dogs.relation.key(), is(equalTo("person_id")));
        assertThat(cats, is(notNullValue()));
        assertThat(cats.relation.key(), is(equalTo("person_id")));
    }

    private void assertRelations(Class c, Cardinality cardinality, int cnt) {
        assertThat(getRelations(c, cardinality).length, is(equalTo(cnt)));
    }

    @Test
    public void getColumnNameShouldWork() throws NoSuchMethodException {
        Method personId = Person.class.getMethod("getId");
        assertColumnName(personId, "id");

        Method cntId = Country.class.getMethod("getId");
        assertColumnName(cntId, "cntId");
    }

    private void assertColumnName(Method method, String name) {
        assertThat(getColumnName(method.getAnnotation(Column.class), method), is(equalTo(name)));
    }

    @Test
    public void getColumnTypeShouldWork() throws NoSuchMethodException {
        Method personId = Person.class.getMethod("getId");
        Method personName = Person.class.getMethod("getName");
        assertColumnType(personId, "INTEGER");
        assertColumnType(personName, "VARCHAR");
    }

    private void assertColumnType(Method method, String type) {
        assertThat(getColumnType(method.getReturnType()), is(equalTo(type)));
    }

    @Test
    public void getTableNameShouldWork() {
        assertTableName(Person.class, "Person");
        assertTableName(Country.class, "countries");
    }

    private void assertTableName(Class c, String name) {
        assertThat(getTableName(c), is(equalTo(name)));
    }

}
