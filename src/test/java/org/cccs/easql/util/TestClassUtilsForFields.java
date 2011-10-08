package org.cccs.easql.util;

import org.cccs.easql.domain.*;
import org.junit.Ignore;
import org.junit.Test;

import static org.cccs.easql.cache.ClassCache.getTable;
import static org.junit.Assert.assertNotNull;

/**
 * User: boycook
 * Date: 12/07/2011
 * Time: 10:02
 */
public class TestClassUtilsForFields extends BaseUtilsTest {

    @Test
    public void getTableShouldWork() {
        assertTableName(Person.class, "Person");
        assertTableName(Dog.class, "Dog");
        assertTableName(Country.class, "countries");
    }

    @Test
    public void getAnnotationShouldWork() {
        DBTable table = getTable(Country.class);
        assertNotNull(table.id.getColumn());
    }

    @Test
    public void getIdShouldWork() {
        assertId(Person.class, "id");
        assertId(Dog.class, "id");
        assertId(Country.class, "cntId");
    }

    @Test
    public void getSequenceShouldWork() {
        assertSequence(Person.class, "person_seq");
        assertSequence(Dog.class, "dog_seq");
        assertSequence(Country.class, "cnt_seq");
    }

    @Test
    public void getColumnNamesShouldWork() {
        assertColumnNames(Person.class, 3);
        assertColumnNames(Dog.class, 1);
        assertColumnNames(Cat.class, 1);
    }

    @Test
    public void getColumnsShouldWork() {
        assertColumns(Person.class, 3);
        assertColumns(Dog.class, 1);
        assertColumns(Cat.class, 1);
    }

    @Test
    public void getRelationsShouldWork() {
        assertOneToOne(Person.class, 0);
        assertOneToMany(Person.class, 2);
        assertManyToOne(Person.class, 0);
        assertManyToMany(Person.class, 0);

        assertOneToOne(Cat.class, 0);
        assertOneToMany(Cat.class, 0);
        assertManyToOne(Cat.class, 1);
        assertManyToMany(Cat.class, 1);

        assertOneToOne(Dog.class, 0);
        assertOneToMany(Dog.class, 0);
        assertManyToOne(Dog.class, 1);
        assertManyToMany(Dog.class, 1);

        assertOneToOne(Country.class, 0);
        assertOneToMany(Country.class, 0);
        assertManyToOne(Country.class, 0);
        assertManyToMany(Country.class, 2);
    }

    @Ignore
    @Test
    public void getPrimaryColumnShouldWork() {
        //TODO: implement
        assertKey(Person.class, "id");
        assertKey(Dog.class, "id");
        assertKey(Cat.class, "id");
    }
}
