package org.cccs.easql.domain;

import javax.persistence.*;
import java.util.Collection;

/**
 * User: boycook
 * Date: 30/06/2011
 * Time: 17:08
 */
@Table
public class Cat {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cat_seq")
    public long id;
    @Column(nullable = false, unique = true)
    public String name;
    @ManyToOne
    @Column(name = "person_id")
    public Person owner;
    @ManyToMany
    @JoinTable(name = "cat_countries", joinColumns = {@JoinColumn(name = "cat_id")}, inverseJoinColumns = @JoinColumn(name = "cntId"))
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
