package org.cccs.easql;

import org.cccs.easql.config.DataDrivenTestEnvironment;
import org.cccs.easql.domain.Cat;
import org.cccs.easql.domain.Dog;
import org.cccs.easql.domain.Person;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Collection;

import static org.cccs.easql.ReflectiveSQLGenerator.generateSelectSQL;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * User: boycook
 * Date: 28/06/2011
 * Time: 19:08
 */
@SuppressWarnings({"unchecked"})
public class TestGenericQuery extends DataDrivenTestEnvironment {

    private Query query;

    @Before
    public void beforeEach() {
        query = new Query(getDataSource());
    }

    @Test
    public void executeQueryShouldWorkForJustClassForPerson() throws Exception {
        Collection<Person> results = query.execute(Person.class);
        assertThat(results.size(), is(equalTo(1)));
    }

    @Test
    public void executeQueryShouldWorkForJustClassForDog() throws Exception {
        Collection<Person> results = query.execute(Dog.class);
        assertThat(results.size(), is(equalTo(1)));
    }

    @Test
    public void executeQueryShouldWorkForJustClassForCat() throws Exception {
        Collection<Person> results = query.execute(Cat.class);
        assertThat(results.size(), is(equalTo(1)));
    }

    @Test
    public void executeQueryShouldWorkWithRelations() throws Exception {
        String sql = generateSelectSQL(Dog.class, true);
        execute(sql);
    }
}
