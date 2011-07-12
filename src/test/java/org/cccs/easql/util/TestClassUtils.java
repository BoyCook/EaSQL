package org.cccs.easql.util;

import org.cccs.easql.ColumnMapping;
import org.cccs.easql.domain.Cat;
import org.cccs.easql.domain.Dog;
import org.cccs.easql.domain.Person;
import org.junit.Test;

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
    public void getColumnNameShouldWork() {

    }

    @Test
    public void getColumnTypeShouldWork() {

    }

    @Test
    public void getTableNameShouldWork() {

    }

    @Test
    public void getExtractionMappingsShouldWork() {

    }

    @Test
    public void getGenericTypeShouldWork() {

    }
}
