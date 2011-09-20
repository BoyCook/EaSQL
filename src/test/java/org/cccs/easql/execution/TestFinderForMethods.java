package org.cccs.easql.execution;

import org.cccs.easql.config.DataDrivenTestEnvironment;
import org.cccs.easql.domain.accessors.Cat;
import org.cccs.easql.domain.accessors.Country;
import org.cccs.easql.domain.accessors.Dog;
import org.cccs.easql.domain.accessors.Person;
import org.cccs.easql.util.MethodSchema;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * User: boycook
 * Date: 14/09/2011
 * Time: 12:21
 */
public class TestFinderForMethods extends DataDrivenTestEnvironment {

    @Before
    public void beforeEach() {
        MethodSchema.setup();
        setup();
        service = new Service(getDataSource());
        finder = new Finder(getDataSource());
    }

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
        assertThat(lassie.getId(), is(equalTo(1l)));
        assertThat(lassie.getName(), is(equalTo("Lassie")));
        assertNull(lassie.getOwner());
    }

    @Test
    public void finderShouldWorkForJustClassForCat() throws Exception {
        final Collection<Cat> results = finder.query(Cat.class);
        final Cat bagpuss = (Cat) results.toArray()[0];

        assertThat(results.size(), is(equalTo(2)));
        assertThat(bagpuss.getId(), is(equalTo(1l)));
        assertThat(bagpuss.getName(), is(equalTo("Bagpuss")));
        assertNull(bagpuss.getOwner());
    }

    @Test
    public void finderShouldWorkWithRelations() throws Exception {
        final Collection<Cat> results = finder.query(Cat.class, true);
        final Cat bagpuss = (Cat) results.toArray()[0];

        assertThat(results.size(), is(equalTo(2)));
        assertThat(bagpuss.getId(), is(equalTo(1l)));
        assertThat(bagpuss.getName(), is(equalTo("Bagpuss")));
        assertCraig(bagpuss.getOwner());
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
        assertThat(craig.getCats().size(), is(equalTo(1)));
        assertThat(craig.getDogs().size(), is(equalTo(1)));
    }

    @Test
    public void finderShouldWorkForManyToManyRelationsFromLeft() throws Exception {
        final Country england = finder.findByKey(Country.class, "England");
        assertThat(england.getName(), is(equalTo("England")));
        assertThat(england.getId(), is(equalTo(1l)));
        assertThat(england.getDogs().size(), is(equalTo(1)));
        assertThat(england.getCats().size(), is(equalTo(2)));
    }

    @Test
    public void finderShouldWorkForManyToManyRelationsFromRight() throws Exception {
        final Dog lassie = finder.findByKey(Dog.class, "Lassie");
        assertThat(lassie.getName(), is(equalTo("Lassie")));
        assertThat(lassie.getId(), is(equalTo(1l)));
        assertThat(lassie.getCountries().size(), is(equalTo(2)));
    }

    private void assertCraig(Person craig) {
        assertNotNull(craig);
        assertThat(craig.getId(), is(equalTo(1l)));
        assertThat(craig.getName(), is(equalTo("Craig")));
        assertThat(craig.getEmail(), is(equalTo("craig@cook.com")));
        assertThat(craig.getPhone(), is(equalTo("07345123456")));
    }
}
