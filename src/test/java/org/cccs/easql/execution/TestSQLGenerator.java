package org.cccs.easql.execution;

import org.apache.commons.lang.ArrayUtils;
import org.cccs.easql.domain.Cat;
import org.cccs.easql.domain.Dog;
import org.cccs.easql.domain.Person;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.cccs.easql.execution.ReflectiveSQLGenerator.*;
import static org.cccs.easql.util.ReflectionUtils.getColumnNames;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * User: boycook
 * Date: 17/06/2011
 * Time: 13:04
 */
public class TestSQLGenerator {

    private Person craig;
    private Dog lassie;
    private Cat bagpuss;

    @Before
    public void setup() throws IOException {
        craig = new Person(1, "Craig", "craig@cook.com", "07234123456");
        lassie = new Dog(1, "Lassie", craig);
        bagpuss = new Cat(1, "Bagpuss", craig);
    }

    @Test
    public void getColumnsShouldWorkForPerson() {
        String[] personColumns = getColumnNames(Person.class);
        assertThat(ArrayUtils.toString(personColumns), is(equalTo("{id,name,email,phone}")));
    }

    @Test
    public void getColumnsShouldWorkForDog() {
        String[] dogColumns = getColumnNames(Dog.class);
        assertThat(ArrayUtils.toString(dogColumns), is(equalTo("{id,name}")));
    }

    @Test
    public void getColumnsShouldWorkForCat() {
        String[] catColumns = getColumnNames(Cat.class);
        assertThat(ArrayUtils.toString(catColumns), is(equalTo("{id,name}")));
    }

    @Test
    public void reflectiveSQLBuilderShouldWorkForInsertsForPerson() throws Exception {
        String personSQL = "INSERT INTO Person (id, name, email, phone) VALUES (1, 'Craig', 'craig@cook.com', '07234123456');";
        assertThat(generateInsertSQL(craig), is(equalTo(personSQL)));
    }

    @Test
    public void reflectiveSQLBuilderShouldWorkForInsertsForDog() throws Exception {
        String dogSQL = "INSERT INTO Dog (id, name, person_id) VALUES (1, 'Lassie', %s);";
        assertThat(generateInsertSQL(lassie), is(equalTo(dogSQL)));
    }

    @Test
    public void reflectiveSQLBuilderShouldWorkForInsertsForCat() throws Exception {
        String catSQL = "INSERT INTO Cat (id, name, person_id) VALUES (1, 'Bagpuss', %s);";
        assertThat(generateInsertSQL(bagpuss), is(equalTo(catSQL)));
    }

    @Test
    public void reflectiveSQLBuilderShouldWorkForSelectsForJustPerson() throws Exception {
        String sql = "SELECT id, name, email, phone FROM Person";
        assertThat(generateSelectSQL(Person.class), is(equalTo(sql)));
    }

    @Test
    public void reflectiveSQLBuilderShouldWorkForJustDog() throws Exception {
        String sql = "SELECT id, name, person_id FROM Dog";
        assertThat(generateSelectSQL(Dog.class), is(equalTo(sql)));
    }

    @Test
    public void reflectiveSQLBuilderShouldWorkForDogAndRelations() throws Exception {
        String sql = "SELECT id, name, person2dog.id as person2dog_id, person2dog.name as person2dog_name, person2dog.email as person2dog_email, person2dog.phone as person2dog_phone FROM Dog LEFT OUTER JOIN Person person2dog ON Dog.id = person2dog.id";
        assertThat(generateSelectSQL(Dog.class, true), is(equalTo(sql)));
    }

    @Test
    public void reflectiveSQLBuilderShouldWorkForJustCat() throws Exception {
        String sql = "SELECT id, name, person_id FROM Cat";
        assertThat(generateSelectSQL(Cat.class), is(equalTo(sql)));
    }

    @Test
    public void reflectiveSQLBuilderShouldWorkForCatAndRelations() throws Exception {
        String sql = "SELECT id, name, person2cat.id as person2cat_id, person2cat.name as person2cat_name, person2cat.email as person2cat_email, person2cat.phone as person2cat_phone FROM Cat LEFT OUTER JOIN Person person2cat ON Cat.id = person2cat.id";
        assertThat(generateSelectSQL(Cat.class, true), is(equalTo(sql)));
    }

    @Test
    public void reflectiveSQLBuilderShouldWorkForUpdatesForPerson() throws Exception {
        String sql = "UPDATE Person set name = 'Craig', email = 'craig@cook.com', phone = '07234123456';";
        assertThat(generateUpdateSQL(craig), is(equalTo(sql)));
    }

    @Test
    public void reflectiveSQLBuilderShouldWorkForUpdatesForDog() throws Exception {
        String sql = "UPDATE Dog set name = 'Lassie';";
        assertThat(generateUpdateSQL(lassie), is(equalTo(sql)));
    }

    @Test
    public void reflectiveSQLBuilderShouldWorkForUpdatesForCat() throws Exception {
        String sql = "UPDATE Cat set name = 'Bagpuss';";
        assertThat(generateUpdateSQL(bagpuss), is(equalTo(sql)));
    }

    @Test
    public void reflectiveSQLBuilderShouldWorkForDeletes() throws Exception {
        String sql = "DELETE FROM Person WHERE id = 1;";
        assertThat(generateDeleteSQL(craig), is(equalTo(sql)));
    }

    @Test
    public void reflectiveSQLBuilderShouldWorkForCreatesForPerson() {
        String sql = "CREATE TABLE Person (id identity NOT NULL PRIMARY KEY, name VARCHAR, email VARCHAR, phone VARCHAR);";
        assertThat(generateCreateSQL(Person.class), is(equalTo(sql)));
    }

    @Test
    public void reflectiveSQLBuilderShouldWorkForCreatesForDog() {
        String sql = "CREATE TABLE Dog (id identity NOT NULL PRIMARY KEY, name VARCHAR, person_id INTEGER);";
        assertThat(generateCreateSQL(Dog.class), is(equalTo(sql)));
    }

    @Test
    public void reflectiveSQLBuilderShouldWorkForCreatesForCat() {
        String sql = "CREATE TABLE Cat (id identity NOT NULL PRIMARY KEY, name VARCHAR, person_id INTEGER);";
        assertThat(generateCreateSQL(Cat.class), is(equalTo(sql)));
    }
}
