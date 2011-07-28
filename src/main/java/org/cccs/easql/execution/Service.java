package org.cccs.easql.execution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import static org.cccs.easql.util.ObjectUtils.getPrimaryValueAsLong;

/**
 * User: boycook
 * Date: 07/07/2011
 * Time: 10:05
 */
public class Service {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private DataSource dataSource;
    private Finder query;

    public Service(DataSource dataSource) {
        this.dataSource = dataSource;
        this.query = new Finder(this.dataSource);
    }

    public void insert(Object o) {
        SQLGenerator sql = new SQLGenerator();
        execute(sql.getInsertSQL(o));
    }

    public void update(Object updated) throws EntityNotFoundException {
        Object original = query.findById(updated.getClass(), getPrimaryValueAsLong(updated));
        SQLGenerator sql = new SQLGenerator();
        execute(sql.getUpdateSQL(original, updated));
    }

    public void delete(Object o) {
        throw new UnsupportedOperationException();
    }

    private void execute(String sql) {
        Executor db = new Executor(this.dataSource);
        db.execute(sql);
    }
}
