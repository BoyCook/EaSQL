package org.cccs.easql.execution;

import org.cccs.easql.Cardinality;
import org.cccs.easql.Column;
import org.cccs.easql.Relation;
import org.cccs.easql.Table;
import org.cccs.easql.domain.LinkTable;
import org.cccs.easql.domain.MemorySequence;
import org.cccs.easql.domain.Sequence;
import org.reflections.Reflections;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.cccs.easql.execution.SQLGenerator.*;
import static org.cccs.easql.util.ClassUtils.getPrimaryColumn;

/**
 * User: boycook
 * Date: 20/07/2011
 * Time: 22:37
 */
public final class Schema {
    //TODO: consider making this static
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
        if (tables == null) {
            tables = gatherTables();
        }
        return tables;
    }

    public static Set<LinkTable> getLinkTables() {
        if (linkTables == null) {
            linkTables = gatherLinkTables();
        }
        return linkTables;
    }

    public static Sequence getSequence(String name) {
        return getSequences().get(name);
    }

    public static Map<String, Sequence> getSequences() {
        if (sequences == null) {
            sequences = gatherSequences();
        }
        return sequences;
    }

    //TODO: handle different DB types
    private static Map<String, Sequence> gatherSequences() {
        Map<String, Sequence> tempSequences = new HashMap<String, Sequence>();

        for (Class<?> table : getTables()) {
            Column column = getPrimaryColumn(table);
            if (isNotEmpty(column.sequence())) {
                tempSequences.put(column.sequence(), new MemorySequence(column.sequence(), 100, 1));
            }
        }

        return tempSequences;
    }

    private static Set<LinkTable> gatherLinkTables() {
        Set<LinkTable> tempLinks = new HashSet<LinkTable>();

        for (Class<?> table : getTables()) {
            for (Field field : table.getFields()) {
                Relation relation = field.getAnnotation(Relation.class);
                if (relation != null && relation.cardinality().equals(Cardinality.MANY_TO_MANY)) {
                    tempLinks.add(new LinkTable(relation.linkTable(), relation.linkedBy()[0], relation.linkedBy()[1]));
                }
            }
        }
        return tempLinks;
    }

    private static Set<Class<?>> gatherTables() {
        Reflections reflections = new Reflections(packageName);
        return reflections.getTypesAnnotatedWith(Table.class);
    }

    private static void createLinkTable(LinkTable linkTable) {
        execute(generateCreateSQL(linkTable));
    }

    private static void createTable(Class c) {
        execute(generateCreateSQL(c));
    }

    private static void execute(String sql) {
        System.out.println("Executing: " + sql);
        JdbcTemplate db = new JdbcTemplate(dataSource);
        db.execute(sql);
    }
}
