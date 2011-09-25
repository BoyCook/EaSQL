package org.cccs.easql.domain.accessors;

import org.cccs.easql.Cardinality;
import org.cccs.easql.Column;
import org.cccs.easql.Relation;
import org.cccs.easql.domain.Country;
import org.cccs.easql.domain.Person;

import java.util.Collection;

/**
 * User: boycook
 * Date: 13/09/2011
 * Time: 15:37
 */
public class NoSequence {

    private long id;
    private String name;
    private Person owner;
    private Collection<Country> countries;

    public NoSequence(String name) {
        this.name = name;
    }

    @Column(primaryKey = true)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(unique = true, mandatory = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Relation(cardinality = Cardinality.MANY_TO_ONE, key = "person_id", name = "person2cat")
    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    @Relation(cardinality = Cardinality.MANY_TO_MANY, linkTable = "cat_countries", linkedBy = {"cntId", "cat_id"})
    public Collection<Country> getCountries() {
        return countries;
    }

    public void setCountries(Collection<Country> countries) {
        this.countries = countries;
    }
}
