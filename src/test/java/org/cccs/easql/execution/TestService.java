package org.cccs.easql.execution;

import org.cccs.easql.config.DataDrivenTestEnvironment;
import org.cccs.easql.domain.Cat;
import org.cccs.easql.domain.Person;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collection;

import static org.cccs.easql.util.ClassUtils.getRelationFields;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

/**
 * User: boycook
 * Date: 07/07/2011
 * Time: 10:25
 */
public class TestService extends DataDrivenTestEnvironment {

    private EaSQLService service;
    private EaSQLQuery query;
    private Person craig;

    @Before
    public void beforeEach() {
        service = new EaSQLService(getDataSource());
        query = new EaSQLQuery(getDataSource());

        Collection people = query.execute(Person.class, true);
        craig = (Person) people.toArray()[0];
    }

    @Test
    public void createWithoutRelationsShouldWork() {
        Person bob = new Person(5, "Bob");
        service.insert(bob);
    }

    @Test
    public void createWithRelationsShouldWork() {
        Cat garfield = new Cat(2, "garfield", craig);
        service.insert(garfield);
    }

    @Test
    public void updateShouldWork() {
        craig.email = "SomeNewEmail";
        assertThat(craig.cats.size(), is(equalTo(1)));
        assertThat(craig.dogs.size(), is(equalTo(1)));

        craig.cats.clear();
        craig.cats.add(new Cat(23, "Daisy", craig));
        Cat fluffy = new Cat();
        fluffy.name = "Fluffy";
        fluffy.owner = craig;
        craig.cats.add(fluffy);
        service.update(craig);
    }
}
