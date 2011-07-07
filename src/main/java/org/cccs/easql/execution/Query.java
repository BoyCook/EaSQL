package org.cccs.easql.execution;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Collection;

import static org.cccs.easql.execution.ReflectiveSQLGenerator.generateSelectSQL;
import static org.cccs.easql.util.ReflectionUtils.getExtractionMappings;

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
        return execute(c, false);
    }

    public Collection execute(final Class c, boolean loadRelations)  {
        final String sql = ReflectiveSQLGenerator.generateSelectSQL(c, loadRelations);
        final GenericQuery query = new GenericQuery(this.dataSource);
        return query.execute(c, sql, loadRelations);
    }

    class GenericQuery extends JdbcTemplate {
        GenericQuery(DataSource dataSource) {
            super(dataSource);
        }

        public Collection execute(final Class c, final String sql, boolean loadRelations){
            return query(sql, new ReflectiveExtractor(c, loadRelations));
        }
    }
}
