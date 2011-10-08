package org.cccs.easql.execution;

import org.cccs.easql.domain.Cat;
import org.cccs.easql.domain.Country;
import org.cccs.easql.domain.Dog;
import org.cccs.easql.domain.Person;
import org.cccs.easql.util.FieldSchema;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * User: boycook
 * Date: 17/06/2011
 * Time: 13:04
 */
public class TestSQLGeneratorForFields extends BaseSQLTest {

    private Person craig;
    private Dog lassie;
    private Cat bagpuss;

    @Before
    public void setup() throws IOException {
        FieldSchema.setup();
        Schema.packageName = "org.cccs.easql";
        craig = new Person("Craig", "craig@cook.com", "07234123456");
        craig.id = 1;
        lassie = new Dog("Lassie", craig);
        lassie.id = 1;
        bagpuss = new Cat("Bagpuss", craig);
        bagpuss.id = 1;
    }

    @Test
    public void sqlBuilderShouldWorkForInsertsForPerson() {
        sqlBuilderShouldWorkForInsertsForPerson(craig);
    }

    @Test
    public void sqlBuilderShouldWorkForInsertsForDog() {
        sqlBuilderShouldWorkForInsertsForDog(lassie);
    }

    @Test
    public void sqlBuilderShouldWorkForInsertsForCat() {
        sqlBuilderShouldWorkForInsertsForCat(bagpuss);
    }

    @Test
    public void sqlBuilderShouldWorkForSelectsForJustPerson() {
        sqlBuilderShouldWorkForSelectsForJustPerson(Person.class);
    }

    @Test
    public void sqlBuilderShouldWorkForJustDog() {
        sqlBuilderShouldWorkForJustDog(Dog.class);
    }

    @Test
    public void sqlBuilderShouldWorkForDogAndRelations() {
        sqlBuilderShouldWorkForDogAndRelations(Dog.class);
    }

    @Test
    public void sqlBuilderShouldWorkForJustCat() {
        sqlBuilderShouldWorkForJustCat(Cat.class);
    }

    @Test
    public void sqlBuilderShouldWorkForCatAndRelations() {
        sqlBuilderShouldWorkForCatAndRelations(Cat.class);
    }

    @Test
    public void sqlBuilderShouldWorkForSelectManyToManyFromLeft() throws NoSuchMethodException {
        sqlBuilderShouldWorkForSelectManyToManyFromLeft(Country.class, Dog.class);
    }

    @Test
    public void sqlBuilderShouldWorkForSelectManyToManyFromRight() throws NoSuchMethodException {
        sqlBuilderShouldWorkForSelectManyToManyFromRight(Dog.class, Country.class);
    }

    @Test
    public void sqlBuilderShouldWorkForUpdatesForPerson() {
        sqlBuilderShouldWorkForUpdatesForPerson(craig);
    }

    @Test
    public void sqlBuilderShouldWorkForUpdatesForDog() {
        sqlBuilderShouldWorkForUpdatesForDog(lassie);
    }

    @Test
    public void sqlBuilderShouldWorkForUpdatesForCat() {
        sqlBuilderShouldWorkForUpdatesForCat(bagpuss);
    }

    @Test
    public void sqlBuilderShouldWorkForDeletes() {
        sqlBuilderShouldWorkForDeletes(craig);
    }

    @Test
    public void sqlBuilderShouldWorkForCreatesForPerson() {
        sqlBuilderShouldWorkForCreatesForPerson(Person.class);
    }

    @Test
    public void sqlBuilderShouldWorkForCreatesForDog() {
        sqlBuilderShouldWorkForCreatesForDog(Dog.class);
    }

    @Test
    public void sqlBuilderShouldWorkForCreatesForCat() {
        sqlBuilderShouldWorkForCreatesForCat(Cat.class);
    }
}
