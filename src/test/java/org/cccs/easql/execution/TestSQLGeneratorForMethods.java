package org.cccs.easql.execution;

import org.cccs.easql.Relation;
import org.cccs.easql.domain.accessors.Cat;
import org.cccs.easql.domain.accessors.Country;
import org.cccs.easql.domain.accessors.Dog;
import org.cccs.easql.domain.accessors.Person;
import org.cccs.easql.util.MethodSchema;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Method;

import static org.cccs.easql.execution.SQLUtils.*;
import static org.cccs.easql.cache.ClassCache.getColumnNames;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * User: boycook
 * Date: 19/09/2011
 * Time: 12:29
 */
public class TestSQLGeneratorForMethods {

    private Person craig;
    private Dog lassie;
    private Cat bagpuss;

    @Before
    public void setup() throws IOException {
        MethodSchema.setup();
        Schema.packageName = "org.cccs.easql";
        craig = new Person("Craig", "craig@cook.com", "07234123456");
        craig.setId(1);
        lassie = new Dog("Lassie", craig);
        lassie.setId(1);
        bagpuss = new Cat("Bagpuss", craig);
        bagpuss.setId(1);
    }

    @Test
    public void getColumnsShouldWorkForPerson() {
        final String[] columns = getColumnNames(Person.class);
        String[] data = {"name", "id", "email", "phone"};
        assertThat(columns, is(equalTo(data)));
    }

    @Test
    public void getColumnsShouldWorkForDog() {
        final String[] columns = getColumnNames(Dog.class);
        String[] data = {"name", "id"};
        assertThat(columns, is(equalTo(data)));
    }

    @Test
    public void getColumnsShouldWorkForCat() {
        final String[] columns = getColumnNames(Cat.class);
        String[] data = {"name", "id"};
        assertThat(columns, is(equalTo(data)));
    }

    @Test
    public void sqlBuilderShouldWorkForInsertsForPerson() {
        final String sql = "INSERT INTO Person (name, id, email, phone) VALUES ('Craig', 101, 'craig@cook.com', '07234123456');";
        assertThat(generateInsertSQL(craig), is(equalTo(sql)));
    }

    @Test
    public void sqlBuilderShouldWorkForInsertsForDog() {
        final String sql = "INSERT INTO Dog (name, id, person_id) VALUES ('Lassie', 101, %s);";
        assertThat(generateInsertSQL(lassie), is(equalTo(sql)));
    }

    @Test
    public void sqlBuilderShouldWorkForInsertsForCat() {
        final String sql = "INSERT INTO Cat (name, id, person_id) VALUES ('Bagpuss', 101, %s);";
        assertThat(generateInsertSQL(bagpuss), is(equalTo(sql)));
    }

    @Test
    public void sqlBuilderShouldWorkForSelectsForJustPerson() {
        final String sql = "SELECT name, id, email, phone FROM Person";
        assertThat(generateSelectSQL(Person.class), is(equalTo(sql)));
    }

    @Test
    public void sqlBuilderShouldWorkForJustDog() {
        final String sql = "SELECT name, id, person_id FROM Dog";
        assertThat(generateSelectSQL(Dog.class), is(equalTo(sql)));
    }

    @Test
    public void sqlBuilderShouldWorkForDogAndRelations() {
        final String sql = "SELECT name, id, person2dog.name as person2dog_name, person2dog.id as person2dog_id, person2dog.email as person2dog_email, person2dog.phone as person2dog_phone FROM Dog LEFT OUTER JOIN Person person2dog ON Dog.person_id = person2dog.id";
        assertThat(generateSelectSQL(Dog.class, true), is(equalTo(sql)));
    }

    @Test
    public void sqlBuilderShouldWorkForJustCat() {
        final String sql = "SELECT name, id, person_id FROM Cat";
        assertThat(generateSelectSQL(Cat.class), is(equalTo(sql)));
    }

    @Test
    public void sqlBuilderShouldWorkForCatAndRelations() {
        final String sql = "SELECT name, id, person2cat.name as person2cat_name, person2cat.id as person2cat_id, person2cat.email as person2cat_email, person2cat.phone as person2cat_phone FROM Cat LEFT OUTER JOIN Person person2cat ON Cat.person_id = person2cat.id";
        assertThat(generateSelectSQL(Cat.class, true), is(equalTo(sql)));
    }

    @Test
    public void sqlBuilderShouldWorkForSelectManyToManyFromLeft() throws NoSuchMethodException {
        final Method dogs = Country.class.getMethod("getDogs");
        final Relation relation = dogs.getAnnotation(Relation.class);
        final String sql = "SELECT name, id FROM Dog a INNER JOIN dog_countries b ON a.id = b.dog_id AND b.cntId = 1;";
        assertThat(generateSelectSQLForManyToMany(Dog.class, relation, 1), is(equalTo(sql)));
    }

    @Test
    public void sqlBuilderShouldWorkForSelectManyToManyFromRight() throws NoSuchMethodException {
        final Method countries = Dog.class.getMethod("getCountries");
        final Relation relation = countries.getAnnotation(Relation.class);
        final String sql = "SELECT name, cntId FROM countries a INNER JOIN dog_countries b ON a.cntId = b.cntId AND b.dog_id = 1;";
        assertThat(generateSelectSQLForManyToMany(Country.class, relation, 1), is(equalTo(sql)));
    }

    @Test
    public void sqlBuilderShouldWorkForUpdatesForPerson() {
        final String sql = "UPDATE Person set name = 'Craig', email = 'craig@cook.com', phone = '07234123456' WHERE id = 1;";
        assertThat(generateUpdateSQL(craig), is(equalTo(sql)));
    }

    @Test
    public void sqlBuilderShouldWorkForUpdatesForDog() {
        final String sql = "UPDATE Dog set name = 'Lassie' WHERE id = 1;";
        assertThat(generateUpdateSQL(lassie), is(equalTo(sql)));
    }

    @Test
    public void sqlBuilderShouldWorkForUpdatesForCat() {
        final String sql = "UPDATE Cat set name = 'Bagpuss' WHERE id = 1;";
        assertThat(generateUpdateSQL(bagpuss), is(equalTo(sql)));
    }

    @Test
    public void sqlBuilderShouldWorkForDeletes() {
        final String sql = "DELETE FROM Person WHERE id = 1;";
        assertThat(generateDeleteSQL(craig), is(equalTo(sql)));
    }

    @Test
    public void sqlBuilderShouldWorkForCreatesForPerson() {
        final String sql = "CREATE TABLE Person (name VARCHAR, id identity NOT NULL PRIMARY KEY, email VARCHAR, phone VARCHAR);";
        assertThat(generateCreateSQL(Person.class), is(equalTo(sql)));
    }

    @Test
    public void sqlBuilderShouldWorkForCreatesForDog() {
        final String sql = "CREATE TABLE Dog (name VARCHAR, id identity NOT NULL PRIMARY KEY, person_id INTEGER);";
        assertThat(generateCreateSQL(Dog.class), is(equalTo(sql)));
    }

    @Test
    public void sqlBuilderShouldWorkForCreatesForCat() {
        final String sql = "CREATE TABLE Cat (name VARCHAR, id identity NOT NULL PRIMARY KEY, person_id INTEGER);";
        assertThat(generateCreateSQL(Cat.class), is(equalTo(sql)));
    }
}
