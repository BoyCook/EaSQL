package org.cccs.easql.execution;

import org.cccs.easql.domain.DBTable;
import org.cccs.easql.domain.TableColumn;

import static org.cccs.easql.cache.ClassCache.getTable;
import static org.cccs.easql.execution.SQLUtils.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * User: boycook
 * Date: 08/10/2011
 * Time: 11:56
 */
public abstract class BaseSQLTest {

    public void sqlBuilderShouldWorkForInsertsForPerson(final Object object) {
        final String sql = "INSERT INTO Person (id, name, email, phone) VALUES (101, 'Craig', 'craig@cook.com', '07234123456');";
        assertThat(generateInsertSQL(object), is(equalTo(sql)));
    }

    public void sqlBuilderShouldWorkForInsertsForDog(final Object object) {
        final String sql = "INSERT INTO Dog (id, name, person_id) VALUES (101, 'Lassie', %s);";
        assertThat(generateInsertSQL(object), is(equalTo(sql)));
    }

    public void sqlBuilderShouldWorkForInsertsForCat(final Object object) {
        final String sql = "INSERT INTO Cat (id, name, person_id) VALUES (101, 'Bagpuss', %s);";
        assertThat(generateInsertSQL(object), is(equalTo(sql)));
    }

    public void sqlBuilderShouldWorkForSelectsForJustPerson(final Class c) {
        final String sql = "SELECT id, name, email, phone FROM Person";
        assertThat(generateSelectSQL(c), is(equalTo(sql)));
    }

    public void sqlBuilderShouldWorkForJustDog(final Class c) {
        final String sql = "SELECT id, name, person_id FROM Dog";
        assertThat(generateSelectSQL(c), is(equalTo(sql)));
    }

    public void sqlBuilderShouldWorkForDogAndRelations(final Class c) {
        final String sql = "SELECT id, name, person2dog.id as person2dog_id, person2dog.name as person2dog_name, person2dog.email as person2dog_email, person2dog.phone as person2dog_phone FROM Dog LEFT OUTER JOIN Person person2dog ON Dog.person_id = person2dog.id";
        assertThat(generateSelectSQLForOneToMany(c), is(equalTo(sql)));
    }

    public void sqlBuilderShouldWorkForJustCat(final Class c) {
        final String sql = "SELECT id, name, person_id FROM Cat";
        assertThat(generateSelectSQL(c), is(equalTo(sql)));
    }

    public void sqlBuilderShouldWorkForCatAndRelations(final Class c) {
        final String sql = "SELECT id, name, person2cat.id as person2cat_id, person2cat.name as person2cat_name, person2cat.email as person2cat_email, person2cat.phone as person2cat_phone FROM Cat LEFT OUTER JOIN Person person2cat ON Cat.person_id = person2cat.id";
        assertThat(generateSelectSQLForOneToMany(c), is(equalTo(sql)));
    }

    public void sqlBuilderShouldWorkForSelectManyToManyFromLeft(final Class left, final Class right) throws NoSuchMethodException {
        final DBTable table = getTable(left);
        final String sql = "SELECT id, name FROM Dog a INNER JOIN dog_countries b ON a.id = b.dog_id AND b.cntId = 1;";

        TableColumn join = null;
        for (TableColumn column : table.many2many) {
            if (column.getGenericType().equals(right)) {
                join = column;
            }
        }

        assertThat(generateSelectSQLForManyToMany(right, join, 1), is(equalTo(sql)));
    }

    public void sqlBuilderShouldWorkForSelectManyToManyFromRight(final Class left, final Class right) throws NoSuchMethodException {
        final DBTable table = getTable(left);
        final String sql = "SELECT cntId, name FROM countries a INNER JOIN dog_countries b ON a.cntId = b.cntId AND b.dog_id = 1;";

        TableColumn join = null;
        for (TableColumn column : table.many2many) {
            if (column.getGenericType().equals(right)) {
                join = column;
            }
        }

        assertThat(generateSelectSQLForManyToMany(right, join, 1), is(equalTo(sql)));
    }

    public void sqlBuilderShouldWorkForUpdatesForPerson(final Object object) {
        final String sql = "UPDATE Person set name = 'Craig', email = 'craig@cook.com', phone = '07234123456' WHERE id = 1;";
        assertThat(generateUpdateSQL(object), is(equalTo(sql)));
    }

    public void sqlBuilderShouldWorkForUpdatesForDog(final Object object) {
        final String sql = "UPDATE Dog set name = 'Lassie' WHERE id = 1;";
        assertThat(generateUpdateSQL(object), is(equalTo(sql)));
    }

    public void sqlBuilderShouldWorkForUpdatesForCat(final Object object) {
        final String sql = "UPDATE Cat set name = 'Bagpuss' WHERE id = 1;";
        assertThat(generateUpdateSQL(object), is(equalTo(sql)));
    }

    public void sqlBuilderShouldWorkForDeletes(final Object object) {
        final String sql = "DELETE FROM Person WHERE id = 1;";
        assertThat(generateDeleteSQL(object), is(equalTo(sql)));
    }

    public void sqlBuilderShouldWorkForCreatesForPerson(final Class c) {
        final String sql = "CREATE TABLE Person (id identity NOT NULL PRIMARY KEY, name VARCHAR, email VARCHAR, phone VARCHAR);";
        assertThat(generateCreateSQL(c), is(equalTo(sql)));
    }

    public void sqlBuilderShouldWorkForCreatesForDog(final Class c) {
        final String sql = "CREATE TABLE Dog (id identity NOT NULL PRIMARY KEY, name VARCHAR, person_id INTEGER);";
        assertThat(generateCreateSQL(c), is(equalTo(sql)));
    }

    public void sqlBuilderShouldWorkForCreatesForCat(final Class c) {
        final String sql = "CREATE TABLE Cat (id identity NOT NULL PRIMARY KEY, name VARCHAR, person_id INTEGER);";
        assertThat(generateCreateSQL(c), is(equalTo(sql)));
    }
}
