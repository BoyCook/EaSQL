package org.cccs.easql.domain;

import org.cccs.easql.Cardinality;
import org.cccs.easql.Column;
import org.cccs.easql.Relation;

import java.util.Collection;

/**
 * User: boycook
 * Date: 13/09/2011
 * Time: 15:37
 */
public class NoSequence {

    @Column(primaryKey = true)
    public long id;
    @Column(unique = true, mandatory = true)
    public String name;
    @Relation(cardinality = Cardinality.MANY_TO_ONE, key = "person_id", name = "person2cat")
    public Person owner;
    @Relation(cardinality = Cardinality.MANY_TO_MANY, linkTable = "cat_countries", linkedBy = {"cntId", "cat_id"})
    public Collection<Country> countries;

    public NoSequence(String name) {
        this.name = name;
    }
}
