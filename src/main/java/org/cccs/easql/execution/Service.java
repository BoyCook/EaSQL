package org.cccs.easql.execution;

/**
 * User: boycook
 * Date: 07/07/2011
 * Time: 10:05
 */
public class Service {

    public void insert(Object o) {
        //Generate INSERT sql for object
        //Check for any @Relation annotations - these will require sub-selects for ID's

        throw new UnsupportedOperationException();
    }

    public void update(Object o) {
        throw new UnsupportedOperationException();
    }

    public void delete(Object o) {
        throw new UnsupportedOperationException();
    }
}
