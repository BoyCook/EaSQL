package org.cccs.easql.execution;

import org.cccs.easql.config.DataDrivenTestEnvironment;
import org.cccs.easql.domain.Cat;
import org.cccs.easql.domain.Person;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

/**
 * User: boycook
 * Date: 07/07/2011
 * Time: 10:25
 */
public class TestService extends DataDrivenTestEnvironment {

    private Person craig;
    private Cat daisy;

    @Before
    public void beforeEach() {
        setup();
        service = new Service(getDataSource());
        finder = new Finder(getDataSource());
    }

    @Before
    public void before() throws EntityNotFoundException {
        craig = finder.findByKey(Person.class, "Craig");
        daisy = finder.findByKey(Cat.class, "Daisy");

        assertThat(craig.name, is(equalTo("Craig")));
        assertThat(daisy.name, is(equalTo("Daisy")));
        assertThat(craig.cats.size(), is(equalTo(1)));
        assertThat(craig.dogs.size(), is(equalTo(1)));
    }

    @Test
    public void createWithoutRelationsShouldWork() {
        service.insert(new Person("Dave"));
        service.insert(new Person("Bob"));
        service.insert(new Person("Jim"));
        service.insert(new Person("Steve"));
        service.insert(new Person("Greg"));
    }

    @Test
    public void createWithRelationsShouldWork() {
        Cat garfield = new Cat("garfield", craig);
        service.insert(garfield);
    }

    @Test
    public void updateShouldWork() throws EntityNotFoundException {
        craig.email = "SomeNewEmail";
        service.update(craig);

        final Person updated = finder.findById(Person.class, 1);
        assertThat(updated.cats.size(), is(equalTo(1)));
        assertThat(updated.dogs.size(), is(equalTo(1)));
        assertThat(updated.email, is(equalTo("SomeNewEmail")));
    }

    @Test
    public void updateOne2ManyRelationsShouldWork() throws EntityNotFoundException {
        craig.dogs.clear();
        craig.cats.clear();
        craig.cats.add(daisy);
        Cat fluffy = new Cat();
        fluffy.name = "Fluffy";
        fluffy.owner = craig;
        craig.cats.add(fluffy);
        service.update(craig);

        final Person updated = finder.findByKey(Person.class, "Craig");
        assertThat(updated.cats.size(), is(equalTo(2)));
        assertThat(updated.dogs.size(), is(equalTo(0)));

        final Cat fluffyDB = finder.findByKey(Cat.class, "Fluffy");
        assertThat(fluffyDB.name, is(equalTo("Fluffy")));
    }

    @Ignore //Really not sure why this is not working
    @Test
    public void updateMany2OneRelationsShouldWork() throws EntityNotFoundException {
        final Person bob = finder.findByKey(Person.class, "Bob");
        final Cat bagpuss = finder.findByKey(Cat.class, "Bagpuss");
        bagpuss.owner = bob;
        service.update(bagpuss);

        final Cat updated = finder.findByKey(Cat.class, "Bagpuss");
        assertThat(updated.countries.size(), is(equalTo(2)));
        assertThat(updated.owner, is(equalTo(bob)));
    }

    @Test
    public void updateMany2ManyRelationsShouldWork() {
    }
}
