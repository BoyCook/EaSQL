package org.cccs.easql.validation;

import org.cccs.easql.domain.Cat;
import org.cccs.easql.domain.NoSequence;
import org.junit.Test;

/**
 * User: boycook
 * Date: 13/09/2011
 * Time: 15:20
 */
public class TestDefaultDataValidator {

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
        bagpuss.id = 1;
        getValidator().validateUpdate(bagpuss);
    }

    private DefaultDataValidator getValidator() {
        return new DefaultDataValidator();
    }
}
