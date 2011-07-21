package org.cccs.easql.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.cccs.easql.domain.Cat;
import org.cccs.easql.domain.Dog;
import org.cccs.easql.domain.Person;
import org.cccs.easql.execution.Schema;
import org.junit.After;
import org.junit.Before;
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
    private Schema schema;
    private BasicDataSource dataSource;

    @Before
    public void setup() {
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
        dataSource.setUrl("jdbc:hsqldb:mem:easql");
        dataSource.setUsername("sa");
        dataSource.setPassword("");

        schema = new Schema("org.cccs.easql", dataSource);
        schema.generate();

        installData();
    }

    @After
    public void tearDown() {
        execute("DELETE FROM DOG;");
        execute("DELETE FROM CAT;");
        execute("DELETE FROM PERSON;");
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
