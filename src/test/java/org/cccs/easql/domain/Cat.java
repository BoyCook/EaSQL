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
public class Cat {

    @Column(primaryKey = true, sequence = "cat_seq")
    public long id;
    @Column(unique = true, mandatory = true)
    public String name;
    @Relation(cardinality = Cardinality.MANY_TO_ONE, key = "person_id", name = "person2cat")
    public Person owner;
    @Relation(cardinality = Cardinality.MANY_TO_MANY, linkTable = "cat_countries", linkedBy = {"cntId", "cat_id"})
    public Collection<Country> countries;

    public Cat() {
    }

    public Cat(final String name, final Person owner) {
        this.name = name;
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cat cat = (Cat) o;
        return id == cat.id && name.equals(cat.name);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Cat{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
