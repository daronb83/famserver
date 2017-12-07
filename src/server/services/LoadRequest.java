package server.services;

import java.util.List;

import server.database.model.Event;
import server.database.model.Person;
import server.database.model.User;

/**
 * Models a JSON request for the LoadService
 */
public class LoadRequest implements Request {

    private List<User> users;
    private List<Person> persons;
    private List<Event> events;

    /**
     * Models a JSON request for the LoadService
     *
     * @param users a list of User objects to load
     * @param persons a list of Person objects to load
     * @param events a list of Event objects to load
     */
    public LoadRequest(List<User> users, List<Person> persons, List<Event> events) {
        this.users = users;
        this.persons = persons;
        this.events = events;
    }

    public LoadRequest(){}

    public List<User> getUsers() {
        return users;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public List<Event> getEvents() {
        return events;
    }
}
