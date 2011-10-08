package org.cccs.easql.domain;

import javax.persistence.*;
import java.util.Collection;

/**
 * User: boycook
 * Date: 12/07/2011
 * Time: 15:53
 */
@Table(name = "countries")
public class Country {

    @Id
    @Column(name = "cntId")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cnt_seq")
    public long id;
    @Column(nullable = false, unique = true)
    public String name;
    @ManyToMany
    @JoinTable(name = "dog_countries", joinColumns = {@JoinColumn(name = "cntId")}, inverseJoinColumns = @JoinColumn(name = "dog_id"))
    public Collection<Dog> dogs;
    @ManyToMany
    @JoinTable(name = "cat_countries", joinColumns = {@JoinColumn(name = "cntId")}, inverseJoinColumns = @JoinColumn(name = "cat_id"))
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
