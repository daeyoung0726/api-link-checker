package api.link.checker.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SwaggerService {

    private static final String SWAGGER_URL = "http://localhost:8080/v3/api-docs";

    /**
     * Fetches Swagger links by matching API information with Swagger documentation.
     *
     * @param apiInfoMap A map containing API group information.
     * @return A map where the key is the HTTP method and path (e.g., "GET_/users"),
     *         and the value is the Swagger link (e.g., "[TagName]/operationId").
     */
    public Map<String, String> fetchSwaggerLinks(Map<String, List<ApiInfo>> apiInfoMap) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> swaggerLinks = new HashMap<>();

        try {
            // Fetch Swagger documentation from the configured URL.
            Map<String, Object> swaggerData = objectMapper.readValue(new URL(SWAGGER_URL), new TypeReference<>() {});

            // Extract API paths from Swagger data.
            Map<String, Map<String, Object>> paths = (Map<String, Map<String, Object>>) swaggerData.get("paths");

            for (Map.Entry<String, Map<String, Object>> pathEntry : paths.entrySet()) {
                String path = pathEntry.getKey();
                Map<String, Object> methods = pathEntry.getValue();

                for (Map.Entry<String, Object> methodEntry : methods.entrySet()) {
                    String httpMethod = methodEntry.getKey().toUpperCase();
                    Map<String, Object> methodDetails = (Map<String, Object>) methodEntry.getValue();

                    // Extract operationId and tags from Swagger method details.
                    String operationId = (String) methodDetails.get("operationId");
                    List<String> tags = (List<String>) methodDetails.get("tags");

                    /**
                     * Match Swagger API information with the local API information.
                     * This ensures that only managed APIs are linked to Swagger documentation.
                     */
                    for (Map.Entry<String, List<ApiInfo>> group : apiInfoMap.entrySet()) {
                        for (ApiInfo apiInfo : group.getValue()) {
                            if (apiInfo.getPath().equals(path) && apiInfo.getHttpMethod().equalsIgnoreCase(httpMethod)) {
                                // Construct Swagger path using the tag and operationId.
                                String tag = tags != null && !tags.isEmpty() ? tags.get(0) : "NoTag";
                                String swaggerPath = tag + "/" + operationId;

                                // Add the mapping to swaggerLinks.
                                swaggerLinks.put(apiInfo.getHttpMethod() + "_" + apiInfo.getPath(), swaggerPath);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            return Collections.emptyMap(); // Return an empty map on failure.
        }

        return swaggerLinks;
    }
}
