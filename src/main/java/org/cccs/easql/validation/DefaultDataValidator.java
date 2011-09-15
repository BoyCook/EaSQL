package org.cccs.easql.validation;

import org.cccs.easql.Column;

import java.lang.reflect.Field;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.cccs.easql.util.ClassUtils.getColumnName;
import static org.cccs.easql.util.ObjectUtils.getFieldValue;

/**
 * User: boycook
 * Date: 13/09/2011
 * Time: 14:58
 */
public class DefaultDataValidator implements DataValidator {

    @Override
    public void validateCreate(Object o) throws ValidationFailureException {
        final Class c = o.getClass();
        for (Field field : c.getFields()) {
            final Column column = field.getAnnotation(Column.class);
            if (column != null) {
                String columnName = getColumnName(column, field);
                final Object columnValue = getFieldValue(field, o);
                if (column.mandatory() && columnValue == null) {
                    throw new ValidationFailureException(columnName + " must have a value");
                } else if (column.primaryKey() && isEmpty(column.sequence())) {
                    validatePrimaryKey(columnName, columnValue);
                }
            }
        }
    }

    @Override
    public void validateUpdate(Object o) throws ValidationFailureException {
        final Class c = o.getClass();
        for (Field field : c.getFields()) {
            final Column column = field.getAnnotation(Column.class);
            if (column != null) {
                String columnName = getColumnName(column, field);
                final Object columnValue = getFieldValue(field, o);
                if (column.mandatory() && columnValue == null) {
                    throw new ValidationFailureException(columnName + " must have a value");
                } else if (column.primaryKey()) {
                    validatePrimaryKey(columnName, columnValue);
                }
            }
        }
    }

    @Override
    public void validateDelete(Object o) throws ValidationFailureException {
    }

    private void validatePrimaryKey(String column, Object value) throws ValidationFailureException {
        if (value == null) {
            throw new ValidationFailureException("Primary key for " + column + " must be specified");
        } else {
            if (Long.valueOf(value.toString()) == 0l) {
                throw new ValidationFailureException("Primary key for " + column + " must be specified");
            }
        }
    }
}