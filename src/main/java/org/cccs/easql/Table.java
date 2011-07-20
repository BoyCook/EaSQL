package org.cccs.easql;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * User: boycook
 * Date: 17/06/2011
 * Time: 12:54
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    String name() default "";
}
