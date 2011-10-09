package org.cccs.easql.domain;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * User: boycook
 * Date: 26/09/2011
 * Time: 14:24
 */
public class TestDomainObjects {

    @Test
    public void equalsAndHashCodeShouldWorkForLinkTableWhenObjectsAreTheSame() {
        final LinkTable table1 = new LinkTable("link1", "left", "right");
        final LinkTable table2 = new LinkTable("link1", "left", "right");
        assertTrue(table1.equals(table2));
    }

    @Test
    public void equalsAndHashCodeShouldFailForLinkTableWhenNamesAreDifferent() {
        final LinkTable table1 = new LinkTable("link1", "left", "right");
        final LinkTable table2 = new LinkTable("link2", "left", "right");
        assertFalse(table1.equals(table2));
    }

    @Test
    public void equalsAndHashCodeShouldFailForLinkTableWhenLeftKeysAreDifferent() {
        final LinkTable table1 = new LinkTable("link1", "left", "right");
        final LinkTable table2 = new LinkTable("link1", "left1", "right");
        assertFalse(table1.equals(table2));
    }

    @Test
    public void equalsAndHashCodeShouldFailForLinkTableWhenRightKeysAreDifferent() {
        final LinkTable table1 = new LinkTable("link1", "left", "right");
        final LinkTable table2 = new LinkTable("link1", "left", "right1");
        assertFalse(table1.equals(table2));
    }

    @Test
    public void equalsAndHashCodeShouldFailForLinkTableWhenObjectIsNull() {
        final LinkTable table1 = new LinkTable("link1", "left", "right");
        assertFalse(table1.equals(null));
    }

    @Test
    public void equalsAndHashCodeShouldFailForLinkTableWhenOriginalObjectIsNull() {
        final LinkTable table1 = new LinkTable("link1", "left", "right");
        assertFalse(null == table1);
    }

    @Test
    public void equalsAndHashCodeShouldFailForLinkTableWhenClassTypesAreDifferent() {
        final LinkTable table1 = new LinkTable("link1", "left", "right");
        assertFalse(table1.equals(Cat.class));
    }

    @Test
    public void constructingMemorySequenceShouldWork() {
        final MemorySequence seq = new MemorySequence("seq", 0, 1);
        assertThat(seq.getName(), is(equalTo("seq")));
        assertThat(seq.getStartsWith(), is(equalTo(0)));
        assertThat(seq.getIncrementBy(), is(equalTo(1)));
    }

    @Test
    public void memorySequenceGetValueShouldWork() {
        final MemorySequence seq = new MemorySequence("seq", 0, 1);
        assertThat(seq.getValue(), is(equalTo("1")));
        assertThat(seq.getValue(), is(equalTo("2")));
    }

    @Test
    public void memorySequenceSetCounterShouldWork() {
        final MemorySequence seq = new MemorySequence("seq", 0, 1);
        assertThat(seq.getValue(), is(equalTo("1")));
        seq.setCounter(0);
        assertThat(seq.getValue(), is(equalTo("1")));
    }
}
