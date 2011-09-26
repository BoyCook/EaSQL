package org.cccs.easql.validation;

import org.cccs.easql.domain.ColumnMapping;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.cccs.easql.util.ClassCache.getColumnMappings;
import static org.cccs.easql.util.ObjectUtils.getPrimaryValueAsLong;
import static org.cccs.easql.util.ObjectUtils.getValue;

/**
 * User: boycook
 * Date: 13/09/2011
 * Time: 14:58
 */
public class DefaultDataValidator implements DataValidator {

    @Override
    public void validateCreate(Object o) throws ValidationFailureException {
        final ColumnMapping[] columns = getColumnMappings(o.getClass());
        for (ColumnMapping column : columns) {
            final Object columnValue = getValue(column, o);
            if (column.column.mandatory() && columnValue == null) {
                throw new ValidationFailureException(column.name + " must have a value");
            } else if (column.column.primaryKey() && isEmpty(column.column.sequence()) && getPrimaryValueAsLong(o) == 0) {
                throw new ValidationFailureException("Primary key for " + column.name + " must be specified");
            }
        }
    }

    @Override
    public void validateUpdate(Object o) throws ValidationFailureException {
        final ColumnMapping[] columns = getColumnMappings(o.getClass());
        for (ColumnMapping column : columns) {
            final Object columnValue = getValue(column, o);
            if (column.column.mandatory() && columnValue == null) {
                throw new ValidationFailureException(column.name + " must have a value");
            } else if (column.column.primaryKey() && getPrimaryValueAsLong(o) == 0) {
                throw new ValidationFailureException("Primary key for " + column.name + " must be specified");
            }
        }
    }

    @Override
    public void validateDelete(Object o) throws ValidationFailureException {
    }
}
