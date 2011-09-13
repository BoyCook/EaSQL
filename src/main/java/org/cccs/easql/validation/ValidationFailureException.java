package org.cccs.easql.validation;

/**
 * User: boycook
 * Date: 12/09/2011
 * Time: 11:06
 */
public class ValidationFailureException extends Exception {

    public ValidationFailureException(String s) {
        super(s);
    }

    public ValidationFailureException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
