package org.cccs.easql.execution;

import org.cccs.easql.domain.NoDefaultConstructor;
import org.cccs.easql.domain.accessors.Cat;
import org.cccs.easql.domain.accessors.Country;
import org.cccs.easql.domain.accessors.Dog;
import org.cccs.easql.domain.accessors.Person;
import org.cccs.easql.util.MethodSchema;
import org.junit.Before;
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
 * Date: 14/09/2011
 * Time: 12:21
 */
@SuppressWarnings({"ALL"})
public class TestFinderForMethods extends BaseFinderTest {

    @Before
    public void beforeEach() {
        MethodSchema.setup();
        setup();
        service = new Service(getDataSource());
        finder = new Finder(getDataSource());
    }

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

    @Test(expected = UnsupportedOperationException.class)
    public void findByIdShouldFailForNoId() throws EntityNotFoundException {
        finder.findById(NoDefaultConstructor.class, 1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void findByIdShouldFailForIdNotSpecified() throws EntityNotFoundException {
        finder.findById(NoDefaultConstructor.class, 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void findByKeyShouldFailForNoKey() throws EntityNotFoundException {
        finder.findByKey(NoDefaultConstructor.class, "foo");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void findByKeyShouldFailForEmptyKey() throws EntityNotFoundException {
        finder.findByKey(NoDefaultConstructor.class, "");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void findByKeyShouldFailForNullKey() throws EntityNotFoundException {
        finder.findByKey(NoDefaultConstructor.class, null);
    }

    @Test
    public void finderShouldWorkForWithWhereClause() throws Exception {
        final Map<String, String> where = new HashMap<String, String>();
        where.put("id", "1");
        final Collection<Person> results = assertWhere(Person.class, where, 1);
        final Person craig = (Person) results.toArray()[0];
        assertCraig(craig);
    }

    @Override
    protected void assertCraig(Object object) {
        Person craig = (Person) object;
        assertNotNull(craig);
        assertThat(craig.getId(), is(equalTo(1l)));
        assertThat(craig.getName(), is(equalTo("Craig")));
        assertThat(craig.getEmail(), is(equalTo("craig@cook.com")));
        assertThat(craig.getPhone(), is(equalTo("07345123456")));
    }
}
