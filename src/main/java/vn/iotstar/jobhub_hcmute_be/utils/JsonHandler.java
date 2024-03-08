package vn.iotstar.jobhub_hcmute_be.utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
public class JsonHandler {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static Logger logger = LoggerFactory.getLogger(JsonHandler.class);

    private JsonHandler() {
    }

    /**
     * @param objToJsonize the object to convert to json
     * @return JSON string representation
     */
    public static String getJsonString(Object objToJsonize) {
        String jsonString = "";
        try {
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(objToJsonize);
        } catch (JsonProcessingException e) {
            logger.error("encountered json processing exception while reading the json object");
        }

        return jsonString;
    }

    /**
     * @param jsonFileToLoad file containing the json
     * @param clazz          Entity class which will hold the json represented data
     * @param <T>
     * @return an object of type T
     * @throws IOException
     */
    public static <T> T createObjectFromJson(File jsonFileToLoad, Class<T> clazz) throws IOException {
        return mapper.readValue(jsonFileToLoad, clazz);
    }

    /**
     * The method returns a list of entity objects from a json array.
     * @param jsonFileToLoad    File containing the json array
     * @param clazz             Entity class which will hold the json represented data
     * @param <T>
     * @return List of objects of type T
     * @throws IOException
     */
    public static <T> List<T> createObjectsFromJson(File jsonFileToLoad, Class<T> clazz) throws IOException {
        CollectionType objectsType = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
        return mapper.readValue(jsonFileToLoad, objectsType);
    }

    /**
     * mapper configurations are handled here to allow for the Enums to be converted to string during read/write.
     * @return ObjectMapper
     */
    public static ObjectMapper getObjectMapperForEnums() {
        mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        return mapper;
    }
}
