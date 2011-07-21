package org.cccs.easql.execution;

import org.cccs.easql.Cardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import static java.lang.String.format;
import static org.cccs.easql.execution.ReflectiveSQLGenerator.*;
import static org.cccs.easql.util.ClassUtils.*;
import static org.cccs.easql.util.ObjectUtils.*;
import static org.cccs.easql.util.ObjectUtils.getPrimaryValue;

/**
 * User: boycook
 * Date: 07/07/2011
 * Time: 10:05
 */
public class Service {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private DataSource dataSource;
    private FInder query;

    public Service(DataSource dataSource) {
        this.dataSource = dataSource;
        this.query = new FInder(this.dataSource);
    }

    public void insert(Object o) {
        String sql = generateInsertSQL(o);
        Object[] relations = getRelations(o, Cardinality.MANY_TO_ONE);

        if (relations.length > 0) {
            for (Object relatedObject : relations) {
                sql = sql.replaceFirst("%s", String.valueOf(getPrimaryValue(relatedObject)));
            }
        }

        execute(sql);
    }

    /*
        TODO handle link tables
        - Find existing asset in DB (use Class and getPrimaryColumn)
        - Diff each @Column field (consider using .equals())
        - For One2Many relations diff Collections
            - Assume create/delete on differences
        - For Many2One change the ID
        - For One2One change the ID
        - For Many2Many ??? assume link table...
     */
    public void update(Object o) {
        StringBuilder insertSQL = new StringBuilder();
        StringBuilder updateSQL = new StringBuilder();
        StringBuilder deleteSQL = new StringBuilder();
        long objectKey = getPrimaryValueAsLong(o);
        Object inDB = query.find(o.getClass(), getPrimaryValueAsLong(o));

        if (!inDB.equals(o)) {
            System.out.println("Objects are different");
            updateSQL.append(generateUpdateSQL(o));
        }

        if (hasRelations(o.getClass(), Cardinality.ONE_TO_MANY)) {
            System.out.println("Checking One2Many...");
            Field[] fields = getRelationFields(o.getClass(), Cardinality.ONE_TO_MANY);

            for (Field field : fields) {
                Collection list = (Collection) getFieldValue(field, o);
                Collection dbList = (Collection) getFieldValue(field, inDB);
                Collection addRelation = new ArrayList();
                Collection removeRelation = new ArrayList();
                compareLists(dbList, list, addRelation, removeRelation);

                for (Object remove : removeRelation) {
                    deleteSQL.append(generateDeleteSQL(remove));
                }

                for (Object add : addRelation) {
                    if (getPrimaryValueAsLong(add) > 0) {
                        updateSQL.append(format(generateUpdateSQLForField(add, getRelatedField(o.getClass(), add.getClass())), objectKey));
                    } else {
                        insertSQL.append(format(generateInsertSQL(add), objectKey));
                    }
                }
            }
        }

        //Checking Objects
        if (hasRelations(o.getClass(), Cardinality.MANY_TO_ONE)) {
            System.out.println("Checking One2One...");
            getRelations(o, Cardinality.MANY_TO_ONE);
            //TODO diff Objects and create UPDATE SQL from diff
        }

        //Checking Link tables
        if (hasRelations(o.getClass(), Cardinality.MANY_TO_MANY)) {
            System.out.println("Checking Many2Many...");
            getRelations(o, Cardinality.MANY_TO_MANY);
            //TODO diff Collections and create UPDATE SQL from diff
        }

        StringBuilder sql = new StringBuilder();
        sql.append(insertSQL.toString());
        sql.append(deleteSQL.toString());
        sql.append(updateSQL.toString());
//        System.out.println(sql.toString());
        execute(sql.toString());
    }

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

    public void delete(Object o) {
        throw new UnsupportedOperationException();
    }

    private void execute(String sql) {
        JdbcTemplate db = new JdbcTemplate(this.dataSource);
        db.execute(sql);
    }
}
