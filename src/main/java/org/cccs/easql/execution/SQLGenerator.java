package org.cccs.easql.execution;

import org.cccs.easql.Cardinality;
import org.cccs.easql.Relation;
import org.cccs.easql.domain.RelationMapping;

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

    public String getUpdateSQL(Object original, Object updated) {
        StringBuilder insertSQL = new StringBuilder();
        StringBuilder updateSQL = new StringBuilder();
        StringBuilder deleteSQL = new StringBuilder();
        long objectKey = getPrimaryValueAsLong(updated);

        if (!original.equals(updated)) {
            System.out.println("Objects are different");
            updateSQL.append(generateUpdateSQL(updated));
        }

        //Checking One2Many collections (without link tables)
        if (hasRelations(updated.getClass(), Cardinality.ONE_TO_MANY)) {
            System.out.println("Checking One2Many...");

            for (RelationMapping mapping : getRelations(updated.getClass(), Cardinality.ONE_TO_MANY)) {
                Collection list = (Collection) getValue(mapping, updated);
                Collection dbList = (Collection) getValue(mapping, original);
                Collection addRelation = new ArrayList();
                Collection removeRelation = new ArrayList();
                compareLists(dbList, list, addRelation, removeRelation);

                //Delete items removed from list
                for (Object remove : removeRelation) {
                    deleteSQL.append(generateDeleteSQL(remove));
                }

                for (Object add : addRelation) {
                    //If item already exists update the foreign key
                    if (getPrimaryValueAsLong(add) > 0) {
                        final Relation relation = getRelation(updated.getClass(), add.getClass());
                        updateSQL.append(format(generateUpdateSQLForRelation(add, relation), objectKey));
                    } else {
                        //If item doesn't exist create new
                        insertSQL.append(format(generateInsertSQL(add), objectKey));
                    }
                }
            }
        }

        //Checking Many2Many collections (with link tables)
        if (hasRelations(updated.getClass(), Cardinality.MANY_TO_MANY)) {
            System.out.println("Checking Many2Many...");
            for (RelationMapping mapping : getRelations(updated.getClass(), Cardinality.MANY_TO_MANY)) {
                Collection list = (Collection) getValue(mapping, updated);
                Collection dbList = (Collection) getValue(mapping, original);
                Collection addRelation = new ArrayList();
                Collection removeRelation = new ArrayList();
                compareLists(dbList, list, addRelation, removeRelation);

                //Delete items removed from list
                for (Object remove : removeRelation) {
                    deleteSQL.append(generateDeleteSQL(mapping.relation, updated, remove));
                }

                for (Object add : addRelation) {
                    //If item doesn't exist then create
                    if ((getPrimaryValueAsLong(add) < 1)) {
                        insertSQL.append(format(generateInsertSQL(add), objectKey));
                    }
                    insertSQL.append(generateInsertSQL(mapping.relation, updated, add));
                }
            }
        }

        //Checking Many2One foreign key relations (Objects)
        if (hasRelations(updated.getClass(), Cardinality.MANY_TO_ONE)) {
            System.out.println("Checking Many2One...");
            //Update foreign key of table
            for (RelationMapping mapping : getRelations(updated.getClass(), Cardinality.MANY_TO_ONE)) {
                final Object value = getValue(mapping, updated);
                updateSQL.append(format(generateUpdateSQLForRelation(updated, mapping.relation), getPrimaryValue(value)));
            }
        }

        StringBuilder sql = new StringBuilder();
        sql.append(insertSQL.toString());
        sql.append(deleteSQL.toString());
        sql.append(updateSQL.toString());
        return sql.toString();
    }
}
