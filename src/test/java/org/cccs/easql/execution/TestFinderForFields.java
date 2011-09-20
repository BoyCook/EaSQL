package org.cccs.easql.execution;

import org.cccs.easql.config.DataDrivenTestEnvironment;
import org.cccs.easql.domain.Cat;
import org.cccs.easql.domain.Country;
import org.cccs.easql.domain.Dog;
import org.cccs.easql.domain.Person;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * User: boycook
 * Date: 28/06/2011
 * Time: 19:08
 */
@SuppressWarnings({"unchecked"})
public class TestFinderForFields extends DataDrivenTestEnvironment {

    @Test
    public void finderShouldWorkForJustClassForPerson() throws Exception {
        final Collection<Person> results = finder.query(Person.class);
        final Person craig = (Person) results.toArray()[0];
        assertThat(results.size(), is(equalTo(2)));
        assertCraig(craig);
    }

    @Test
    public void finderShouldWorkForWithWhereClause() throws Exception {
        final Map<String, String> where = new HashMap<String, String>();
        where.put("id", "1");
        final Collection<Person> results = finder.query(Person.class, false, where);
        final Person craig = (Person) results.toArray()[0];
        assertThat(results.size(), is(equalTo(1)));
        assertCraig(craig);
    }

    @Test
    public void finderShouldWorkForJustClassForDog() throws Exception {
        final Collection<Dog> results = finder.query(Dog.class);
        final Dog lassie = (Dog) results.toArray()[0];

        assertThat(results.size(), is(equalTo(1)));
        assertThat(lassie.id, is(equalTo(1l)));
        assertThat(lassie.name, is(equalTo("Lassie")));
        assertNull(lassie.owner);
    }

    @Test
    public void finderShouldWorkForJustClassForCat() throws Exception {
        final Collection<Cat> results = finder.query(Cat.class);
        final Cat bagpuss = (Cat) results.toArray()[0];

        assertThat(results.size(), is(equalTo(2)));
        assertThat(bagpuss.id, is(equalTo(1l)));
        assertThat(bagpuss.name, is(equalTo("Bagpuss")));
        assertNull(bagpuss.owner);
    }

    @Test
    public void finderShouldWorkWithRelations() throws Exception {
        final Collection<Cat> results = finder.query(Cat.class, true);
        final Cat bagpuss = (Cat) results.toArray()[0];

        assertThat(results.size(), is(equalTo(2)));
        assertThat(bagpuss.id, is(equalTo(1l)));
        assertThat(bagpuss.name, is(equalTo("Bagpuss")));
        assertCraig(bagpuss.owner);
    }

    @Test(expected = EntityNotFoundException.class)
    public void finderByIdShouldThrowExceptionForInvalidId() throws EntityNotFoundException {
        finder.findById(Person.class, -1);
    }

    @Test
    public void finderByIdShouldWork() throws EntityNotFoundException {
        final Person p = finder.findById(Person.class, 1);
        assertCraig(p);
    }

    @Test(expected = EntityNotFoundException.class)
    public void finderByKeyShouldThrowExceptionForInvalidKey() throws EntityNotFoundException {
        finder.findByKey(Person.class, "FOOBAR123");
    }

    @Test
    public void finderByKeyShouldWork() throws EntityNotFoundException {
        final Person p = finder.findByKey(Person.class, "Craig");
        assertCraig(p);
    }

    @Test
    public void finderByKeyShouldWorkCaseInsensitive() throws EntityNotFoundException {
        final Person p = finder.findByKey(Person.class, "CRAIG");
        assertCraig(p);
    }

    @Test
    public void finderShouldWorkForOneToManyRelations() throws Exception {
        final Person craig = finder.findByKey(Person.class, "Craig");
        assertCraig(craig);
        assertThat(craig.cats.size(), is(equalTo(1)));
        assertThat(craig.dogs.size(), is(equalTo(1)));
    }

    @Test
    public void finderShouldWorkForManyToManyRelationsFromLeft() throws Exception {
        final Country england = finder.findByKey(Country.class, "England");
        assertThat(england.name, is(equalTo("England")));
        assertThat(england.id, is(equalTo(1l)));
        assertThat(england.dogs.size(), is(equalTo(1)));
        assertThat(england.cats.size(), is(equalTo(2)));
    }

    @Test
    public void finderShouldWorkForManyToManyRelationsFromRight() throws Exception {
        final Dog lassie = finder.findByKey(Dog.class, "Lassie");
        assertThat(lassie.name, is(equalTo("Lassie")));
        assertThat(lassie.id, is(equalTo(1l)));
        assertThat(lassie.countries.size(), is(equalTo(2)));
    }

    private void assertCraig(Person craig) {
        assertNotNull(craig);
        assertThat(craig.id, is(equalTo(1l)));
        assertThat(craig.name, is(equalTo("Craig")));
        assertThat(craig.email, is(equalTo("craig@cook.com")));
        assertThat(craig.phone, is(equalTo("07345123456")));
    }
}
