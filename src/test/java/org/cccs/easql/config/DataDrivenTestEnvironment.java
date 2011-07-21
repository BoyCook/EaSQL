package org.cccs.easql.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.cccs.easql.execution.Finder;
import org.cccs.easql.execution.Schema;
import org.cccs.easql.execution.Service;
import org.junit.After;
import org.junit.Before;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.cccs.easql.domain.Sequence.setCounter;

/**
 * User: boycook
 * Date: 30/06/2011
 * Time: 17:09
 */
public class DataDrivenTestEnvironment {

    private String sqlFile = "src/test/resources/data.sql";
    private BasicDataSource dataSource;
    protected Service service;
    protected Finder query;

    @Before
    public void beforeEach() {
        setup();
        service = new Service(getDataSource());
        query = new Finder(getDataSource());
        setCounter(3);
    }

    protected void setup() {
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
        dataSource.setUrl("jdbc:hsqldb:mem:easql");
        dataSource.setUsername("sa");
        dataSource.setPassword("");

        Schema.packageName = "org.cccs.easql";
        Schema.dataSource = dataSource;
        Schema.generate();
        installData();
    }

    @After
    public void tearDown() {
        //TODO: move this to Schema
        execute("DELETE FROM DOG;");
        execute("DELETE FROM CAT;");
        execute("DELETE FROM PERSON;");
        execute("DELETE FROM CAT_COUNTRIES;");
        execute("DELETE FROM DOG_COUNTRIES;");
    }

    private void installData() {
        if (getSqlFile() != null) {
            System.out.println("Installing test data");
            try {
                execute(readFileToString(new File(getSqlFile())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Not installing any data");
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
