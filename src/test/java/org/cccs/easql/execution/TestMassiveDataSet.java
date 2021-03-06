package org.cccs.easql.execution;

import org.cccs.easql.config.DataDrivenTestEnvironment;
import org.cccs.easql.domain.Cat;
import org.cccs.easql.domain.Dog;
import org.cccs.easql.domain.Person;
import org.cccs.easql.validation.ValidationFailureException;
import org.junit.Ignore;
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
@Ignore(value = "Ignored to speed up test - this is for periodic baselining")
public class TestMassiveDataSet extends DataDrivenTestEnvironment {

    @Test
    public void testDataShouldInstall() throws ValidationFailureException {
        installMassiveDataSet();
        Collection people = finder.all(Person.class);
        assertThat(people.size(), is(equalTo(9997)));

        Collection dogs = finder.all(Dog.class);
        assertThat(dogs.size(), is(equalTo(9996)));

        Collection cats = finder.all(Cat.class);
        assertThat(cats.size(), is(equalTo(9997)));
    }

    private void installMassiveDataSet() throws ValidationFailureException {
        List<Person> people = new ArrayList<Person>();

        for (int i = 5; i < 10000; i++) {
            Person person = new Person("Person" + i);
            people.add(person);
            service.insert(person);
        }

        for (int i = 5; i < 10000; i++) {
            service.insert(new Dog("Dog" + i, people.get(i - 5)));
        }

        for (int i = 5; i < 10000; i++) {
            service.insert(new Cat("Cat" + i, people.get(i - 5)));
        }
    }
}
