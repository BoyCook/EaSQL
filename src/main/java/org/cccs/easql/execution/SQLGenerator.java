package org.cccs.easql.execution;

import org.cccs.easql.domain.DBTable;
import org.cccs.easql.domain.TableColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.cccs.easql.cache.ClassCache.getTable;
import static org.cccs.easql.execution.SQLUtils.*;
import static org.cccs.easql.util.ClassUtils.*;
import static org.cccs.easql.util.ObjectUtils.*;

/**
 * User: boycook
 * Date: 28/07/2011
 * Time: 11:09
 */
public class SQLGenerator {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public String getSelectSQL(final Class c, boolean loadRelations, final Map<String, String> where) {
        String sql;
        if (loadRelations) {
            sql = generateSelectSQLForOneToMany(c);
        } else {
            sql = generateSelectSQL(c);
        }

        final String whereSQL = generateWhere(where);

        if (isNotEmpty(whereSQL)) {
            sql = sql + whereSQL;
        }
        return sql;
    }

    public String getInsertSQL(Object o) {
        final DBTable table = getTable(o.getClass());
        String sql = generateInsertSQL(o);
        for (TableColumn column : table.many2one) {
            final Object relatedObject = column.getValue(o);
            final DBTable relatedTable = getTable(relatedObject.getClass());
            sql = sql.replaceFirst("%s", relatedTable.id.getValue(relatedObject).toString());
        }
        return sql;
    }

    public String getUpdateSQL(Object original, Object updated) {
        final DBTable table = getTable(original.getClass());
        StringBuilder insertSQL = new StringBuilder();
        StringBuilder updateSQL = new StringBuilder();
        StringBuilder deleteSQL = new StringBuilder();

        long objectKey = Long.valueOf(table.id.getValue(updated).toString());

        if (!original.equals(updated)) {
            log.debug(format("%s [%d] has been updated", original.getClass(), objectKey));
            updateSQL.append(generateUpdateSQL(updated));
        }

        //Checking One2Many collections (without link tables)
        if (hasRelations(updated.getClass(), OneToMany.class)) {
            for (TableColumn mapping : getRelations(updated.getClass(), OneToMany.class)) {
                Collection list = (Collection) getValue(mapping, updated);
                Collection dbList = (Collection) getValue(mapping, original);
                Collection addRelation = new ArrayList();
                Collection removeRelation = new ArrayList();
                compareLists(dbList, list, addRelation, removeRelation);

                //Delete items removed from list
                for (Object remove : removeRelation) {
                    //Person column in Dog class
                    TableColumn relatedColumn = getRelatedColumn(table, mapping);
                    if (relatedColumn.getColumn().nullable()) {
                        updateSQL.append(generateClearForeignKeySQL(remove, relatedColumn));
                    } else {
                        deleteSQL.append(generateDeleteSQL(remove));
                    }
                }

                for (Object add : addRelation) {
                    //If item already exists update the foreign key
                    if (getPrimaryValueAsLong(add) > 0) {
                        TableColumn relation = getRelation(updated.getClass(), add.getClass());
                        updateSQL.append(format(generateUpdateSQLForRelation(add, relation), objectKey));
                    } else {
                        //If item doesn't exist create new
                        insertSQL.append(format(generateInsertSQL(add), objectKey));
                    }
                }
            }
        }

        //Checking Many2Many collections (with link tables)
        if (hasRelations(updated.getClass(), ManyToMany.class)) {

            for (TableColumn mapping : getRelations(updated.getClass(), ManyToMany.class)) {
                Collection list = (Collection) mapping.getValue(updated);
                Collection dbList = (Collection) mapping.getValue(original);
                Collection addRelation = new ArrayList();
                Collection removeRelation = new ArrayList();
                compareLists(dbList, list, addRelation, removeRelation);

                //Delete items removed from list
                for (Object remove : removeRelation) {
                    deleteSQL.append(generateDeleteSQL(mapping, updated, remove));
                }

                for (Object add : addRelation) {
                    //If item doesn't exist then create
                    if ((getPrimaryValueAsLong(add) < 1)) {
                        insertSQL.append(format(generateInsertSQL(add), objectKey));
                    }
                    insertSQL.append(generateInsertSQL(mapping, updated, add));
                }
            }
        }

        //Checking Many2One foreign key relations (Objects)
        if (hasRelations(updated.getClass(), ManyToOne.class)) {
            //Update foreign key of table
            for (TableColumn mapping : getRelations(updated.getClass(), ManyToOne.class)) {
                final Object value = getValue(mapping, updated);
                updateSQL.append(format(generateUpdateSQLForRelation(updated, mapping), getPrimaryValue(value)));
            }
        }

        StringBuilder sql = new StringBuilder();
        sql.append(insertSQL.toString());
        sql.append(deleteSQL.toString());
        sql.append(updateSQL.toString());
        return sql.toString();
    }

    public String getDeleteSQL(Object o) {
        return generateDeleteSQL(o);
    }
}
