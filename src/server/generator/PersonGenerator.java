package server.generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

import server.database.model.Person;
import server.generator.model.NameList;
import server.json.JsonDecoder;

/**
 * Generates random person objects
 */
class PersonGenerator extends DataGenerator {

    private static final String PATH_TO_BOY_NAMES = "json/mnames.json";
    private static final String PATH_TO_GIRL_NAMES = "json/fnames.json";
    private static final String PATH_TO_LAST_NAMES = "json/snames.json";

    /**
     * Generates a Person object with a random name
     *
     * @param gender true = female, false = male
     * @return the person object
     */
    Person getRandomPerson(String username, String gender) {
        return getRandomPerson(username, gender, getName(PATH_TO_LAST_NAMES));
    }

    /**
     * Generates a Person object with a random name
     *
     * @param gender true = female, false = male
     * @param lastName a given last name, which the person object will use
     * @return the person object
     */
    Person getRandomPerson(String username, String gender, String lastName) {
        logger.info("getRandomPerson: " + gender + ", " + username);
        assert gender.equals("f") || gender.equals("m") : "Invalid gender";
        String firstName;

        if (gender.equals("f")) {
            firstName = getName(PATH_TO_GIRL_NAMES);
        }
        else {
            firstName = getName(PATH_TO_BOY_NAMES);
        }

        logger.info("created Person: " + firstName + " " + lastName);
        return new Person(uniqueId(), username, firstName, lastName, gender);
    }

    private String getName(String path) {
        String jsonNames;

        try {
            jsonNames = new String(Files.readAllBytes(Paths.get(path)));
        }
        catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }

        JsonDecoder decoder = new JsonDecoder();
        NameList nameList = (NameList)decoder.decode(jsonNames, new NameList());
        int randomIndex = randNum(0, nameList.data.size() - 1);

        return nameList.data.get(randomIndex);
    }
}
