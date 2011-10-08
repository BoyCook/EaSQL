package org.cccs.easql.domain.accessors;

import javax.persistence.*;
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

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dog_seq")
    public long getId() {
        return id;
    }

    @Column(unique = true, name = "name")
    public String getName() {
        return name;
    }

    @ManyToOne
    @Column(name = "person_id")
    public Person getOwner() {
        return owner;
    }

    @ManyToMany
    @JoinTable(name = "dog_countries", joinColumns = {@JoinColumn(name = "dog_id")}, inverseJoinColumns = @JoinColumn(name = "cntId"))
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
}
