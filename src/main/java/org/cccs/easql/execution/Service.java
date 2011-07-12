package org.cccs.easql.execution;

import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

import static org.cccs.easql.execution.ReflectiveSQLGenerator.generateInsertSQL;
import static org.cccs.easql.util.ObjectUtils.getPrimaryValue;
import static org.cccs.easql.util.ObjectUtils.getRelations;

/**
 * User: boycook
 * Date: 07/07/2011
 * Time: 10:05
 */
public class Service {

    private DataSource dataSource;

    public Service(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insert(Object o) {
        String sql = generateInsertSQL(o);
        Object[] relations = getRelations(o);

        if (relations.length > 0) {
            for (Object relatedObject : relations) {
                sql = sql.replaceFirst("%s", String.valueOf(getPrimaryValue(relatedObject)));
            }
        }

        execute(sql);
    }

    public void update(Object o) {
        throw new UnsupportedOperationException();
    }

    public void delete(Object o) {
        throw new UnsupportedOperationException();
    }

    private void execute(String sql) {
        JdbcTemplate db = new JdbcTemplate(this.dataSource);
        db.execute(sql);
    }
}
