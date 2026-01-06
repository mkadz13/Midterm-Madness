package Main.data;

import Main.model.World;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 *Author: Makaato Serumaga
 * Utility class for loading {@link World} data from JSON resources.
 * All methods are static; this class is not meant to be instantiated.
 */
public class JsonWorldLoader {

    /**
     * Private constructor to prevent instantiation.
     */
    private JsonWorldLoader() {}

    /**
     * Loads a {@link World} from a JSON file on the classpath.
     *
     * @param resourcePath the classpath path of the JSON file
     *   
     * @return the deserialized {@link World} instance
     * @throws RuntimeException if the resource cannot be found or the JSON cannot be parsed
     */
    public static World loadWorld(String resourcePath) {
        try {
            InputStream in = JsonWorldLoader.class.getResourceAsStream(resourcePath);
            if (in == null) {
                throw new IllegalStateException("Could not find resource" + resourcePath);
            }

            Reader reader = new InputStreamReader(in);
            Gson gson = new GsonBuilder().create();
            World world = gson.fromJson(reader, World.class);

            return world;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load json", e);
        }
    }
}
