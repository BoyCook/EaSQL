package org.cccs.easql.execution;

import org.cccs.easql.config.DataDrivenTestEnvironment;
import org.cccs.easql.domain.accessors.*;
import org.cccs.easql.validation.ValidationFailureException;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * User: boycook
 * Date: 20/09/2011
 * Time: 17:41
 */
public class TestServiceForMethods extends DataDrivenTestEnvironment {

    private Person craig;
    private Cat daisy;

    @Before
    public void before() throws EntityNotFoundException, ValidationFailureException {
        craig = finder.findByKey(Person.class, "Craig");
        daisy = finder.findByKey(Cat.class, "Daisy");

        assertThat(craig.getName(), is(equalTo("Craig")));
        assertThat(daisy.getName(), is(equalTo("Daisy")));
        assertThat(craig.getCats().size(), is(equalTo(1)));
        assertThat(craig.getDogs().size(), is(equalTo(1)));
    }

    //Create assertions
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

    //Update assertions
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

    //Delete assertions
    @Test(expected = EntityNotFoundException.class)
    public void deleteShouldWorkWithOne2ManyRelations() throws ValidationFailureException, EntityNotFoundException {
        final Person craig = finder.findByKey(Person.class, "Craig");
        assertThat(craig.getCats().size(), is(equalTo(1)));
        assertThat(craig.getDogs().size(), is(equalTo(1)));
        service.delete(craig);
        //Hasn't deleted relations
        for (Cat cat : craig.getCats()) {
            assertNotNull(finder.findById(Cat.class, cat.getId()));
        }
        for (Dog dog: craig.getDogs()) {
            assertNotNull(finder.findById(Dog.class, dog.getId()));
        }
        finder.findByKey(Person.class, "Craig");
    }

    @Test(expected = EntityNotFoundException.class)
    public void deleteShouldWorkWithMany2OneRelations() throws ValidationFailureException, EntityNotFoundException {
        final Cat daisy = finder.findByKey(Cat.class, "Daisy");
        assertThat(daisy.getCountries().size(), is(equalTo(2)));
        assertNotNull(daisy.getOwner());
        service.delete(daisy);
        //Hasn't deleted relations
        assertNotNull(finder.findById(Person.class, daisy.getOwner().getId()));
        for (Country country : daisy.getCountries()) {
            assertNotNull(finder.findById(Country.class, country.getId()));
        }
        finder.findByKey(Cat.class, "Daisy");
    }

    @Test(expected = EntityNotFoundException.class)
    public void deleteShouldWorkWithMany2ManyRelations() throws ValidationFailureException, EntityNotFoundException {
        final Country england = finder.findByKey(Country.class, "England");
        assertThat(england.getCats().size(), is(equalTo(2)));
        assertThat(england.getDogs().size(), is(equalTo(1)));
        service.delete(england);
        //Hasn't deleted relations
        for (Cat cat : england.getCats()) {
            assertNotNull(finder.findById(Cat.class, cat.getId()));
        }
        for (Dog dog: england.getDogs()) {
            assertNotNull(finder.findById(Dog.class, dog.getId()));
        }
        finder.findByKey(Country.class, "England");
    }

    //Validation assertions
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
    public void validatorShouldFailForMissingPrimaryKeyOnCreate() throws ValidationFailureException {
        final NoSequence o = new NoSequence("Foo");
        service.insert(o);
    }

    @Test(expected = ValidationFailureException.class)
    public void validatorShouldFailForMissingPrimaryKeyOnUpdate() throws ValidationFailureException, EntityNotFoundException {
        final Cat bagpuss = new Cat("BagPuss", null);
        service.update(bagpuss);
    }

    @Test(expected = ValidationFailureException.class)
    public void validatorShouldFailForNoPrimaryKeyOnDelete() throws ValidationFailureException, EntityNotFoundException {
        final NoSequence o = new NoSequence("Foo");
        service.delete(o);
    }

    @Test(expected = ValidationFailureException.class)
    public void validatorShouldFailForMissingPrimaryKeyOnDelete() throws ValidationFailureException, EntityNotFoundException {
        final Cat bagpuss = new Cat("BagPuss", null);
        service.delete(bagpuss);
    }
}
