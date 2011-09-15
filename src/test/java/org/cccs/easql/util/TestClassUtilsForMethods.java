package org.cccs.easql.util;

import org.apache.commons.beanutils.PropertyUtils;
import org.cccs.easql.Cardinality;
import org.cccs.easql.domain.RelationMapping;
import org.cccs.easql.domain.accessors.Cat;
import org.cccs.easql.domain.accessors.Country;
import org.cccs.easql.domain.accessors.Dog;
import org.cccs.easql.domain.accessors.Person;
import org.junit.Test;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.cccs.easql.util.ClassUtils.getRelations;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

/**
 * User: boycook
 * Date: 13/09/2011
 * Time: 18:03
 */
public class TestClassUtilsForMethods extends BaseTest {

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
    public void checkRelationsShouldWork() {
        assertRelationsExist(Person.class, Cardinality.ONE_TO_MANY, true);
        assertRelationsExist(Person.class, Cardinality.MANY_TO_ONE, false);
        assertRelationsExist(Dog.class, Cardinality.ONE_TO_MANY, false);
        assertRelationsExist(Dog.class, Cardinality.MANY_TO_ONE, true);
        assertRelationsExist(Cat.class, Cardinality.ONE_TO_MANY, false);
        assertRelationsExist(Cat.class, Cardinality.MANY_TO_ONE, true);
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

    @Test
    public void getColumnNameShouldWork() throws NoSuchMethodException {
        Method personId = Person.class.getMethod("getId");
        assertColumnName(personId, "id");

        Method cntId = Country.class.getMethod("getId");
        assertColumnName(cntId, "cntId");
    }

    @Test
    public void getColumnTypeShouldWork() throws NoSuchMethodException {
        Method personId = Person.class.getMethod("getId");
        Method personName = Person.class.getMethod("getName");
        assertColumnType(personId, "INTEGER");
        assertColumnType(personName, "VARCHAR");
    }

    @Test
    public void getTableNameShouldWork() {
        assertTableName(Person.class, "Person");
        assertTableName(Country.class, "countries");
    }

    @Test
    public void getPropertyDescriptorShouldWork() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        getProperty(new Cat(), "name");
    }

    private void getProperty(final Object object, final String name) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        final PropertyDescriptor property = PropertyUtils.getPropertyDescriptor(object, name);
        System.out.println("DisplayName: " + property.getDisplayName());
        System.out.println("Type: " + property.getPropertyType().getName());
        System.out.println("Getter: " + property.getReadMethod().getName());
        System.out.println("Setter: " + property.getWriteMethod().getName());
    }
}
