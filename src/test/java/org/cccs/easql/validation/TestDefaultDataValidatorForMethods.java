package org.cccs.easql.validation;

import org.cccs.easql.domain.accessors.Cat;
import org.cccs.easql.domain.accessors.NoSequence;
import org.junit.Test;

/**
 * User: boycook
 * Date: 25/09/2011
 * Time: 10:12
 */
public class TestDefaultDataValidatorForMethods {

    @Test(expected = ValidationFailureException.class)
    public void validatorShouldFailForMissingMandatoryFieldOnCreate() throws ValidationFailureException {
        final Cat bagpuss = new Cat();
        getValidator().validateCreate(bagpuss);
    }

    @Test(expected = ValidationFailureException.class)
    public void validatorShouldFailForMissingMandatoryFieldOnUpdate() throws ValidationFailureException {
        final Cat bagpuss = new Cat();
        getValidator().validateUpdate(bagpuss);
    }

    @Test(expected = ValidationFailureException.class)
    public void validatorShouldFailForMissingPrimaryKeyOnCreate() throws ValidationFailureException {
        final NoSequence o = new NoSequence("Foo");
        getValidator().validateCreate(o);
    }

    @Test(expected = ValidationFailureException.class)
    public void validatorShouldFailForMissingPrimaryKeyOnUpdate() throws ValidationFailureException {
        final Cat bagpuss = new Cat("BagPuss", null);
        getValidator().validateUpdate(bagpuss);
    }

    @Test
    public void validatorShouldWorkOnCreateForCorrectCat() throws ValidationFailureException {
        final Cat bagpuss = new Cat("BagPuss", null);
        getValidator().validateCreate(bagpuss);
    }

    @Test
    public void validatorShouldWorkOnUpdateForCorrectCat() throws ValidationFailureException {
        final Cat bagpuss = new Cat("BagPuss", null);
        bagpuss.setId(1);
        getValidator().validateUpdate(bagpuss);
    }

    private DefaultDataValidator getValidator() {
        return new DefaultDataValidator();
    }
}
