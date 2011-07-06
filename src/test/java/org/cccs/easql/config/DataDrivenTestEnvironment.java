package org.cccs.easql.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.cccs.easql.domain.Cat;
import org.cccs.easql.domain.Dog;
import org.cccs.easql.domain.Person;
import org.junit.BeforeClass;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.cccs.easql.execution.ReflectiveSQLGenerator.generateCreateSQL;

/**
 * User: boycook
 * Date: 30/06/2011
 * Time: 17:09
 */
public class DataDrivenTestEnvironment {

    private static String sqlFile = "src/test/resources/data.sql";
    private static BasicDataSource dataSource;

    @BeforeClass
    public static void setup() {
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
        dataSource.setUrl("jdbc:hsqldb:mem:easql");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        createSchema();
        installData();
    }

    private static void createSchema() {
        execute(generateCreateSQL(Person.class));
        execute(generateCreateSQL(Dog.class));
        execute(generateCreateSQL(Cat.class));
    }

    private static void installData() {
        try {
            execute(readFileToString(new File(getSqlFile())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void execute(String sql) {
        JdbcTemplate db = new JdbcTemplate(getDataSource());
        System.out.println("Executing SQL: " + sql);
        db.execute(sql);
    }

    public static String getSqlFile() {
        return sqlFile;
    }

    public static void setSqlFile(String sqlFile) {
        DataDrivenTestEnvironment.sqlFile = sqlFile;
    }

    public static BasicDataSource getDataSource() {
        return dataSource;
    }
}
