package org.cccs.easql.validation;

import org.cccs.easql.domain.Cat;
import org.cccs.easql.domain.Invalid;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * User: boycook
 * Date: 12/09/2011
 * Time: 11:15
 */
public class TestSchemaValidator {

    @Test(expected = ValidationFailureException.class)
    public void validatorShouldThrowExceptionWhenConstructorDoesNotExist() throws ValidationFailureException {
        getValidator(Invalid.class).validateConstructor();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void validatorShouldThrowExceptionWhenIdDoesNotExist() {
        getValidator(Invalid.class).validateId();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void validatorShouldThrowExceptionWhenKeyDoesNotExist() {
        getValidator(Invalid.class).validateKey();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void validatorShouldThrowExceptionWhenWhereColumnDoesNotExist() {
        getValidator(Cat.class).validateWhere(getWhere("foo", "bar"));
        getValidator(Invalid.class).validateWhere(getWhere("foo", "bar"));
    }

    @Test
    public void validatorShouldWorkWhenConstructorExists() throws ValidationFailureException {
        getValidator(Cat.class).validateConstructor();
    }

    @Test
    public void validatorShouldWorkWhenIdExists() {
        getValidator(Cat.class).validateId();
    }

    @Test
    public void validatorShouldWorkWhenKeyExists() {
        getValidator(Cat.class).validateKey();
    }

    @Test
    public void validatorShouldNotThrowExceptionWhenWhereColumnDoesExist() {
        getValidator(Cat.class).validateWhere(getWhere("id", "bar"));
        getValidator(Cat.class).validateWhere(getWhere("name", "bar"));
        getValidator(Cat.class).validateWhere(getWhere("person_id", "bar"));
    }

    private Map<String, String> getWhere(final String key, final String value) {
        final Map<String, String> where = new HashMap<String, String>();
        where.put(key, value);
        return where;
    }

    @SuppressWarnings({"unchecked"})
    private SchemaValidator getValidator(final Class tClass) {
        return new SchemaValidator(tClass);
    }
}
