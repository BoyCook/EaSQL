package org.cccs.easql;

import org.cccs.easql.config.DataDrivenTestEnvironment;
import org.cccs.easql.domain.Cat;
import org.cccs.easql.domain.Dog;
import org.cccs.easql.domain.Person;
import org.cccs.easql.execution.Query;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.cccs.easql.execution.ReflectiveSQLGenerator.generateInsertSQL;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * User: boycook
 * Date: 06/07/2011
 * Time: 20:56
 */
public class TestMassiveDataSet extends DataDrivenTestEnvironment {

    private Query query;

    @Before
    public void beforeEach() {
        query = new Query(getDataSource());
    }

    @Test
    public void testDataShouldInstall() {
        installMassiveDataSet();
        Collection people = query.execute(Person.class);
        assertThat(people.size(), is(equalTo(9999)));

//        Collection dogs = query.execute(Dog.class);
//        assertThat(dogs.size(), is(equalTo(9999)));
//
//        Collection cats = query.execute(Cat.class);
//        assertThat(cats.size(), is(equalTo(9999)));
    }

    private void installMassiveDataSet() {
        StringBuilder sql = new StringBuilder();

        List<Person> people = new ArrayList<Person>();

        for (int i=2; i<10000; i++) {
            Person person = new Person(i, "Person" + i);
            people.add(person);
            sql.append(generateInsertSQL(person));
        }

//        for (int i=2; i<10000; i++) {
//            sql.append(generateInsertSQL(new Dog(i, "Dog" + i, people.get(i-2))));
//        }
//
//        for (int i=2; i<10000; i++) {
//            sql.append(generateInsertSQL(new Cat(i, "Cat" + i, people.get(i-2))));
//        }

        JdbcTemplate db = new JdbcTemplate(getDataSource());
        db.execute(sql.toString());
    }
}
