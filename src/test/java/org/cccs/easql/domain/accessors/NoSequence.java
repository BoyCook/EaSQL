package org.cccs.easql.domain.accessors;

import org.cccs.easql.domain.Country;
import org.cccs.easql.domain.Person;

import javax.persistence.*;
import java.util.Collection;

/**
 * User: boycook
 * Date: 13/09/2011
 * Time: 15:37
 */
public class NoSequence {

    private long id;
    private String name;
    private Person owner;
    private Collection<Country> countries;

    public NoSequence(String name) {
        this.name = name;
    }

    @Id
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne
    @Column(name = "person_id")
    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    @ManyToMany
    @JoinTable(name = "cat_countries", joinColumns = {@JoinColumn(name = "cat_id")}, inverseJoinColumns = @JoinColumn(name = "cntId"))
    public Collection<Country> getCountries() {
        return countries;
    }

    public void setCountries(Collection<Country> countries) {
        this.countries = countries;
    }
}
