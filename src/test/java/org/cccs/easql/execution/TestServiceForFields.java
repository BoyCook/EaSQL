package org.cccs.easql.execution;

import org.cccs.easql.config.DataDrivenTestEnvironment;
import org.cccs.easql.domain.*;
import org.cccs.easql.validation.ValidationFailureException;
import org.junit.Test;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * User: boycook
 * Date: 07/07/2011
 * Time: 10:25
 */
public class TestServiceForFields extends DataDrivenTestEnvironment {

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
    public void createWithRelationsShouldWork() throws ValidationFailureException, EntityNotFoundException {
        final Person craig = finder.findByKey(Person.class, "Craig");
        final Cat garfield = new Cat("garfield", craig);
        service.insert(garfield);
    }

    //Update assertions
    @Test
    public void updateShouldWork() throws EntityNotFoundException, ValidationFailureException {
        final Person craig = finder.findByKey(Person.class, "Craig");
        craig.email = "SomeNewEmail";
        service.update(craig);

        final Person updated = finder.findById(Person.class, 1);
        assertThat(updated.cats.size(), is(equalTo(1)));
        assertThat(updated.dogs.size(), is(equalTo(1)));
        assertThat(updated.email, is(equalTo("SomeNewEmail")));
    }

    @Test
    public void updateOne2ManyRelationsShouldWork() throws EntityNotFoundException, ValidationFailureException {
        final Person craigOriginal = finder.findByKey(Person.class, "Craig");
        final Person craig = finder.findByKey(Person.class, "Craig");
        final Cat daisy = finder.findByKey(Cat.class, "Daisy");
        craig.dogs.clear();
        craig.cats.clear();
        craig.cats.add(daisy);
        final Cat fluffy = new Cat("Fluffy", craig);
        craig.cats.add(fluffy);
        service.update(craig);

        assertCats(craigOriginal.cats);
        assertNotDogs(craigOriginal.dogs);

        final Person updated = finder.findByKey(Person.class, "Craig");
        assertThat(updated.cats.size(), is(equalTo(2)));
        assertThat(updated.dogs.size(), is(equalTo(0)));

        final Cat fluffyDB = finder.findByKey(Cat.class, "Fluffy");
        assertThat(fluffyDB.name, is(equalTo("Fluffy")));
    }

    @Test
    public void updateMany2OneRelationsShouldWork() throws EntityNotFoundException, ValidationFailureException {
        final Person craig = finder.findByKey(Person.class, "Craig");
        final Person bob = finder.findByKey(Person.class, "Bob");
        final Cat bagpuss = finder.findByKey(Cat.class, "Bagpuss");
        assertThat(bagpuss.owner, is(equalTo(craig)));
        assertThat(bagpuss.owner.id, is(equalTo(1l)));
        assertThat(bob.id, is(equalTo(2l)));

        bagpuss.owner = bob;
        service.update(bagpuss);

        final Cat updated = finder.findByKey(Cat.class, "Bagpuss");
        assertThat(updated.countries.size(), is(equalTo(2)));
        assertThat(updated.owner, is(equalTo(bob)));
    }

    @Test
    public void updateMany2ManyRelationsShouldWork() throws EntityNotFoundException, ValidationFailureException {
        final Cat daisy = finder.findByKey(Cat.class, "Daisy");
        final Country england = finder.findByKey(Country.class, "England");
        final Country ireland = finder.findByKey(Country.class, "Ireland");
        final Country wales = finder.findByKey(Country.class, "Wales");

        assertThat(daisy.countries.size(), is(equalTo(2)));
        assertTrue(daisy.countries.contains(england));
        assertTrue(daisy.countries.contains(ireland));
        assertTrue(england.cats.contains(daisy));
        assertTrue(ireland.cats.contains(daisy));

        daisy.countries.remove(ireland);
        daisy.countries.add(wales);
        daisy.countries.add(new Country("Spain"));
        service.update(daisy);

        final Cat updatedDaisy = finder.findByKey(Cat.class, "Daisy");
        final Country updatedEngland = finder.findByKey(Country.class, "England");
        final Country updatedIreland = finder.findByKey(Country.class, "Ireland");
        final Country updatedWales = finder.findByKey(Country.class, "Wales");
        final Country spain = finder.findByKey(Country.class, "Spain");

        assertThat(updatedDaisy.countries.size(), is(equalTo(3)));
        assertTrue(updatedDaisy.countries.contains(updatedEngland));
        assertTrue(updatedDaisy.countries.contains(spain));
        assertFalse(updatedDaisy.countries.contains(updatedIreland));
        assertTrue(updatedDaisy.countries.contains(updatedWales));
        assertTrue(updatedEngland.cats.contains(updatedDaisy));
        assertFalse(updatedIreland.cats.contains(updatedDaisy));
        assertTrue(updatedWales.cats.contains(updatedDaisy));
        assertTrue(spain.cats.contains(updatedDaisy));
    }

    //Delete assertions
    @Test(expected = EntityNotFoundException.class)
    public void deleteShouldWorkWithOne2ManyRelations() throws ValidationFailureException, EntityNotFoundException {
        final Person craig = finder.findByKey(Person.class, "Craig");
        assertThat(craig.cats.size(), is(equalTo(1)));
        assertThat(craig.dogs.size(), is(equalTo(1)));
        service.delete(craig);
        //Hasn't deleted relations
        assertCats(craig.cats);
        assertDogs(craig.dogs);
        finder.findByKey(Person.class, "Craig");
    }

    @Test(expected = EntityNotFoundException.class)
    public void deleteShouldWorkWithMany2OneRelations() throws ValidationFailureException, EntityNotFoundException {
        final Cat daisy = finder.findByKey(Cat.class, "Daisy");
        assertThat(daisy.countries.size(), is(equalTo(2)));
        assertNotNull(daisy.owner);
        service.delete(daisy);
        //Hasn't deleted relations
        assertNotNull(finder.findById(Person.class, daisy.owner.id));
        assertCountries(daisy.countries);
        finder.findByKey(Cat.class, "Daisy");
    }

    @Test(expected = EntityNotFoundException.class)
    public void deleteShouldWorkWithMany2ManyRelations() throws ValidationFailureException, EntityNotFoundException {
        final Country england = finder.findByKey(Country.class, "England");
        assertThat(england.cats.size(), is(equalTo(2)));
        assertThat(england.dogs.size(), is(equalTo(1)));
        service.delete(england);
        //Hasn't deleted relations
        assertCats(england.cats);
        assertDogs(england.dogs);
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

    private void assertCats(Collection<Cat> cats) throws ValidationFailureException, EntityNotFoundException {
        for (Cat cat : cats) {
            assertNotNull(finder.findById(Cat.class, cat.id));
        }
    }

    private void assertDogs(Collection<Dog> dogs) throws ValidationFailureException, EntityNotFoundException {
        for (Dog dog: dogs) {
            assertNotNull(finder.findById(Dog.class, dog.id));
        }
    }

    private void assertNotDogs(Collection<Dog> dogs) throws ValidationFailureException {
        for (Dog dog: dogs) {
            try {
                assertNull(finder.findById(Dog.class, dog.id));
            } catch (EntityNotFoundException e) {
                //This is expected
            } catch (ValidationFailureException e) {
                throw e;
            }
        }
    }

    private void assertCountries(Collection<Country> countries) throws ValidationFailureException, EntityNotFoundException {
        for (Country country : countries) {
            assertNotNull(finder.findById(Country.class, country.id));
        }
    }
}
