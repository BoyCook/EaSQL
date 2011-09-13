package org.cccs.easql.validation;

/**
 * User: boycook
 * Date: 13/09/2011
 * Time: 15:15
 */
public interface DataValidator {
    public void validateCreate(Object o) throws ValidationFailureException;
    public void validateUpdate(Object o) throws ValidationFailureException;
    public void validateDelete(Object o) throws ValidationFailureException;
}
