package org.cccs.easql.execution;

import org.cccs.easql.domain.DBTable;
import org.cccs.easql.domain.LinkTable;
import org.cccs.easql.domain.MemorySequence;
import org.cccs.easql.domain.Sequence;
import org.reflections.Reflections;

import javax.persistence.GeneratedValue;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.cccs.easql.cache.ClassCache.getTable;
import static org.cccs.easql.execution.SQLUtils.generateCreateSQL;
import static org.cccs.easql.execution.SQLUtils.generateDeleteSQL;

/**
 * User: boycook
 * Date: 20/07/2011
 * Time: 22:37
 */
public final class Schema {
    public static String packageName;
    public static DataSource dataSource;
    private static Set<Class<?>> tables;
    private static Set<LinkTable> linkTables;
    private static Map<String, Sequence> sequences;
    private static boolean generated = false;

    public static void generate() {
        if (!generated) {
            for (Class<?> table : getTables()) {
                createTable(table);
            }
            for (LinkTable linkTable : getLinkTables()) {
                createLinkTable(linkTable);
            }
            generated = true;
        }
    }

    public static void tearDown() {
        //TODO: truncate from link tables and truncate in correct order
        for (Class<?> table : getTables()) {
            execute(generateDeleteSQL(table));
        }
    }

    public static Set<Class<?>> getTables() {
        if (Schema.tables == null) {
            Schema.tables = gatherTables();
        }
        return Schema.tables;
    }

    public static Set<LinkTable> getLinkTables() {
        if (Schema.linkTables == null) {
            Schema.linkTables = gatherLinkTables();
        }
        return Schema.linkTables;
    }

    public static Sequence getSequence(String name) {
        return getSequences().get(name);
    }

    public static Map<String, Sequence> getSequences() {
        if (Schema.sequences == null) {
            Schema.sequences = gatherSequences();
        }
        return Schema.sequences;
    }

    public static void setTables(Set<Class<?>> tables) {
        Schema.tables = tables;
    }

    public static void setLinkTables(Set<LinkTable> linkTables) {
        Schema.linkTables = linkTables;
    }

    public static void setSequences(Map<String, Sequence> sequences) {
        Schema.sequences = sequences;
    }

    //TODO: handle different DB types
    private static Map<String, Sequence> gatherSequences() {
        Map<String, Sequence> tempSequences = new HashMap<String, Sequence>();
        for (Class<?> table : getTables()) {
            DBTable dbTable = getTable(table);
            GeneratedValue seq = dbTable.id.getGeneratedValue();
            if (seq != null) {
                tempSequences.put(seq.generator(), new MemorySequence(seq.generator(), 100, 1));
            }
        }
        return tempSequences;
    }

    private static Set<LinkTable> gatherLinkTables() {
        Set<LinkTable> tempLinks = new HashSet<LinkTable>();
        for (Class<?> table : getTables()) {
            for (Field field : table.getFields()) {
                JoinTable joinTable = field.getAnnotation(JoinTable.class);
                if (joinTable != null) {
                    tempLinks.add(new LinkTable(joinTable.name(), joinTable.joinColumns()[0].name(), joinTable.inverseJoinColumns()[0].name()));
                }
            }
        }
        return tempLinks;
    }

    private static Set<Class<?>> gatherTables() {
        final Reflections reflections = new Reflections(packageName);
        return reflections.getTypesAnnotatedWith(Table.class);
    }

    private static void createLinkTable(LinkTable linkTable) {
        execute(generateCreateSQL(linkTable));
    }

    private static void createTable(Class c) {
        execute(generateCreateSQL(c));
    }

    private static void execute(String sql) {
        Executor db = new Executor(dataSource);
        db.execute(sql);
    }
}
