package org.cccs.easql.execution;

import org.cccs.easql.Cardinality;
import org.cccs.easql.Relation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import static org.cccs.easql.util.ClassUtils.*;
import static org.cccs.easql.util.ObjectUtils.getPrimaryValue;
import static org.cccs.easql.util.ObjectUtils.setObjectValue;

/**
 * User: boycook
 * Date: 22/06/2011
 * Time: 14:17
 */
public class Finder {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private DataSource dataSource;

    public Finder(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Object findById(Class c, long id) throws EntityNotFoundException {
        Map<String, String> where = new HashMap<String, String>();
        where.put(getPrimaryColumnName(c), String.valueOf(id));
        Collection results = query(c, true, where);

        if (results == null || results.size() == 0) {
            throw new EntityNotFoundException(format("%s with ID %d not found", getTableName(c), id));
        }

        return results.toArray()[0];
    }

    public Object findByKey(Class c, String key) throws EntityNotFoundException {
        Map<String, String> where = new HashMap<String, String>();
        where.put(format("upper(%s)", getUniqueColumnName(c)), String.valueOf(key).toUpperCase());
        Collection results = query(c, true, where);

        if (results == null || results.size() == 0) {
            throw new EntityNotFoundException(format("%s with key %s not found", getTableName(c), key));
        }

        return results.toArray()[0];
    }

    public Collection query(final Class c) {
        return query(c, false);
    }

    public Collection query(final Class c, boolean loadRelations) {
        return query(c, loadRelations, new HashMap<String, String>());
    }

    //TODO: implement a better way of generating where clauses i.e. search predicate
    //TODO: throw exception if Class is incorrectly annotated
    public Collection query(final Class c, boolean loadRelations, Map<String, String> whereClauses) {
        String sql = generateSelectSQL(c, loadRelations);
        final String where = generateWhere(whereClauses);

        if (isNotEmpty(where)) {
            sql = sql + where;
        }

        final GenericQuery query = new GenericQuery(this.dataSource);
        Collection results = query.execute(c, sql, loadRelations);

        if (loadRelations && hasRelations(c, Cardinality.ONE_TO_MANY)) {
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
                    setObjectValue(field, result, query(relatedClass, false, where));
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
            log.debug(format("Executing SQL [%s] took [%d ms] and returned [%d] result(s)", sql, clock.getLastTaskTimeMillis(), results.size()));
            return results;
        }
    }
}
