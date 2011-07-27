package org.cccs.easql.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.cccs.easql.execution.Executor;
import org.cccs.easql.execution.Finder;
import org.cccs.easql.execution.Schema;
import org.cccs.easql.execution.Service;
import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.readFileToString;

/**
 * User: boycook
 * Date: 30/06/2011
 * Time: 17:09
 */
public class DataDrivenTestEnvironment {

    private String sqlFile = "src/test/resources/data.sql";
    private BasicDataSource dataSource;
    protected Service service;
    protected Finder finder;

    @Before
    public void beforeEach() {
        setup();
        service = new Service(getDataSource());
        finder = new Finder(getDataSource());
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
        execute("DELETE FROM COUNTRIES;");
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
        final Executor db = new Executor(getDataSource());
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
