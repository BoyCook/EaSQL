package org.cccs.easql.execution;

import org.cccs.easql.domain.DBTable;
import org.cccs.easql.domain.ExtractionMapping;
import org.cccs.easql.domain.TableColumn;

import java.util.ArrayList;
import java.util.Collection;

import static org.cccs.easql.cache.ClassCache.getTable;

/**
 * User: boycook
 * Date: 08/10/2011
 * Time: 15:52
 */
public abstract class BaseMappingsGenerator {
    public Collection<ExtractionMapping> generateMappingForClass(final Class c, final Object o) {
        return generateMappingForClass(c, o, "");
    }

    public Collection<ExtractionMapping> generateMappingForClass(final Class c, final Object o, final String prefix) {
        final DBTable table = getTable(c);
        Collection<ExtractionMapping> mappings = new ArrayList<ExtractionMapping>();
        if (table.id != null) {
            mappings.add(new ExtractionMapping(table.id.getProperty(), prefix + table.id.getName(), table.id.getType(), o));
        }
        for (TableColumn column : table.columns) {
            mappings.add(new ExtractionMapping(column.getProperty(), prefix + column.getName(), column.getType(), o));
        }
        return mappings;
    }
}
