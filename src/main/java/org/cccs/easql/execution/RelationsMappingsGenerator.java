package org.cccs.easql.execution;

import org.cccs.easql.domain.DBTable;
import org.cccs.easql.domain.ExtractionMapping;
import org.cccs.easql.domain.TableColumn;

import java.util.ArrayList;
import java.util.Collection;

import static org.cccs.easql.cache.ClassCache.getTable;
import static org.cccs.easql.execution.SQLUtils.getJoinTableName;
import static org.cccs.easql.util.ObjectUtils.getNewObject;
import static org.cccs.easql.util.ObjectUtils.setValue;

/**
 * User: boycook
 * Date: 08/10/2011
 * Time: 15:12
 */
public class RelationsMappingsGenerator extends BaseMappingsGenerator implements MappingsGenerator {
    @Override
    public ExtractionMapping[] generate(Class c) {
        final DBTable table = getTable(c);
        final Object o = getNewObject(c);
        final Collection<ExtractionMapping> mappings = new ArrayList<ExtractionMapping>();
        mappings.addAll(generateMappingForClass(c, o));

        for (TableColumn column : table.many2one) {
            //Get ExtractionMapping for related object
            final DBTable relatedTable = getTable(column.getType());
            final Object relatedO = getNewObject(relatedTable.c);
            final String prefix = getJoinTableName(relatedTable, table) + "_";
            mappings.addAll(generateMappingForClass(column.getType(), relatedO, prefix));
            //Set object value
            setValue(column.getProperty(), o, relatedO);
        }

        return mappings.toArray(new ExtractionMapping[mappings.size()]);
    }
}
