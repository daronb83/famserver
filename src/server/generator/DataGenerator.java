package server.generator;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

/**
 * Generates random data for services
 */
abstract class DataGenerator {

    /* STATIC */
    protected static Logger logger;
    static final int YEAR = 2017;
    static final int GENERATION_LENGTH = 18;

    static {
        logger = Logger.getLogger("famServer");
    }


    /* NON-STATIC */
    int randNum(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    String uniqueId() {
        return UUID.randomUUID().toString();
    }

}
