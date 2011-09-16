package org.cccs.easql.domain;

/**
 * User: boycook
 * Date: 29/06/2011
 * Time: 00:32
 */
public class ExtractionMapping {

    public final String property;
    public final String name;
    public final Class type;
    public Object object;

    public ExtractionMapping(String property, String name, Class type) {
        this(property, name, type, null);
    }

    public ExtractionMapping(String property, String name, Class type, Object object) {
        this.property = property;
        this.name = name;
        this.type = type;
        this.object = object;
    }
}
