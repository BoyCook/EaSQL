package org.cccs.easql.domain;

/**
 * User: boycook
 * Date: 21/07/2011
 * Time: 14:00
 */
public class Sequence {

    public final String name;
    public final int startsWith;
    public final int incrementBy;

    private static long counter = 1;

    public Sequence(String name, int startsWith, int incrementBy) {
        this.name = name;
        this.startsWith = startsWith;
        this.incrementBy = incrementBy;
    }

    public static long getCounter() {
        counter++;
        return counter;
    }

    public static void setCounter(long counter) {
        Sequence.counter = counter;
    }
}
