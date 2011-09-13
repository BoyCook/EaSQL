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
@Table(name = "Person")
public class Person {

    private long id;
    private String name;
    private String email;
    private String phone;
    private Collection<Dog> dogs;
    private Collection<Cat> cats;

    public Person() {
    }

    public Person(String name) {
        this.name = name;
    }

    public Person(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    @Column(primaryKey = true, sequence = "person_seq", name = "id")
    public long getId() {
        return id;
    }

    @Column(unique = true)
    public String getName() {
        return name;
    }

    @Column
    public String getEmail() {
        return email;
    }

    @Column
    public String getPhone() {
        return phone;
    }

    @Relation(cardinality = Cardinality.ONE_TO_MANY, key = "person_id")
    public Collection<Dog> getDogs() {
        return dogs;
    }

    @Relation(cardinality = Cardinality.ONE_TO_MANY, key = "person_id")
    public Collection<Cat> getCats() {
        return cats;
    }

    public void setCats(Collection<Cat> cats) {
        this.cats = cats;
    }

    public void setDogs(Collection<Dog> dogs) {
        this.dogs = dogs;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Person{" +
                "phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
