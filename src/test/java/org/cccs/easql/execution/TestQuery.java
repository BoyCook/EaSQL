package org.cccs.easql.execution;

import org.cccs.easql.config.DataDrivenTestEnvironment;
import org.cccs.easql.domain.Cat;
import org.cccs.easql.domain.Dog;
import org.cccs.easql.domain.Person;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.cccs.easql.execution.ReflectiveSQLGenerator.generateSelectSQL;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * User: boycook
 * Date: 28/06/2011
 * Time: 19:08
 */
@SuppressWarnings({"unchecked"})
public class TestQuery extends DataDrivenTestEnvironment {

    @Test
    public void executeQueryShouldWorkForJustClassForPerson() throws Exception {
        Collection<Person> results = query.execute(Person.class);
        Person craig = (Person) results.toArray()[0];
        assertThat(results.size(), is(equalTo(2)));
        assertCraig(craig);
    }

    @Test
    public void executeQueryShouldWorkForOneToManyRelations() throws Exception {
        Collection<Person> results = query.execute(Person.class, true);
        Person craig = (Person) results.toArray()[0];
        assertThat(results.size(), is(equalTo(2)));
        assertCraig(craig);
        assertThat(craig.cats.size(), is(equalTo(1)));
        assertThat(craig.dogs.size(), is(equalTo(1)));
    }

    @Test
    public void executeQueryShouldWorkForWithWhereClause() throws Exception {
        Map<String, String> where = new HashMap<String, String>();
        where.put("id", "1");
        Collection<Person> results = query.execute(Person.class, false, where);
        Person craig = (Person) results.toArray()[0];
        assertThat(results.size(), is(equalTo(1)));
        assertCraig(craig);
    }

    @Test
    public void executeQueryShouldWorkForJustClassForDog() throws Exception {
        Collection<Dog> results = query.execute(Dog.class);
        Dog lassie = (Dog) results.toArray()[0];

        assertThat(results.size(), is(equalTo(1)));
        assertThat(lassie.id, is(equalTo(1l)));
        assertThat(lassie.name, is(equalTo("Lassie")));
        assertNull(lassie.owner);
    }

    @Test
    public void executeQueryShouldWorkForJustClassForCat() throws Exception {
        Collection<Cat> results = query.execute(Cat.class);
        Cat bagpuss = (Cat) results.toArray()[0];

        assertThat(results.size(), is(equalTo(2)));
        assertThat(bagpuss.id, is(equalTo(1l)));
        assertThat(bagpuss.name, is(equalTo("Bagpuss")));
        assertNull(bagpuss.owner);
    }

    @Test
    public void executeQueryShouldWorkWithRelations() throws Exception {
        Collection<Cat> results = query.execute(Cat.class, true);
        Cat bagpuss = (Cat) results.toArray()[0];

        assertThat(results.size(), is(equalTo(2)));
        assertThat(bagpuss.id, is(equalTo(1l)));
        assertThat(bagpuss.name, is(equalTo("Bagpuss")));
        assertCraig(bagpuss.owner);
    }

    private void assertCraig(Person craig) {
        assertNotNull(craig);
        assertThat(craig.id, is(equalTo(1l)));
        assertThat(craig.name, is(equalTo("Craig")));
        assertThat(craig.email, is(equalTo("craig@cook.com")));
        assertThat(craig.phone, is(equalTo("07345123456")));
    }
}
