package org.cccs.easql.execution;

import org.cccs.easql.Cardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.cccs.easql.execution.ReflectiveSQLGenerator.generateInsertSQL;
import static org.cccs.easql.util.ClassUtils.getPrimaryColumn;
import static org.cccs.easql.util.ClassUtils.hasRelations;
import static org.cccs.easql.util.ObjectUtils.*;

/**
 * User: boycook
 * Date: 07/07/2011
 * Time: 10:05
 */
public class EaSQLService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private DataSource dataSource;
    private EaSQLQuery query;

    public EaSQLService(DataSource dataSource) {
        this.dataSource = dataSource;
        this.query = new EaSQLQuery(this.dataSource);
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
        Object inDB = findInDB(o);

        if (!inDB.equals(o)) {
            System.out.println("Objects are different");
            //TODO create UPDATE SQL from diff
        }

        //Checking Collections
        if (hasRelations(o.getClass(), Cardinality.ONE_TO_MANY)) {
            System.out.println("Object has Collections, checking...");

            Field[] fields = getRelationFields(o.getClass(), Cardinality.ONE_TO_MANY);

            for (Field field : fields) {
                Collection dbList = (Collection) getFieldValue(field, inDB);
                Collection list = (Collection) getFieldValue(field, o);
            }

            //TODO diff items in Collections and create UPDATE SQL from diff
        }

        //Checking Objects
        if (hasRelations(o.getClass(), Cardinality.MANY_TO_ONE)) {
            System.out.println("Object has relations, checking...");
            getRelations(o, Cardinality.MANY_TO_ONE);
            //TODO diff Objects and create UPDATE SQL from diff
        }

        //Checking Link tables
        if (hasRelations(o.getClass(), Cardinality.MANY_TO_MANY)) {
            System.out.println("Object has link table relations, checking...");
            getRelations(o, Cardinality.MANY_TO_MANY);
            //TODO diff Collections and create UPDATE SQL from diff
        }
    }

    public void delete(Object o) {
        throw new UnsupportedOperationException();
    }

    private Object findInDB(Object o) {
        Map<String, String> where = new HashMap<String, String>();
        where.put(getPrimaryColumn(o.getClass()), getPrimaryValue(o).toString());
        return query.execute(o.getClass(), true, where);
    }

    private void execute(String sql) {
        JdbcTemplate db = new JdbcTemplate(this.dataSource);
        db.execute(sql);
    }
}
