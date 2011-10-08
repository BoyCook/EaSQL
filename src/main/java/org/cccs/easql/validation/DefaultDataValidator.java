package org.cccs.easql.validation;

import org.cccs.easql.domain.DBTable;
import org.cccs.easql.domain.TableColumn;

import static org.cccs.easql.cache.ClassCache.getTable;

/**
 * User: boycook
 * Date: 13/09/2011
 * Time: 14:58
 */
public class DefaultDataValidator implements DataValidator {

    @Override
    public void validateCreate(Object o) throws ValidationFailureException {
        final DBTable table = getTable(o.getClass());
        if (table.id != null && table.id.getGeneratedValue() == null && Long.valueOf(table.id.getValue(o).toString()) == 0) {
            throw new ValidationFailureException("Primary key " + table.id.getName() + " for " + table.getName() + " must be specified");
        }
        for (TableColumn column : table.columns) {
            if (!column.getColumn().nullable() && column.getValue(o) == null) {
                throw new ValidationFailureException(column.getName() + " must have a value");
            }
        }
    }

    @Override
    public void validateUpdate(Object o) throws ValidationFailureException {
        final DBTable table = getTable(o.getClass());
        if (table.id != null && Long.valueOf(table.id.getValue(o).toString()) == 0) {
            throw new ValidationFailureException("Primary key " + table.id.getName() + " for " + table.getName() + " must be specified");
        }
        for (TableColumn column : table.columns) {
            if (!column.getColumn().nullable() && column.getValue(o) == null) {
                throw new ValidationFailureException(column.getName() + " must have a value");
            }
        }
    }

    @Override
    public void validateDelete(Object o) throws ValidationFailureException {
    }
}
