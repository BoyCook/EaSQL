package org.cccs.easql.domain;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
}
