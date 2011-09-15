package org.cccs.easql.domain.accessors;

import org.cccs.easql.Cardinality;
import org.cccs.easql.Column;
import org.cccs.easql.Relation;
import org.cccs.easql.Table;

import java.util.Collection;

/**
 * User: boycook
 * Date: 30/06/2011
 * Time: 17:08
 */
@Table
public class Dog {

    private long id;
    private String name;
    private Person owner;
    private Collection<Country> countries;

    public Dog() {
    }

    public Dog(String name, Person owner) {
        this.name = name;
        this.owner = owner;
    }

    @Column(primaryKey = true, sequence = "dog_seq", name = "id")
    public long getId() {
        return id;
    }

    @Column(unique = true, name = "name")
    public String getName() {
        return name;
    }

    @Relation(cardinality = Cardinality.MANY_TO_ONE, key = "person_id", name = "person2dog")
    public Person getOwner() {
        return owner;
    }

    @Relation(cardinality = Cardinality.MANY_TO_MANY, linkTable = "dog_countries", linkedBy = {"cntId", "dog_id"}, end = Relation.End.RIGHT)
    public Collection<Country> getCountries() {
        return countries;
    }

    public void setCountries(Collection<Country> countries) {
        this.countries = countries;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "Dog{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
