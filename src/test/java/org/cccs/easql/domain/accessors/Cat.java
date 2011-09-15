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
public class Cat {

    private long id;
    private String name;
    private Person owner;
    private Collection<Country> countries;

    public Cat() {
    }

    public Cat(final String name, final Person owner) {
        this.name = name;
        this.owner = owner;
    }

    @Column(primaryKey = true, sequence = "cat_seq", name = "id")
    public long getId() {
        return id;
    }

    @Column(unique = true, name = "name")
    public String getName() {
        return name;
    }

    @Relation(cardinality = Cardinality.MANY_TO_ONE, key = "person_id", name = "person2cat")
    public Person getOwner() {
        return owner;
    }

    @Relation(cardinality = Cardinality.MANY_TO_MANY, linkTable = "cat_countries", linkedBy = {"cntId", "cat_id"})
    public Collection<Country> getCountries() {
        return countries;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setOwner(final Person owner) {
        this.owner = owner;
    }

    public void setCountries(final Collection<Country> countries) {
        this.countries = countries;
    }

    @Override
    public String toString() {
        return "Cat{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
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
}
