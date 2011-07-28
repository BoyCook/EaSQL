package org.cccs.easql.execution;

import org.cccs.easql.Cardinality;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import static java.lang.String.format;
import static org.cccs.easql.execution.SQLUtils.*;
import static org.cccs.easql.execution.SQLUtils.generateUpdateSQLForRelation;
import static org.cccs.easql.util.ClassUtils.getRelatedField;
import static org.cccs.easql.util.ClassUtils.getRelationFields;
import static org.cccs.easql.util.ClassUtils.hasRelations;
import static org.cccs.easql.util.ObjectUtils.*;
import static org.cccs.easql.util.ObjectUtils.getFieldValue;
import static org.cccs.easql.util.ObjectUtils.getPrimaryValueAsLong;

/**
 * User: boycook
 * Date: 28/07/2011
 * Time: 11:09
 */
public class SQLGenerator {

    public String getInsertSQL(Object o) {
        String sql = generateInsertSQL(o);
        Object[] relations = getRelations(o, Cardinality.MANY_TO_ONE);

        if (relations.length > 0) {
            for (Object relatedObject : relations) {
                sql = sql.replaceFirst("%s", String.valueOf(getPrimaryValue(relatedObject)));
            }
        }
        return sql;
    }

    /*
        TODO handle link tables
        - Find existing asset in DB (use Class and getPrimaryColumnName)
        - Diff each @Column field (consider using .equals())
        - For One2Many relations diff Collections
            - Assume create/delete on differences
        - For Many2One change the ID
        - For One2One change the ID
        - For Many2Many ??? assume link table...
     */
    public String getUpdateSQL(Object original, Object updated) {
        StringBuilder insertSQL = new StringBuilder();
        StringBuilder updateSQL = new StringBuilder();
        StringBuilder deleteSQL = new StringBuilder();
        long objectKey = getPrimaryValueAsLong(updated);

        if (!original.equals(updated)) {
            System.out.println("Objects are different");
            updateSQL.append(generateUpdateSQL(updated));
        }

        if (hasRelations(updated.getClass(), Cardinality.ONE_TO_MANY)) {
            System.out.println("Checking One2Many...");

            for (Field field : getRelationFields(updated.getClass(), Cardinality.ONE_TO_MANY)) {
                Collection list = (Collection) getFieldValue(field, updated);
                Collection dbList = (Collection) getFieldValue(field, original);
                Collection addRelation = new ArrayList();
                Collection removeRelation = new ArrayList();
                compareLists(dbList, list, addRelation, removeRelation);

                for (Object remove : removeRelation) {
                    deleteSQL.append(generateDeleteSQL(remove));
                }

                for (Object add : addRelation) {
                    if (getPrimaryValueAsLong(add) > 0) {
                        updateSQL.append(format(generateUpdateSQLForRelation(add, getRelatedField(updated.getClass(), add.getClass())), objectKey));
                    } else {
                        insertSQL.append(format(generateInsertSQL(add), objectKey));
                    }
                }
            }
        }

        //Checking Objects
        if (hasRelations(updated.getClass(), Cardinality.MANY_TO_ONE)) {
            System.out.println("Checking Many2One...");
            for (Field field : getRelationFields(updated.getClass(), Cardinality.MANY_TO_ONE)) {
                final Object value = getFieldValue(field, updated);
                updateSQL.append(format(generateUpdateSQLForRelation(updated, field), getPrimaryValue(value)));
            }
        }

        //Checking Link tables
        if (hasRelations(updated.getClass(), Cardinality.MANY_TO_MANY)) {
            //TODO diff Collections and create UPDATE SQL from diff
            System.out.println("Checking Many2Many...");
            getRelations(updated, Cardinality.MANY_TO_MANY);
        }

        StringBuilder sql = new StringBuilder();
        sql.append(insertSQL.toString());
        sql.append(deleteSQL.toString());
        sql.append(updateSQL.toString());
        return sql.toString();
    }

    @SuppressWarnings({"unchecked"})
    private void compareLists(Collection original, Collection updated, Collection addRelation, Collection removeRelation) {
        for (Object o : original) {
            if (!updated.contains(o)) {
                //Remove relation
                removeRelation.add(o);
            }
        }

        for (Object o : updated) {
            if (!original.contains(o)) {
                //Add relation
                addRelation.add(o);
            }
        }
    }
}
