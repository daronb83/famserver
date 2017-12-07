package server.generator;

import server.database.model.Person;
import server.database.model.Event;
import java.util.ArrayList;
import java.util.List;

import server.database.model.*;

/**
 * Represents a family personList of randomly generated people
 */
public class FamilyTreeGenerator extends DataGenerator{

    private List<Person> personList;
    private List<Event> eventList;
    private int generations;


    /**
     * Represents a family personList of randomly generated people
     *
     * @param descendant the root person
     * @param generations the number of generations to generate
     */
    public Person generateTree(Person descendant, int generations) {
        logger.info("Entering FamilyTreeGenerator for " + descendant.getDescendant());

        this.generations = generations;
        personList = new ArrayList<>();
        eventList = new ArrayList<>();

        addSpouse(descendant);
        fillGenerations(descendant, generations);

        logger.info("Finished generating persons and events");
        return descendant;
    }

    private void addSpouse(Person descendant) {
        PersonGenerator pGen = new PersonGenerator();
        String gender = "m";

        if (descendant.getGender().equals("m")) { // sets gender to the opposite of the primary user
            gender = "f";
        }

        Person spouse = pGen.getRandomPerson(descendant.getDescendant(), gender);

        descendant.setSpouse(spouse.getPersonID());  // cross assign spouse ids
        spouse.setSpouse(descendant.getPersonID());
        personList.add(spouse);
    }

    private void fillGenerations(Person root, int gensRemaining) {

        if (gensRemaining == 0) {  // return case
            return;
        }

        PersonGenerator pGen = new PersonGenerator();
        EventGenerator eGen = new EventGenerator();

        Person father = pGen.getRandomPerson(root.getDescendant(), "m", root.getLastName());
        eventList.addAll(eGen.getRandomEvents(father, generations - gensRemaining));
        personList.add(father);

        Person mother = pGen.getRandomPerson(root.getDescendant(), "f");
        eventList.addAll(eGen.getRandomEvents(mother, generations - gensRemaining));
        personList.add(mother);

        root.setFather(father.getPersonID());  // cross assign ids
        root.setMother(mother.getPersonID());
        father.setSpouse(mother.getPersonID());
        mother.setSpouse(father.getPersonID());

        fillGenerations(father, gensRemaining - 1);  // recursive
        fillGenerations(mother, gensRemaining - 1);
    }

    public List<Person> getPersonList() {
       return personList;
    }

    public List<Event> getEventList() {
        return eventList;
    }

}
