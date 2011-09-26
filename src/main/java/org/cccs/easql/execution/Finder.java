package org.cccs.easql.execution;

import org.cccs.easql.Cardinality;
import org.cccs.easql.domain.RelationMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.cccs.easql.cache.ClassCache.*;
import static org.cccs.easql.execution.SQLUtils.*;
import static org.cccs.easql.util.ClassUtils.getRelations;
import static org.cccs.easql.util.ClassUtils.hasRelations;
import static org.cccs.easql.util.ObjectUtils.*;

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

    @SuppressWarnings({"unchecked"})
    public <T> T findById(final Class<T> c, long id) throws EntityNotFoundException {
        log.debug(format("Searching for [%s] with id [%d]", c.getName(), id));
        Map<String, String> where = new HashMap<String, String>();
        where.put(getPrimaryColumnName(c), String.valueOf(id));
        Collection results = query(c, true, where);

        if (results == null || results.size() == 0) {
            throw new EntityNotFoundException(format("%s with ID %d not found", getTableName(c), id));
        }

        return (T) results.toArray()[0];
    }

    @SuppressWarnings({"unchecked"})
    public <T> T findByKey(final Class<T> c, String key) throws EntityNotFoundException {
        log.debug(format("Searching for [%s] with key [%s]", c.getName(), key));
        Map<String, String> where = new HashMap<String, String>();
        where.put(format("upper(%s)", getUniqueColumnName(c)), String.valueOf(key).toUpperCase());
        Collection results = query(c, true, where);

        if (results == null || results.size() == 0) {
            throw new EntityNotFoundException(format("%s with key %s not found", getTableName(c), key));
        }

        return (T) results.toArray()[0];
    }

    public <T> Collection<T> all(final Class<T> c) {
        return all(c, false);
    }

    public <T> Collection<T> all(final Class<T> c, boolean loadRelations) {
        return query(c, loadRelations, new HashMap<String, String>());
    }

    //TODO: implement a better way of generating where clauses i.e. search predicate
    //TODO: throw exception if Class is incorrectly annotated
    public <T> Collection<T> query(final Class<T> c, boolean loadRelations, Map<String, String> whereClauses) {
        log.debug(format("Searching for [%s]", c.getName()));
        String sql = generateSelectSQL(c, loadRelations);
        final String where = generateWhere(whereClauses);

        if (isNotEmpty(where)) {
            sql = sql + where;
        }

        Collection<T> results = query(c, sql, loadRelations);

        if (loadRelations && hasRelations(c, Cardinality.ONE_TO_MANY)) {
            loadOneToMany(results);
        }

        if (loadRelations && hasRelations(c, Cardinality.MANY_TO_MANY)) {
            loadManyToMany(results);
        }

        return results;
    }

    public <T> Collection<T> query(final Class<T> c, final String sql, boolean loadRelations) {
        final Executor executor = new Executor(this.dataSource);
        return executor.query(c, sql, loadRelations);
    }

    @SuppressWarnings({"unchecked"})
    private void loadOneToMany(Collection results) {
        log.debug("Loading One2Many foreign key (object) relations");
        for (Object result : results) {
            RelationMapping[] relations = getRelations(result.getClass(), Cardinality.ONE_TO_MANY);
            for (RelationMapping relation : relations) {
                Class relatedClass = relation.getGenericType();
                Map<String, String> where = new HashMap<String, String>();
                where.put(relation.relation.key(), getPrimaryValue(result).toString());
                setValue(relation.property, result, query(relatedClass, false, where));
            }
        }
    }

    @SuppressWarnings({"unchecked"})
    private void loadManyToMany(Collection results) {
        log.debug("Loading Many2Many link table (collection) relations");
        for (Object result : results) {
            RelationMapping[] relations = getRelations(result.getClass(), Cardinality.MANY_TO_MANY);
            for (RelationMapping relation : relations) {
                final Class relatedClass = relation.getGenericType();
                final String sql = generateSelectSQLForManyToMany(relatedClass, relation.relation, getPrimaryValueAsLong(result));
                final Collection relatedResults = query(relatedClass, sql, false);
                setValue(relation.property, result, relatedResults);
            }
        }
    }
}
