package org.cccs.easql.execution;

import org.cccs.easql.config.DataDrivenTestEnvironment;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.jdbc.support.incrementer.DB2SequenceMaxValueIncrementer;

/**
 * User: boycook
 * Date: 21/07/2011
 * Time: 22:18
 */
@Ignore
public class TestSequences extends DataDrivenTestEnvironment {

    @Before
    public void beforeEach() {
        setSqlFile(null);
        setup();
        service = new Service(getDataSource());
        query = new Finder(getDataSource());
    }

    @Test
    public void getSequenceValueShouldWork() {
//        AbstractSequenceMaxValueIncrementer;
//        PostgreSQLSequenceMaxValueIncrementer foo;
//        DB2SequenceMaxValueIncrementer bar = new DB2SequenceMaxValueIncrementer(getDataSource(), "person_seq");
//        bar.nextLongValue();
    }

}
