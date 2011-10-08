package org.cccs.easql.util;

import org.apache.commons.beanutils.PropertyUtils;
import org.cccs.easql.domain.Person;
import org.cccs.easql.domain.accessors.Cat;
import org.junit.Test;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import static org.cccs.easql.util.ClassUtils.isSelectableColumn;
import static org.cccs.easql.util.ClassUtils.stripName;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: boycook
 * Date: 07/10/2011
 * Time: 16:01
 */
public class TestClassUtils {

    @Test
    public void isSelectableMethodShouldWork() throws NoSuchFieldException {
        Field id = Person.class.getField("id");
        Field name = Person.class.getField("name");
        Field email = Person.class.getField("email");
        Field phone = Person.class.getField("phone");
        Field cats = Person.class.getField("cats");
        Field dogs = Person.class.getField("dogs");

        assertFalse(isSelectableColumn(cats));
        assertFalse(isSelectableColumn(dogs));
        assertFalse(isSelectableColumn(id));
        assertTrue(isSelectableColumn(name));
        assertTrue(isSelectableColumn(email));
        assertTrue(isSelectableColumn(phone));
    }

    @Test
    public void stripNameShouldWork() {
        assertThat(stripName("getName"), is(equalTo("name")));
        assertThat(stripName("isName"), is(equalTo("name")));
        assertThat(stripName("hasName"), is(equalTo("name")));
        assertThat(stripName("getFooBar"), is(equalTo("fooBar")));
        assertThat(stripName("fooBarName"), is(equalTo("fooBarName")));
    }

    @Test
    public void getPropertyDescriptorShouldWork() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        getProperty(new Cat(), "name");
    }

    private void getProperty(final Object object, final String name) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        final PropertyDescriptor property = PropertyUtils.getPropertyDescriptor(object, name);
        System.out.println("DisplayName: " + property.getDisplayName());
        System.out.println("Type: " + property.getPropertyType().getName());
        System.out.println("Getter: " + property.getReadMethod().getName());
        System.out.println("Setter: " + property.getWriteMethod().getName());
    }
}
