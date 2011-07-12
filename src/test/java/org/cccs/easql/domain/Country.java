package org.cccs.easql.domain;

import org.cccs.easql.Column;
import org.cccs.easql.Table;

/**
 * User: boycook
 * Date: 12/07/2011
 * Time: 15:53
 */
@Table(name = "countries")
public class Country {

    @Column(primaryKey = true, name = "cntId")
    public long id;

}
