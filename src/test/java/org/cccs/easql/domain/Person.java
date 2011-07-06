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

    public Person(long id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public Person() {}
}
