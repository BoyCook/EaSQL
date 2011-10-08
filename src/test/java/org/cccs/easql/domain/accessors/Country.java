package org.cccs.easql.domain.accessors;

import javax.persistence.*;
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

    public Country() {
    }

    public Country(String name) {
        this.name = name;
    }

    @Id
    @Column(name = "cntId")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cnt_seq")
    public long getId() {
        return id;
    }

    @Column(name = "name", nullable = false, unique = true)
    public String getName() {
        return name;
    }

    @ManyToMany
    @JoinTable(name = "dog_countries", joinColumns = {@JoinColumn(name = "cntId")}, inverseJoinColumns = @JoinColumn(name = "dog_id"))
    public Collection<Dog> getDogs() {
        return dogs;
    }

    @ManyToMany
    @JoinTable(name = "cat_countries", joinColumns = {@JoinColumn(name = "cntId")}, inverseJoinColumns = @JoinColumn(name = "cat_id"))
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
