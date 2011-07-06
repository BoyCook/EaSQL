package org.cccs.easql;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * User: boycook
 * Date: 17/06/2011
 * Time: 12:55
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    String name() default "";
    String sequence() default "";
    boolean mandatory() default false;
    boolean primaryKey() default false;
    boolean unique() default false;
}
