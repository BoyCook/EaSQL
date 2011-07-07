package org.cccs.easql.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.cccs.easql.domain.Cat;
import org.cccs.easql.domain.Dog;
import org.cccs.easql.domain.Person;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.cccs.easql.execution.ReflectiveSQLGenerator.generateCreateSQL;
import static org.cccs.easql.execution.ReflectiveSQLGenerator.generateSelectSQL;

/**
 * User: boycook
 * Date: 30/06/2011
 * Time: 17:09
 */
public class DataDrivenTestEnvironment {

    private String sqlFile = "src/test/resources/data.sql";
    private BasicDataSource dataSource;
    private boolean schemaCreated = false;

    @Before
    public void setup() {
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
        dataSource.setUrl("jdbc:hsqldb:mem:easql");
        dataSource.setUsername("sa");
        dataSource.setPassword("");

        if (!schemaCreated) {
            schemaCreated = true;
            createSchema();
        }

        installData();
    }

    @After
    public void tearDown() {
        execute("DELETE FROM DOG;");
        execute("DELETE FROM CAT;");
        execute("DELETE FROM PERSON;");
    }

    private void createSchema() {
        createTable(Person.class);
        createTable(Dog.class);
        createTable(Cat.class);
    }

    private void createTable(Class c) {
        try {
            execute(generateSelectSQL(c));
        } catch (BadSqlGrammarException e) {
            execute(generateCreateSQL(c));
        }
    }

    private void installData() {
        try {
            execute(readFileToString(new File(getSqlFile())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void execute(String sql) {
        JdbcTemplate db = new JdbcTemplate(getDataSource());
        System.out.println("Executing SQL: " + sql);
        db.execute(sql);
    }

    public String getSqlFile() {
        return sqlFile;
    }

    public void setSqlFile(String sqlFile) {
        this.sqlFile = sqlFile;
    }

    public BasicDataSource getDataSource() {
        return dataSource;
    }
}
