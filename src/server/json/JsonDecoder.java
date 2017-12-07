package server.json;

import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Logger;

/**
 * Decodes json strings into java objects
 */
public class JsonDecoder {
    
    private static Logger logger;

    static {
        logger = Logger.getLogger("famServer");
    }

    /**
     * Decodes a json string into a java objects
     *
     * @param jsonString the string of json to decode
     * @param object an empty model object to fill with data
     * @return the filled object
     */
    public Object decode(String jsonString, Object object) {
        logger.info("Decoding json string");
        Gson gson = new Gson();
        return gson.fromJson(jsonString, object.getClass());
    }

    /**
     * Decodes a json InputStream into a java object
     *
     * @param jsonStream the stream of json to decode
     * @param object an empty model object to fill with data
     * @return the filled object
     */
    public Object decodeStream(InputStream jsonStream, Object object) {
        logger.info("Decoding json stream");
        Gson gson = new Gson();
        Reader reader = new InputStreamReader(jsonStream);
        return gson.fromJson(reader, object.getClass());
    }

}
