package org.cccs.easql.execution;

import org.cccs.easql.Cardinality;
import org.cccs.easql.Relation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StopWatch;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.cccs.easql.execution.ReflectiveSQLGenerator.generateSelectSQL;
import static org.cccs.easql.execution.ReflectiveSQLGenerator.generateWhere;
import static org.cccs.easql.util.ReflectionUtils.*;

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
        return execute(c, loadRelations, new HashMap<String, String>());
    }

    public Collection execute(final Class c, boolean loadRelations, Map<String, String> whereClauses)  {
        String sql = generateSelectSQL(c, loadRelations);
        final String where = generateWhere(whereClauses);

        if (isNotEmpty(where)) {
            sql = sql + where;
        }

        final GenericQuery query = new GenericQuery(this.dataSource);
        Collection results = query.execute(c, sql, loadRelations);

        if (loadRelations && hasOneToMany(c)) {
            loadRelatedEntities(results);
        }

        return results;
    }

    private void loadRelatedEntities(Collection results) {
        for (Object result : results) {
            Class c = result.getClass();
            Field[] fields = c.getFields();
            for (Field field : fields) {
                Relation relation = field.getAnnotation(Relation.class);
                if (relation != null && relation.cardinality().equals(Cardinality.ONE_TO_MANY)) {
                    Class relatedClass = getGenericType(field);
                    Map<String, String> where = new HashMap<String, String>();
                    where.put(relation.key(), getPrimaryValue(result).toString());
                    setObjectValue(field, result, execute(relatedClass, false, where));
                }
            }
        }
    }

    class GenericQuery extends JdbcTemplate {
        private StopWatch clock;

        GenericQuery(DataSource dataSource) {
            super(dataSource);
        }

        public Collection execute(final Class c, final String sql, boolean loadRelations) {
            clock = new StopWatch("QueryExecution");
            clock.start();
            Collection<?> results = query(sql, new ReflectiveExtractor(c, loadRelations));
            clock.stop();
            System.out.println(format("Executing SQL [%s] took [%d ms] and returned [%d] result(s)", sql, clock.getLastTaskTimeMillis(), results.size()));
            return results;
        }
    }
}
