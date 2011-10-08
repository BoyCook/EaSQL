package org.cccs.easql.execution;

import org.cccs.easql.domain.ExtractionMapping;

import java.util.Collection;

import static org.cccs.easql.util.ObjectUtils.getNewObject;

/**
 * User: boycook
 * Date: 08/10/2011
 * Time: 12:56
 */
public class BasicMappingsGenerator extends BaseMappingsGenerator implements MappingsGenerator {
    @Override
    public ExtractionMapping[] generate(final Class c) {
        final Object o = getNewObject(c);
        final Collection<ExtractionMapping> mappings = generateMappingForClass(c, o);
        return mappings.toArray(new ExtractionMapping[mappings.size()]);
    }
}
