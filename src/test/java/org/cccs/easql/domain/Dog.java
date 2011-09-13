package org.cccs.easql.domain;

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

    @Column(primaryKey = true, sequence = "dog_seq")
    public long id;
    @Column(unique = true, mandatory = true)
    public String name;
    @Relation(cardinality = Cardinality.MANY_TO_ONE, key = "person_id", name = "person2dog")
    public Person owner;
    @Relation(cardinality = Cardinality.MANY_TO_MANY, linkTable = "dog_countries", linkedBy = {"cntId", "dog_id"}, end = Relation.End.RIGHT)
    public Collection<Country> countries;

    public Dog() {
    }

    public Dog(String name, Person owner) {
        this.name = name;
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dog dog = (Dog) o;
        return id == dog.id && name.equals(dog.name);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Dog{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
