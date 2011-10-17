package org.cccs.easql.execution;

import org.cccs.easql.validation.DataValidator;
import org.cccs.easql.validation.DefaultDataValidator;
import org.cccs.easql.validation.ValidationFailureException;
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
    private DataValidator validator;

    public Service(DataSource dataSource) {
        this.dataSource = dataSource;
        this.validator = new DefaultDataValidator();
        this.query = new Finder(this.dataSource);
    }

    public void insert(Object o) throws ValidationFailureException {
        validator.validateCreate(o);
        SQLGenerator sql = new SQLGenerator();
        execute(sql.getInsertSQL(o));
    }

    public void update(Object updated) throws ValidationFailureException, EntityNotFoundException {
        validator.validateUpdate(updated);
        Object original = query.findById(updated.getClass(), getPrimaryValueAsLong(updated));
        SQLGenerator sql = new SQLGenerator();
        execute(sql.getUpdateSQL(original, updated));
    }

    public void delete(Object o) throws ValidationFailureException {
        validator.validateDelete(o);
        SQLGenerator sql = new SQLGenerator();
        execute(sql.getDeleteSQL(o));
    }

    private void execute(String sql) {
        Executor db = new Executor(this.dataSource);
        db.execute(sql);
    }
}
