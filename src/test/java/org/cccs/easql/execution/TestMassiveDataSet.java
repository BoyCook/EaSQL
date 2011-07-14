package org.cccs.easql.execution;

import org.cccs.easql.config.DataDrivenTestEnvironment;
import org.cccs.easql.domain.Cat;
import org.cccs.easql.domain.Dog;
import org.cccs.easql.domain.Person;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * User: boycook
 * Date: 06/07/2011
 * Time: 20:56
 */
public class TestMassiveDataSet extends DataDrivenTestEnvironment {

    private EaSQLQuery query;
    private EaSQLService service;

    @Before
    public void beforeEach() {
        query = new EaSQLQuery(getDataSource());
        service = new EaSQLService(getDataSource());
    }

    @Test
    public void testDataShouldInstall() {
        installMassiveDataSet();
        Collection people = query.execute(Person.class);
        assertThat(people.size(), is(equalTo(9998)));

        Collection dogs = query.execute(Dog.class);
        assertThat(dogs.size(), is(equalTo(9996)));

        Collection cats = query.execute(Cat.class);
        assertThat(cats.size(), is(equalTo(9996)));
    }

    private void installMassiveDataSet() {
        List<Person> people = new ArrayList<Person>();

        for (int i=5; i<10000; i++) {
            Person person = new Person(i, "Person" + i);
            people.add(person);
            service.insert(person);
        }

        for (int i=5; i<10000; i++) {
            service.insert(new Dog(i, "Dog" + i, people.get(i-5)));
        }

        for (int i=5; i<10000; i++) {
            service.insert(new Cat(i, "Cat" + i, people.get(i-5)));
        }
    }
}
