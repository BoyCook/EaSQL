package org.cccs.easql.domain.accessors;

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

    private long id;
    private String name;
    private Collection<Dog> dogs;
    private Collection<Cat> cats;

    @Column(primaryKey = true, name = "cntId", sequence = "cnt_seq")
    public long getId() {
        return id;
    }

    @Column(unique = true, name = "name")
    public String getName() {
        return name;
    }

    @Relation(cardinality = Cardinality.MANY_TO_MANY, linkTable = "dog_countries", linkedBy = {"cntId", "dog_id"}, end = Relation.End.LEFT)
    public Collection<Dog> getDogs() {
        return dogs;
    }

    @Relation(cardinality = Cardinality.MANY_TO_MANY, linkTable = "cat_countries", linkedBy = {"cntId", "cat_id"}, end = Relation.End.LEFT)
    public Collection<Cat> getCats() {
        return cats;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCats(Collection<Cat> cats) {
        this.cats = cats;
    }

    public void setDogs(Collection<Dog> dogs) {
        this.dogs = dogs;
    }

    public void setName(String name) {
        this.name = name;
    }
}
