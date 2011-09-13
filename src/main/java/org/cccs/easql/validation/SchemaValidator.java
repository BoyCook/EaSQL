package org.cccs.easql.validation;

import java.lang.reflect.InvocationTargetException;

/**
 * User: boycook
 * Date: 12/09/2011
 * Time: 11:07
 */
public class SchemaValidator {

    public void validate(final Class c) throws ValidationFailureException {
        final String errMessage = "Error invoking constructor";
        try {
            if (c.getConstructor().newInstance() == null) {
                System.out.println("Constructor not found for: " + c.getName());
                throw new ValidationFailureException(c.getName() +  " must have a constructor");
            }
        } catch (InstantiationException e) {
            System.out.println(errMessage);
            e.printStackTrace();
            throw new ValidationFailureException(c.getName() +  " must have a constructor");
        } catch (IllegalAccessException e) {
            System.out.println(errMessage);
            e.printStackTrace();
            throw new ValidationFailureException(c.getName() +  " must have a constructor");
        } catch (InvocationTargetException e) {
            System.out.println(errMessage);
            e.printStackTrace();
            throw new ValidationFailureException(c.getName() +  " must have a constructor");
        } catch (NoSuchMethodException e) {
            System.out.println(errMessage);
            e.printStackTrace();
            throw new ValidationFailureException(c.getName() +  " must have a constructor");
        }
    }
}
