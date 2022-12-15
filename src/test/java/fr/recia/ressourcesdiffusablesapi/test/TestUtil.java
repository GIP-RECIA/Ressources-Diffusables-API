package fr.recia.ressourcesdiffusablesapi.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

/**
 * Utility class for testing REST controllers.
 */
public class TestUtil {

    /** MediaType for JSON UTF8 */
    public static final MediaType APPLICATION_JSON_UTF8 =
            new MediaType(
                    MediaType.APPLICATION_JSON.getType(),
                    MediaType.APPLICATION_JSON.getSubtype(),
                    StandardCharsets.UTF_8
            );

    /** MediaType for XML UTF8 */
    public static final MediaType APPLICATION_XML_UTF8 =
            new MediaType(
                    MediaType.APPLICATION_XML.getType(),
                    MediaType.APPLICATION_XML.getSubtype(),
                    StandardCharsets.UTF_8
            );

    /**
     * Convert an object to JSON byte array.
     *
     * @param object the object to convert
     * @return the JSON byte array
     */
    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }

    /**
     * Convert JSON byte array to an Object.
     *
     * @param jsonBytes the JSON byte array
     * @return object the object converted
     */
    public static Object convertJsonBytesToObject(byte[] jsonBytes, Class<Object> objectType) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.readValue(jsonBytes, objectType);
    }
}
