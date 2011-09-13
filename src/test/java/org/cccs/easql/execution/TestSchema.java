package org.cccs.easql.execution;

import org.cccs.easql.domain.LinkTable;
import org.cccs.easql.domain.Sequence;
import org.cccs.easql.util.FieldSchema;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

/**
 * User: boycook
 * Date: 21/07/2011
 * Time: 13:33
 */
public class TestSchema {

    @Before
    public void setup() {
        FieldSchema.setup();
        Schema.packageName = "org.cccs.easql";
    }

    @Test
    public void getTablesShouldWork() {
        Set<Class<?>> tables = Schema.getTables();
        assertThat(tables.size(), is(equalTo(4)));
    }

    @Test
    public void getLinkTablesShouldWork() {
        Set<LinkTable> linkTables = Schema.getLinkTables();
        assertThat(linkTables.size(), is(equalTo(2)));
    }

    @Test
    public void getSequencesShouldWork() {
        Map<String, Sequence> sequences = Schema.getSequences();
        assertThat(sequences.size(), is(equalTo(4)));
    }
}
