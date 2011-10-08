package org.cccs.easql.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * User: boycook
 * Date: 12/09/2011
 * Time: 11:07
 */
public class SchemaValidator {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public void validate(final Class c) throws ValidationFailureException {
        final String errMessage = "Error invoking constructor";
        try {
            if (c.getConstructor().newInstance() == null) {
                log.error("Constructor not found for: " + c.getName());
                throw new ValidationFailureException(c.getName() + " must have a constructor");
            }
        } catch (InstantiationException e) {
            log.debug(errMessage);
            e.printStackTrace();
            throw new ValidationFailureException(c.getName() + " must have a constructor", e);
        } catch (IllegalAccessException e) {
            log.debug(errMessage);
            e.printStackTrace();
            throw new ValidationFailureException(c.getName() + " must have a constructor", e);
        } catch (InvocationTargetException e) {
            log.debug(errMessage);
            e.printStackTrace();
            throw new ValidationFailureException(c.getName() + " must have a constructor", e);
        } catch (NoSuchMethodException e) {
            log.debug(errMessage);
            e.printStackTrace();
            throw new ValidationFailureException(c.getName() + " must have a constructor", e);
        }
    }
}
