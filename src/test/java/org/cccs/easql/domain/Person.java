package org.cccs.easql.domain;

import javax.persistence.*;
import java.util.Collection;

/**
 * User: boycook
 * Date: 30/06/2011
 * Time: 17:08
 */
@Table(name = "Person")
public class Person {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "person_seq")
    public long id;
    @Column(nullable = false, unique = true)
    public String name;
    @Column
    public String email;
    @Column
    public String phone;
    @OneToMany
    @Column(name = "person_id")
    public Collection<Dog> dogs;
    @OneToMany
    @Column(name = "person_id")
    public Collection<Cat> cats;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        return id == person.id &&
                !(email != null ? !email.equals(person.email) : person.email != null) &&
                !(name != null ? !name.equals(person.name) : person.name != null) &&
                !(phone != null ? !phone.equals(person.phone) : person.phone != null);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        return result;
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
