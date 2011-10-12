package org.cccs.easql.execution;

import org.cccs.easql.domain.DBTable;
import org.cccs.easql.domain.TableColumn;
import org.cccs.easql.validation.SchemaValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.sql.DataSource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.cccs.easql.cache.ClassCache.getTable;
import static org.cccs.easql.execution.SQLUtils.generateSelectSQLForManyToMany;
import static org.cccs.easql.util.ClassUtils.hasRelations;
import static org.cccs.easql.util.ObjectUtils.setValue;

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
        log.debug(format("Searching for [%s] with id [%d]", c.getSimpleName(), id));
        final DBTable table = getTable(c);
        if (table.id == null) {
            throw new UnsupportedOperationException(format("[%s] has no key to search by", c.getSimpleName()));
        } else if (id == 0) {
            throw new IllegalArgumentException(format("Search id for [%s] must not be specified", c.getSimpleName()));
        }
        Collection<T> results = query(c, true, getWhere(table.id.getName(), String.valueOf(id)));
        if (results == null || results.size() == 0) {
            throw new EntityNotFoundException(format("%s with ID %d not found", table.getName(), id));
        }
        return (T) results.toArray()[0];
    }

    @SuppressWarnings({"unchecked"})
    public <T> T findByKey(final Class<T> c, String key) throws EntityNotFoundException {
        log.debug(format("Searching for [%s] with key [%s]", c.getSimpleName(), key));
        final DBTable table = getTable(c);
        if (table.key == null) {
            throw new UnsupportedOperationException(format("[%s] has no key to search by", c.getSimpleName()));
        } else if (isEmpty(key)) {
            throw new IllegalArgumentException(format("Search key for [%s] must not be null", c.getSimpleName()));
        }
        Collection<T> results = query(c, true, getWhere(format("upper(%s)", table.key.getName()), String.valueOf(key).toUpperCase()));
        if (results == null || results.size() == 0) {
            throw new EntityNotFoundException(format("%s with key %s not found", table.getName(), key));
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
    public <T> Collection<T> query(final Class<T> c, boolean loadRelations, Map<String, String> where) {
        log.debug(format("Searching for [%s]", c.getSimpleName()));
        final SQLGenerator gen = new SQLGenerator();
        Collection<T> results = query(c, gen.getSelectSQL(c, loadRelations, where), loadRelations);
        if (loadRelations && hasRelations(c, OneToMany.class)) {
            loadOneToMany(results);
        }
        if (loadRelations && hasRelations(c, ManyToMany.class)) {
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
            final DBTable table = getTable(result.getClass());
            for (TableColumn column : table.one2many) {
                Map<String, String> where = new HashMap<String, String>();
                where.put(column.getName(), table.id.getValue(result).toString());
                setValue(column.getProperty(), result, query(column.getGenericType(), false, where));
            }
        }
    }

    @SuppressWarnings({"unchecked"})
    private void loadManyToMany(Collection results) {
        log.debug("Loading Many2Many link table (collection) relations");
        for (Object result : results) {
            final DBTable table = getTable(result.getClass());
            for (TableColumn column : table.many2many) {
                final String sql = generateSelectSQLForManyToMany(column.getGenericType(), column, Long.valueOf(table.id.getValue(result).toString()));
                final Collection relatedResults = query(column.getGenericType(), sql, false);
                setValue(column.getProperty(), result, relatedResults);
            }
        }
    }

    private Map<String, String> getWhere(final String key, final String value) {
        final Map<String, String> where = new HashMap<String, String>();
        where.put(key, value);
        return where;
    }

    private SchemaValidator getValidator(final Class c) {
        return new SchemaValidator(c);
    }
}
