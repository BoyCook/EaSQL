package org.cccs.easql.execution;

import org.cccs.easql.config.DataDrivenTestEnvironment;
import org.cccs.easql.domain.accessors.Country;
import org.cccs.easql.domain.accessors.Cat;
import org.cccs.easql.domain.accessors.Person;
import org.cccs.easql.validation.ValidationFailureException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: boycook
 * Date: 20/09/2011
 * Time: 17:41
 */
public class TestServiceForMethods extends DataDrivenTestEnvironment {

    private Person craig;
    private Cat daisy;

    @Before
    public void before() throws EntityNotFoundException {
        craig = finder.findByKey(Person.class, "Craig");
        daisy = finder.findByKey(Cat.class, "Daisy");

        assertThat(craig.getName(), is(equalTo("Craig")));
        assertThat(daisy.getName(), is(equalTo("Daisy")));
        assertThat(craig.getCats().size(), is(equalTo(1)));
        assertThat(craig.getDogs().size(), is(equalTo(1)));
    }

    @Test
    public void createWithoutRelationsShouldWork() throws ValidationFailureException {
        service.insert(new Person("Dave"));
        service.insert(new Person("Bob"));
        service.insert(new Person("Jim"));
        service.insert(new Person("Steve"));
        service.insert(new Person("Greg"));
    }

    @Test
    public void createWithRelationsShouldWork() throws ValidationFailureException {
        Cat garfield = new Cat("garfield", craig);
        service.insert(garfield);
    }

    @Test
    public void updateShouldWork() throws EntityNotFoundException, ValidationFailureException {
        craig.setEmail("SomeNewEmail");
        service.update(craig);

        final Person updated = finder.findById(Person.class, 1);
        assertThat(updated.getCats().size(), is(equalTo(1)));
        assertThat(updated.getDogs().size(), is(equalTo(1)));
        assertThat(updated.getEmail(), is(equalTo("SomeNewEmail")));
    }

    @Test
    public void updateOne2ManyRelationsShouldWork() throws EntityNotFoundException, ValidationFailureException {
        craig.getDogs().clear();
        craig.getCats().clear();
        craig.getCats().add(daisy);
        Cat fluffy = new Cat("Fluffy", craig);
        craig.getCats().add(fluffy);
        service.update(craig);

        final Person updated = finder.findByKey(Person.class, "Craig");
        assertThat(updated.getCats().size(), is(equalTo(2)));
        assertThat(updated.getDogs().size(), is(equalTo(0)));

        final Cat fluffyDB = finder.findByKey(Cat.class, "Fluffy");
        assertThat(fluffyDB.getName(), is(equalTo("Fluffy")));
    }

    @Test
    public void updateMany2OneRelationsShouldWork() throws EntityNotFoundException, ValidationFailureException {
        final Person craig = finder.findByKey(Person.class, "Craig");
        final Person bob = finder.findByKey(Person.class, "Bob");
        final Cat bagpuss = finder.findByKey(Cat.class, "Bagpuss");
        assertThat(bagpuss.getOwner(), is(equalTo(craig)));
        assertThat(bagpuss.getOwner().getId(), is(equalTo(1l)));
        assertThat(bob.getId(), is(equalTo(2l)));

        bagpuss.setOwner(bob);
        service.update(bagpuss);

        final Cat updated = finder.findByKey(Cat.class, "Bagpuss");
        assertThat(updated.getCountries().size(), is(equalTo(2)));
        assertThat(updated.getOwner(), is(equalTo(bob)));
    }

    @Test
    public void updateMany2ManyRelationsShouldWork() throws EntityNotFoundException, ValidationFailureException {
        final Cat daisy = finder.findByKey(Cat.class, "Daisy");
        final Country england = finder.findByKey(Country.class, "England");
        final Country ireland = finder.findByKey(Country.class, "Ireland");
        final Country wales = finder.findByKey(Country.class, "Wales");

        assertThat(daisy.getCountries().size(), is(equalTo(2)));
        assertTrue(daisy.getCountries().contains(england));
        assertTrue(daisy.getCountries().contains(ireland));
        assertTrue(england.getCats().contains(daisy));
        assertTrue(ireland.getCats().contains(daisy));

        daisy.getCountries().remove(ireland);
        daisy.getCountries().add(wales);
        daisy.getCountries().add(new Country("Spain"));
        service.update(daisy);

        final Cat updatedDaisy = finder.findByKey(Cat.class, "Daisy");
        final Country updatedEngland = finder.findByKey(Country.class, "England");
        final Country updatedIreland = finder.findByKey(Country.class, "Ireland");
        final Country updatedWales = finder.findByKey(Country.class, "Wales");
        final Country spain = finder.findByKey(Country.class, "Spain");

        assertThat(updatedDaisy.getCountries().size(), is(equalTo(3)));
        assertTrue(updatedDaisy.getCountries().contains(updatedEngland));
        assertTrue(updatedDaisy.getCountries().contains(spain));
        assertFalse(updatedDaisy.getCountries().contains(updatedIreland));
        assertTrue(updatedDaisy.getCountries().contains(updatedWales));
        assertTrue(updatedEngland.getCats().contains(updatedDaisy));
        assertFalse(updatedIreland.getCats().contains(updatedDaisy));
        assertTrue(updatedWales.getCats().contains(updatedDaisy));
        assertTrue(spain.getCats().contains(updatedDaisy));
    }

    @Ignore
    @Test(expected = ValidationFailureException.class)
    public void validatorShouldFailForMissingMandatoryFieldOnCreate() throws ValidationFailureException {
        final Cat bagpuss = new Cat();
        service.insert(bagpuss);
    }

    @Test(expected = ValidationFailureException.class)
    public void validatorShouldFailForMissingMandatoryFieldOnUpdate() throws ValidationFailureException, EntityNotFoundException {
        final Cat bagpuss = new Cat();
        service.update(bagpuss);
    }

    @Test(expected = ValidationFailureException.class)
    public void validatorShouldFailForMissingPrimaryKeyOnUpdate() throws ValidationFailureException, EntityNotFoundException {
        final Cat bagpuss = new Cat("BagPuss", null);
        service.update(bagpuss);
    }
}
