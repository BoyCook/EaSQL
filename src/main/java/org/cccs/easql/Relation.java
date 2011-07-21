package org.cccs.easql;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * User: boycook
 * Date: 04/07/2011
 * Time: 14:14
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Relation {
    String name() default "";
    String linkTable() default "";
    String[] linkedBy() default {};
    String key() default "";
    Cardinality cardinality() default Cardinality.ONE_TO_ONE;
}
