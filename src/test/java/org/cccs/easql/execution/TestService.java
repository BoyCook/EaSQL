package org.cccs.easql.execution;

import org.cccs.easql.config.DataDrivenTestEnvironment;
import org.cccs.easql.domain.Cat;
import org.cccs.easql.domain.Person;
import org.junit.Before;
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

    private Service service;
    private FInder query;
    private Person craig;
    private Cat daisy;

    @Before
    public void beforeEach() {
        service = new Service(getDataSource());
        query = new FInder(getDataSource());
        craig = (Person) query.find(Person.class, 1);
        daisy = (Cat) query.find(Cat.class, 2);

        assertThat(craig.name, is(equalTo("Craig")));
        assertThat(daisy.name, is(equalTo("Daisy")));
    }

    @Test
    public void createWithoutRelationsShouldWork() {
        Person bob = new Person(5, "Bob");
        service.insert(bob);
    }

    @Test
    public void createWithRelationsShouldWork() {
        Cat garfield = new Cat(3, "garfield", craig);
        service.insert(garfield);
    }

    @Test
    public void updateShouldWork() {
        craig.email = "SomeNewEmail";
        assertThat(craig.cats.size(), is(equalTo(1)));
        assertThat(craig.dogs.size(), is(equalTo(1)));

        craig.dogs.clear();
        craig.cats.clear();
        craig.cats.add(daisy);
        Cat fluffy = new Cat();
        fluffy.name = "Fluffy";
        fluffy.owner = craig;
        craig.cats.add(fluffy);
        service.update(craig);

        final Person updated = (Person) query.find(Person.class, 1);
        assertThat(updated.cats.size(), is(equalTo(2)));
        assertThat(updated.dogs.size(), is(equalTo(0)));
        assertThat(updated.email, is(equalTo("SomeNewEmail")));

        Cat fluffyDB = (Cat) query.find(Cat.class, 0);
        assertThat(fluffyDB.name, is(equalTo("Fluffy")));
    }
}
