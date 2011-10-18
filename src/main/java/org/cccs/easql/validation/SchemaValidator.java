package org.cccs.easql.validation;

import org.cccs.easql.domain.DBTable;
import org.cccs.easql.domain.TableColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static java.lang.String.format;

/**
 * User: boycook
 * Date: 12/09/2011
 * Time: 11:07
 */
public class SchemaValidator<T> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final Class<T> tClass;
    private DBTable table;

    public SchemaValidator(Class<T> tClass) {
        this.tClass = tClass;
    }

    public void validateConstructor() throws ValidationFailureException {
        final String errMessage = "Error invoking constructor";
        try {
            if (tClass.getConstructor().newInstance() == null) {
                log.error("Constructor not found for: " + tClass.getName());
                throw new ValidationFailureException(tClass.getName() + " must have a constructor");
            }
        } catch (InstantiationException e) {
            log.error(errMessage, e);
            throw new ValidationFailureException(tClass.getName() + " must have a constructor", e);
        } catch (IllegalAccessException e) {
            log.error(errMessage, e);
            throw new ValidationFailureException(tClass.getName() + " must have a constructor", e);
        } catch (InvocationTargetException e) {
            log.error(errMessage, e);
            throw new ValidationFailureException(tClass.getName() + " must have a constructor", e);
        } catch (NoSuchMethodException e) {
            log.error(errMessage, e);
            throw new ValidationFailureException(tClass.getName() + " must have a constructor", e);
        }
    }

    public void validateId() {
        if (getTable().id == null) {
            throw new UnsupportedOperationException(format("[%s] has no key to search by", tClass.getSimpleName()));
        }
    }

    public void validateKey() {
        if (getTable().key == null) {
            throw new UnsupportedOperationException(format("[%s] has no key to search by", tClass.getSimpleName()));
        }
    }

    public void validateWhere(final Map<String, String> where) {
        Collection<TableColumn> columns = new ArrayList<TableColumn>();
        columns.add(getTable().id);
        columns.addAll(Arrays.asList(getTable().columns));
        columns.addAll(Arrays.asList(getTable().one2one));
        columns.addAll(Arrays.asList(getTable().many2one));

        for (String key : where.keySet()) {
            if (!containsKey(columns, key)) {
                throw new UnsupportedOperationException(format("[%s] has no column [%s] to search by", tClass.getSimpleName(), key));
            }
        }
    }

    private boolean containsKey(final Collection<TableColumn> columns, final String key) {
        boolean contains = false;
        for (TableColumn column : columns) {
            if (column.getName().equals(key)) {
                contains = true;
            }
        }
        return contains;
    }

    private DBTable getTable() {
        if (this.table == null) {
            this.table = org.cccs.easql.cache.ClassCache.getTable(tClass);
        }
        return this.table;
    }
}
