package org.cccs.easql.validation;

import org.cccs.easql.domain.Cat;
import org.cccs.easql.domain.NoDefaultConstructor;
import org.cccs.easql.validation.SchemaValidator;
import org.cccs.easql.validation.ValidationFailureException;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * User: boycook
 * Date: 12/09/2011
 * Time: 11:15
 */
public class TestSchemaValidator {

    @Test
    public void validatorShouldWorkWhenConstructorExists() throws ValidationFailureException {
        getValidator().validate(Cat.class);
    }

    @Test(expected = ValidationFailureException.class)
    public void validatorShouldThrowExceptionWhenConstructorDoesNotExist() throws ValidationFailureException {
        getValidator().validate(NoDefaultConstructor.class);
    }

    private SchemaValidator getValidator() {
        return new SchemaValidator();
    }
}
