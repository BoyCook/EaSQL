package org.cccs.easql.execution;

import org.cccs.easql.domain.ExtractionMapping;

/**
 * User: boycook
 * Date: 08/10/2011
 * Time: 12:39
 */
public interface MappingsGenerator {
    public ExtractionMapping[] generate(final Class c);
}
