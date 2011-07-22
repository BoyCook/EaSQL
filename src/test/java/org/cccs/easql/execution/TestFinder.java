package org.cccs.easql.execution;

import org.cccs.easql.config.DataDrivenTestEnvironment;
import org.cccs.easql.domain.Cat;
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
public class TestFinder extends DataDrivenTestEnvironment {

    @Test
    public void finderShouldWorkForJustClassForPerson() throws Exception {
        Collection<Person> results = finder.query(Person.class);
        Person craig = (Person) results.toArray()[0];
        assertThat(results.size(), is(equalTo(2)));
        assertCraig(craig);
    }

    @Test
    public void finderShouldWorkForOneToManyRelations() throws Exception {
        Collection<Person> results = finder.query(Person.class, true);
        Person craig = (Person) results.toArray()[0];
        assertThat(results.size(), is(equalTo(2)));
        assertCraig(craig);
        assertThat(craig.cats.size(), is(equalTo(1)));
        assertThat(craig.dogs.size(), is(equalTo(1)));
    }

    @Test
    public void finderShouldWorkForWithWhereClause() throws Exception {
        Map<String, String> where = new HashMap<String, String>();
        where.put("id", "1");
        Collection<Person> results = finder.query(Person.class, false, where);
        Person craig = (Person) results.toArray()[0];
        assertThat(results.size(), is(equalTo(1)));
        assertCraig(craig);
    }

    @Test
    public void finderShouldWorkForJustClassForDog() throws Exception {
        Collection<Dog> results = finder.query(Dog.class);
        Dog lassie = (Dog) results.toArray()[0];

        assertThat(results.size(), is(equalTo(1)));
        assertThat(lassie.id, is(equalTo(1l)));
        assertThat(lassie.name, is(equalTo("Lassie")));
        assertNull(lassie.owner);
    }

    @Test
    public void finderShouldWorkForJustClassForCat() throws Exception {
        Collection<Cat> results = finder.query(Cat.class);
        Cat bagpuss = (Cat) results.toArray()[0];

        assertThat(results.size(), is(equalTo(2)));
        assertThat(bagpuss.id, is(equalTo(1l)));
        assertThat(bagpuss.name, is(equalTo("Bagpuss")));
        assertNull(bagpuss.owner);
    }

    @Test
    public void finderShouldWorkWithRelations() throws Exception {
        Collection<Cat> results = finder.query(Cat.class, true);
        Cat bagpuss = (Cat) results.toArray()[0];

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
        Person p = (Person) finder.findById(Person.class, 1);
        assertCraig(p);
    }

    @Test(expected = EntityNotFoundException.class)
    public void finderByKeyShouldThrowExceptionForInvalidKey() throws EntityNotFoundException {
        finder.findByKey(Person.class, "FOOBAR123");
    }

    @Test
    public void finderByKeyShouldWork() throws EntityNotFoundException {
        Person p = (Person) finder.findByKey(Person.class, "Craig");
        assertCraig(p);
    }

    @Test
    public void finderByKeyShouldWorkCaseInsensitive() throws EntityNotFoundException {
        Person p = (Person) finder.findByKey(Person.class, "CRAIG");
        assertCraig(p);
    }

    private void assertCraig(Person craig) {
        assertNotNull(craig);
        assertThat(craig.id, is(equalTo(1l)));
        assertThat(craig.name, is(equalTo("Craig")));
        assertThat(craig.email, is(equalTo("craig@cook.com")));
        assertThat(craig.phone, is(equalTo("07345123456")));
    }
}
