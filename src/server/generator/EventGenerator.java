package server.generator;

import server.database.model.Person;
import server.database.model.Event;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;

import server.database.model.*;
import server.generator.model.Location;
import server.generator.model.LocationList;
import server.json.JsonDecoder;

/**
 * Generates random events
 */
public class EventGenerator extends DataGenerator{

    private final static String PATH_TO_JSON_LOCATIONS = "json/locations.json";

    /**
     * Returns a complete set of random events for one person
     *
     * @param person the person to generate events for
     * @param generation the number of generations removed from the user
     * @return a complete set of random events for one person
     */
    public List<Event> getRandomEvents(Person person, int generation) {
        logger.info("getRandomEvents for " + person.getFirstName() + " " + person.getLastName());
        List<Event> output = new ArrayList<>();

        int birthYear = getBirthYear(generation);
        int deathYear = getDeathYear(birthYear);
        int marriageYear = getMarriageYear(birthYear, deathYear);

        // All users get these three events
        output.add(getRandomEvent(person, "Birth", birthYear));
        output.add(getRandomEvent(person, "Marriage", marriageYear));
        output.add(getRandomEvent(person, "Death", deathYear));

        // Additionally, we will generate a number of random events
        int minRandomEvents = 1;
        int maxRandomEvents = 5;
        int randomInt = randNum(minRandomEvents, maxRandomEvents);

        for (int i = 0; i < randomInt; i++) {
            String eventType = getRandomEventType();
            int year = randNum(birthYear, deathYear);
            output.add(getRandomEvent(person, eventType, year));
        }

        logger.info("Exiting getRandomEvents");
        return output;
    }

    private Event getRandomEvent(Person person, String eventType, int year) {
        String eventID = uniqueId();
        String personID = person.getPersonID();
        Location location = getRandomLocation();
        Event output = new Event(eventID, personID, person.getDescendant(), eventType, location.latitude,
                                 location.longitude, location.country, location.city, "" + year);

        logger.info("Creating " + eventType + " event for " + person.getFirstName() + " " + person.getLastName());
        return output;
    }

    private int getBirthYear(int generation) {
        int min = YEAR - GENERATION_LENGTH - (generation * GENERATION_LENGTH);
        int max = min + GENERATION_LENGTH;
        return randNum(min, max);
    }

    private int getDeathYear(int birthYear) {
        int min = birthYear + GENERATION_LENGTH + 1;
        int max = birthYear + (GENERATION_LENGTH * 6);
        return randNum(min, max);
    }

    private int getMarriageYear(int birthYear, int deathYear) {
        int min = birthYear + GENERATION_LENGTH;
        int max = deathYear - 1;
        return randNum(min, max);
    }

    private String getRandomEventType() {
        int min = 0;
        int max = 100;
        int randNum = randNum(min, max);

        if (randNum < 10) {
            return "Court Record";
        }
        else if (randNum < 20) {
            return "Baptism";
        }
        else if (randNum < 40) {
            return "Christening";
        }
        else {
            return "Census Record";
        }
    }

    private Location getRandomLocation() {
        String jsonLocations;

        try {
            jsonLocations = new String(Files.readAllBytes(Paths.get(PATH_TO_JSON_LOCATIONS)));
        }
        catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }

        JsonDecoder decoder = new JsonDecoder();
        LocationList locationList = (LocationList)decoder.decode(jsonLocations, new LocationList());
        int randomIndex = randNum(0, locationList.data.size() - 1);

        return locationList.data.get(randomIndex);
    }
}
