package org.cccs.easql.execution;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.util.StopWatch;

import javax.sql.DataSource;
import java.util.Collection;

import static java.lang.String.format;

/**
 * User: boycook
 * Date: 27/07/2011
 * Time: 16:05
 */
public class Executor extends JdbcTemplate {
    private StopWatch clock;

    public Executor(DataSource dataSource) {
        super(dataSource);
    }

    public <T> Collection<T> query(final Class<T> c, final String sql, boolean loadRelations) {
        clock = new StopWatch("QueryExecution");
        clock.start();
        Collection<T> results = query(sql, new Extractor<T>(c, loadRelations));
        clock.stop();
        System.out.println(format("Executing Query [%s] took [%d ms] and returned [%d] result(s)", sql, clock.getLastTaskTimeMillis(), results.size()));
        return results;
    }

    @Deprecated
    public <T> Collection<T> query(final Class<T> c, final String sql, boolean loadRelations, final ResultSetExtractor<Collection<T>> extractor) {
        clock = new StopWatch("QueryExecution");
        clock.start();
        Collection<T> results = query(sql, extractor);
        clock.stop();
        System.out.println(format("Executing Query [%s] took [%d ms] and returned [%d] result(s)", sql, clock.getLastTaskTimeMillis(), results.size()));
        return results;
    }

    public void execute(String sql) {
        clock = new StopWatch("SQLExecution");
        clock.start();
        super.execute(sql);
        clock.stop();
        System.out.println(format("Executing Update [%s] took [%d ms]", sql, clock.getLastTaskTimeMillis()));
    }
}
