package org.cccs.easql.execution;

import org.cccs.easql.domain.*;
import org.cccs.easql.validation.ValidationFailureException;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * User: boycook
 * Date: 28/06/2011
 * Time: 19:08
 */
@SuppressWarnings({"unchecked"})
public class TestFinderForFields extends BaseFinderTest {

    @Test
    public void finderShouldWorkForAllPersons() throws Exception {
        assertAll(Person.class, false, 2);
    }

    @Test
    public void finderShouldWorkForAllDogs() throws Exception {
        assertAll(Dog.class, false, 1);
    }

    @Test
    public void finderShouldWorkForAllCats() throws Exception {
        assertAll(Cat.class, false, 2);
    }

    @Test
    public void finderShouldWorkForAllPersonsWithRelations() throws Exception {
        assertAll(Person.class, true, 2);
    }

    @Test
    public void finderShouldWorkForAllDogsWithRelations() throws Exception {
        assertAll(Dog.class, true, 1);
    }

    @Test
    public void finderShouldWorkForAllCatsWithRelations() throws Exception {
        assertAll(Cat.class, true, 2);
    }

    @Test
    public void finderByIdShouldWork() throws EntityNotFoundException, ValidationFailureException {
        final Person p = finder.findById(Person.class, 1);
        assertCraig(p);
    }

    @Test
    public void finderByKeyShouldWork() throws EntityNotFoundException, ValidationFailureException {
        final Person p = finder.findByKey(Person.class, "Craig");
        assertCraig(p);
    }

    @Test
    public void finderByKeyShouldWorkCaseInsensitive() throws EntityNotFoundException, ValidationFailureException {
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

    @Test
    public void finderShouldWorkForWithWhereClause() throws Exception {
        final Map<String, String> where = new HashMap<String, String>();
        where.put("id", "1");
        final Collection<Person> results = assertWhere(Person.class, where, 1);
        final Person craig = (Person) results.toArray()[0];
        assertCraig(craig);
    }

    @Test(expected = EntityNotFoundException.class)
    public void finderByIdShouldThrowExceptionForInvalidId() throws EntityNotFoundException, ValidationFailureException {
        finder.findById(Person.class, -1);
    }

    @Test(expected = EntityNotFoundException.class)
    public void finderByKeyShouldThrowExceptionForInvalidKey() throws EntityNotFoundException, ValidationFailureException {
        finder.findByKey(Person.class, "FOOBAR123");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void findByIdShouldFailForNoId() throws EntityNotFoundException, ValidationFailureException {
        finder.findById(Invalid.class, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findByIdShouldFailForIdNotSpecified() throws EntityNotFoundException, ValidationFailureException {
        finder.findById(Person.class, 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void findByKeyShouldFailForNoKey() throws EntityNotFoundException, ValidationFailureException {
        finder.findByKey(Invalid.class, "foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void findByKeyShouldFailForEmptyKey() throws EntityNotFoundException, ValidationFailureException {
        finder.findByKey(Person.class, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void findByKeyShouldFailForNullKey() throws EntityNotFoundException, ValidationFailureException {
        finder.findByKey(Person.class, null);
    }

    @Override
    protected void assertCraig(Object object) {
        Person craig = (Person) object;
        assertNotNull(craig);
        assertThat(craig.id, is(equalTo(1l)));
        assertThat(craig.name, is(equalTo("Craig")));
        assertThat(craig.email, is(equalTo("craig@cook.com")));
        assertThat(craig.phone, is(equalTo("07345123456")));
    }
}
