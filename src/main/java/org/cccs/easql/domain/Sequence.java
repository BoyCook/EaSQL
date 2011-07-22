package org.cccs.easql.domain;

/**
 * User: boycook
 * Date: 22/07/2011
 * Time: 12:06
 */
public interface Sequence {

    public String getValue();

    public void setCounter(long counter);

    public String getName();

    public int getStartsWith();

    public int getIncrementBy();

}
