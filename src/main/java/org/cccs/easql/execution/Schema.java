package org.cccs.easql.execution;

import org.cccs.easql.Table;
import org.reflections.Reflections;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Set;

import static org.cccs.easql.execution.ReflectiveSQLGenerator.generateCreateSQL;
import static org.cccs.easql.execution.ReflectiveSQLGenerator.generateSelectSQL;

/**
 * User: boycook
 * Date: 20/07/2011
 * Time: 22:37
 */
public class Schema {

    private final String packageName;
    private final DataSource dataSource;

    public Schema(String packageName, DataSource dataSource) {
        this.packageName = packageName;
        this.dataSource = dataSource;
    }

    public void generate() {
        Set<Class<?>> tables = findTables();
        for (Class<?> table : tables) {
            createTable(table);
        }
    }

    private Set<Class<?>> findTables() {
        Reflections reflections = new Reflections(packageName);
        return reflections.getTypesAnnotatedWith(Table.class);
    }

    private void createTable(Class c) {
        try {
            execute(generateSelectSQL(c));
        } catch (BadSqlGrammarException e) {
            execute(generateCreateSQL(c));
        }
    }

    private void execute(String sql) {
        JdbcTemplate db = new JdbcTemplate(dataSource);
        db.execute(sql);
    }
}
