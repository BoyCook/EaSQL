package org.cccs.easql.execution;

import org.cccs.easql.config.DataDrivenTestEnvironment;

import java.util.Collection;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * User: boycook
 * Date: 12/10/2011
 * Time: 12:13
 */
@SuppressWarnings({"unchecked"})
public abstract class BaseFinderTest extends DataDrivenTestEnvironment {

    protected Collection assertAll(final Class c, boolean relations, int cnt) throws Exception {
        Collection results = finder.all(c, relations);
        assertThat(results.size(), is(equalTo(cnt)));
        return results;
    }

    protected Collection assertWhere(final Class c, final Map<String, String> where, int cnt) throws Exception {
        final Collection results = finder.query(c, false, where);
        assertThat(results.size(), is(equalTo(cnt)));
        return results;
    }

    protected abstract void assertCraig(Object object);
}
