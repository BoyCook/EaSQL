package org.cccs.easql.execution;

/**
 * User: boycook
 * Date: 22/07/2011
 * Time: 14:49
 */
public class EntityNotFoundException extends Exception {

    public EntityNotFoundException(String s) {
        super(s);
    }

    public EntityNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
