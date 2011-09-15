package org.cccs.easql.util;

import org.apache.commons.beanutils.PropertyUtils;
import org.cccs.easql.Cardinality;
import org.cccs.easql.domain.*;
import org.junit.Test;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import static org.cccs.easql.util.ClassUtils.getGenericType;
import static org.cccs.easql.util.ClassUtils.getRelations;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

/**
 * User: boycook
 * Date: 12/07/2011
 * Time: 10:02
 */
public class TestClassUtilsForFields extends BaseTest {

    @Test
    public void getColumnNamesShouldWork() {
        assertColumnNames(Person.class, 4);
        assertColumnNames(Dog.class, 2);
        assertColumnNames(Cat.class, 2);
    }

    @Test
    public void getColumnsShouldWork() {
        assertColumns(Person.class, 4);
        assertColumns(Dog.class, 2);
        assertColumns(Cat.class, 2);
    }

    @Test
    public void getPrimaryColumnShouldWork() {
        assertPrimaryColumn(Person.class, "id");
        assertPrimaryColumn(Dog.class, "id");
        assertPrimaryColumn(Cat.class, "id");
    }

    @Test
    public void getRelationsShouldWork() {
        assertRelationsExist(Person.class, Cardinality.ONE_TO_MANY, true);
        assertRelationsExist(Person.class, Cardinality.MANY_TO_ONE, false);
        assertRelationsExist(Dog.class, Cardinality.ONE_TO_MANY, false);
        assertRelationsExist(Dog.class, Cardinality.MANY_TO_ONE, true);
        assertRelationsExist(Cat.class, Cardinality.ONE_TO_MANY, false);
        assertRelationsExist(Cat.class, Cardinality.MANY_TO_ONE, true);
    }

    @Test
    public void getRelationFieldsShouldWork() {
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

    @Test
    public void getColumnNameShouldWork() throws NoSuchFieldException {
        Field personId = Person.class.getField("id");
        assertColumnName(personId, "id");

        Field cntId = Country.class.getField("id");
        assertColumnName(cntId, "cntId");
    }

    @Test
    public void getColumnTypeShouldWork() throws NoSuchFieldException {
        Field personId = Person.class.getField("id");
        Field personName = Person.class.getField("name");
        assertColumnType(personId, "INTEGER");
        assertColumnType(personName, "VARCHAR");
    }

    @Test
    public void getTableNameShouldWork() {
        assertTableName(Person.class, "Person");
        assertTableName(Country.class, "countries");
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

    private void assertGenericType(final Field field, final Class c) {
        assertThat(getGenericType(field), is(equalTo(c)));
    }
}
