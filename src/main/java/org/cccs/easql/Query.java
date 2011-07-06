package org.cccs.easql;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Collection;

import static org.cccs.easql.ReflectiveSQLGenerator.generateSelectSQL;
import static org.cccs.easql.ReflectiveSQLGenerator.getColumns;

/**
 * User: boycook
 * Date: 22/06/2011
 * Time: 14:17
 */
public class Query {

    private DataSource dataSource;

    public Query(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Collection execute(final Class c)  {
        final String sql = generateSelectSQL(c, false);
        final GenericQuery query = new GenericQuery(this.dataSource);
        return query.execute(c, sql);
    }

    class GenericQuery extends JdbcTemplate {
        GenericQuery(DataSource dataSource) {
            super(dataSource);
        }

        public Collection execute(final Class c, final String sql){
            return query(sql, new ReflectiveExtractor(c, getColumns(c)));
        }
    }
}
