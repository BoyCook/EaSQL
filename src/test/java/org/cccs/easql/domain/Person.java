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
@Table(name = "Person")
public class Person {

    @Column(primaryKey = true)
    public long id;
    @Column
    public String name;
    @Column
    public String email;
    @Column
    public String phone;
    @Relation(cardinality = Cardinality.ONE_TO_MANY, key = "person_id")
    public Collection<Dog> dogs;
    @Relation(cardinality = Cardinality.ONE_TO_MANY, key = "person_id")
    public Collection<Cat> cats;

    public Person() {}

    public Person(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Person(long id, String name, String email, String phone) {
        this.id = id;
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
}
