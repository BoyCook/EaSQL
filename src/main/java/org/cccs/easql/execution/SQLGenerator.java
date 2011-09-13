package org.cccs.easql.execution;

import org.cccs.easql.Cardinality;
import org.cccs.easql.Relation;
import org.cccs.easql.domain.RelationMapping;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import static java.lang.String.format;
import static org.cccs.easql.execution.SQLUtils.*;
import static org.cccs.easql.util.ClassUtils.*;
import static org.cccs.easql.util.ObjectUtils.*;

/**
 * User: boycook
 * Date: 28/07/2011
 * Time: 11:09
 */
public class SQLGenerator {

    public String getInsertSQL(Object o) {
        String sql = generateInsertSQL(o);
        Object[] relations = getRelatedValues(o, Cardinality.MANY_TO_ONE);

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
        - Diff each @Column member (consider using .equals())
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

            for (RelationMapping mapping : getRelations(updated.getClass(), Cardinality.ONE_TO_MANY)) {
                Collection list = (Collection) getValue(mapping, updated);
                Collection dbList = (Collection) getValue(mapping, original);
                Collection addRelation = new ArrayList();
                Collection removeRelation = new ArrayList();
                compareLists(dbList, list, addRelation, removeRelation);

                for (Object remove : removeRelation) {
                    deleteSQL.append(generateDeleteSQL(remove));
                }

                for (Object add : addRelation) {
                    if (getPrimaryValueAsLong(add) > 0) {
                        final Field relatedField = getRelatedField(updated.getClass(), add.getClass());
                        updateSQL.append(format(generateUpdateSQLForRelation(add, relatedField.getAnnotation(Relation.class)), objectKey));
                    } else {
                        insertSQL.append(format(generateInsertSQL(add), objectKey));
                    }
                }
            }
        }

        //Checking Objects
        if (hasRelations(updated.getClass(), Cardinality.MANY_TO_ONE)) {
            System.out.println("Checking Many2One...");

            for (RelationMapping mapping : getRelations(updated.getClass(), Cardinality.MANY_TO_ONE)) {
                final Object value = getValue(mapping, updated);
                updateSQL.append(format(generateUpdateSQLForRelation(updated, mapping.relation), getPrimaryValue(value)));
            }
        }

        //Checking Link tables
        if (hasRelations(updated.getClass(), Cardinality.MANY_TO_MANY)) {
            //TODO diff Collections and create UPDATE SQL from diff
            System.out.println("Checking Many2Many...");
            getRelatedValues(updated, Cardinality.MANY_TO_MANY);
        }

        StringBuilder sql = new StringBuilder();
        sql.append(insertSQL.toString());
        sql.append(deleteSQL.toString());
        sql.append(updateSQL.toString());
        return sql.toString();
    }

    //TODO: move to utils class
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
