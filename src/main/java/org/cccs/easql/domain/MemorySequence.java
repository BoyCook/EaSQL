package org.cccs.easql.domain;

/**
 * User: boycook
 * Date: 21/07/2011
 * Time: 14:00
 */
public class MemorySequence implements Sequence {

    private final String name;
    private final int incrementBy;
    private final int startsWith;
    private long counter = 0;

    public MemorySequence(String name, int startsWith, int incrementBy) {
        this.name = name;
        this.startsWith = startsWith;
        this.counter = startsWith;
        this.incrementBy = incrementBy;
    }

    public String getValue() {
        counter = counter + incrementBy;
        return String.valueOf(counter);
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

    public String getName() {
        return name;
    }

    public int getStartsWith() {
        return startsWith;
    }

    public int getIncrementBy() {
        return incrementBy;
    }
}
