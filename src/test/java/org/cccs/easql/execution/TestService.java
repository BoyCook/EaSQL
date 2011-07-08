package org.cccs.easql.execution;

import org.cccs.easql.config.DataDrivenTestEnvironment;
import org.cccs.easql.domain.Cat;
import org.cccs.easql.domain.Person;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

/**
 * User: boycook
 * Date: 07/07/2011
 * Time: 10:25
 */
public class TestService extends DataDrivenTestEnvironment {

    private Service service;
    private Query query;
    private Person craig;

    @Before
    public void beforeEach() {
        service = new Service(getDataSource());
        query = new Query(getDataSource());

        Collection people = query.execute(Person.class);
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
}
