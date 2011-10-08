package org.cccs.easql.domain;

import javax.persistence.*;
import java.util.Collection;

/**
 * User: boycook
 * Date: 13/09/2011
 * Time: 15:37
 */
public class NoSequence {

    @Id
    @Column
    public long id;
    @Column
    public String name;
    @ManyToOne
    @Column(name = "person_id")
    public Person owner;
    @ManyToMany
    @JoinTable(name = "cat_countries", joinColumns = {@JoinColumn(name = "cat_id")}, inverseJoinColumns = @JoinColumn(name = "cntId"))
    public Collection<Country> countries;

    public NoSequence(String name) {
        this.name = name;
    }
}
