package org.cccs.easql.util;

import org.cccs.easql.domain.LinkTable;
import org.cccs.easql.domain.MemorySequence;
import org.cccs.easql.domain.Sequence;
import org.cccs.easql.domain.accessors.*;
import org.cccs.easql.execution.Schema;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: boycook
 * Date: 13/09/2011
 * Time: 19:08
 */
public class MethodSchema {
    public static void setup() {
        setupTables();
        setupLinkTables();
        setupSequences();
    }

    private static void setupTables() {
        final Set<Class<?>> tables = new HashSet<Class<?>>();
        tables.add(Person.class);
        tables.add(Dog.class);
        tables.add(Cat.class);
        tables.add(Country.class);
        Schema.setTables(tables);
    }

    private static void setupLinkTables() {
        final Set<LinkTable> tables = new HashSet<LinkTable>();
        tables.add(new LinkTable("dog_countries", "cntId", "dog_id"));
        tables.add(new LinkTable("cat_countries", "cntId", "cat_id"));
        Schema.setLinkTables(tables);
    }

    private static void setupSequences() {
        Map<String, Sequence> sequences = new HashMap<String, Sequence>();
        sequences.put("cnt_seq", new MemorySequence("cnt_seq", 100, 1));
        sequences.put("dog_seq", new MemorySequence("dog_seq", 100, 1));
        sequences.put("cat_seq", new MemorySequence("cat_seq", 100, 1));
        sequences.put("person_seq", new MemorySequence("person_seq", 100, 1));
        Schema.setSequences(sequences);
    }
}
