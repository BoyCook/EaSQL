package org.cccs.easql.domain;

import org.cccs.easql.Cardinality;
import org.cccs.easql.Column;
import org.cccs.easql.Relation;
import org.cccs.easql.Table;

import java.util.Collection;

/**
 * User: boycook
 * Date: 12/07/2011
 * Time: 15:53
 */
@Table(name = "countries")
public class Country {

    @Column(primaryKey = true, name = "cntId", sequence = "cnt_seq")
    public long id;
    @Column(unique = true, mandatory = true)
    public String name;
    @Relation(cardinality = Cardinality.MANY_TO_MANY, linkTable = "dog_countries", linkedBy = {"cntId", "dog_id"}, end = Relation.End.LEFT)
    public Collection<Dog> dogs;
    @Relation(cardinality = Cardinality.MANY_TO_MANY, linkTable = "cat_countries", linkedBy = {"cntId", "cat_id"}, end = Relation.End.LEFT)
    public Collection<Cat> cats;

    public Country() {
    }

    public Country(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return id == country.id && name.equals(country.name);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + name.hashCode();
        return result;
    }
}
