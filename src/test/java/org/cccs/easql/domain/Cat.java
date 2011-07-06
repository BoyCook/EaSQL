package org.cccs.easql.domain;

import org.cccs.easql.Cardinality;
import org.cccs.easql.Column;
import org.cccs.easql.Relation;

/**
 * User: boycook
 * Date: 30/06/2011
 * Time: 17:08
 */
public class Cat {

    @Column(primaryKey = true)
    public long id;
    @Column
    public String name;
    @Relation(cardinality = Cardinality.MANY_TO_ONE, key="person_id", name = "person2cat")
    public Person owner;

    public Cat(long id, String name, Person owner) {
        this.id = id;
        this.name = name;
        this.owner = owner;
    }

    public Cat() {}

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
}
