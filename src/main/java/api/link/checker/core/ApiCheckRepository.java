package api.link.checker.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Repository for managing API status and metadata.
 * Handles initialization, updates, and persistence of API data.
 */
@Repository
public class ApiCheckRepository {
    private final Map<String, List<ApiInfo>> groupedApiMap = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String filePath;

    /**
     * Constructor initializes the file path and loads data from the file.
     *
     * @param filePath File path for saving and loading API data.
     */
    public ApiCheckRepository(@Value("${api.checker.storage.filepath:api-status.json}") String filePath) {
        this.filePath = filePath;
        loadFromFile(); // Load API data from file at startup.
    }

    /**
     * Initializes API data by merging new data and keeping existing states.
     *
     * @param groupedApis Map of grouped APIs with their metadata.
     */
    public void initializeApis(Map<String, List<ApiInfo>> groupedApis) {
        // Remove deleted groups
        Set<String> newGroupNames = groupedApis.keySet();
        groupedApiMap.keySet().removeIf(groupName -> !newGroupNames.contains(groupName));

        // Add or update APIs while maintaining existing check states
        groupedApis.forEach((groupName, apiList) -> {
            List<ApiInfo> existingApiList = groupedApiMap.getOrDefault(groupName, new ArrayList<>());

            Map<String, ApiInfo> existingApiMap = existingApiList.stream()
                    .collect(Collectors.toMap(
                            api -> api.getHttpMethod() + "_" + api.getPath(),
                            api -> api
                    ));

            List<ApiInfo> finalApiList = new ArrayList<>();

            for (ApiInfo api : apiList) {
                String key = api.getHttpMethod() + "_" + api.getPath();
                if (existingApiMap.containsKey(key)) {
                    api.setChecked(existingApiMap.get(key).isChecked()); // Preserve existing check state
                }
                finalApiList.add(api);
            }

            groupedApiMap.put(groupName, finalApiList);
        });

        saveToFile(); // Persist updated data
    }

    /**
     * Updates the check status of a specific API.
     *
     * @param httpMethod HTTP method of the API.
     * @param path       Path of the API.
     * @param checked    New check status.
     */
    public void updateCheckStatus(String httpMethod, String path, String nickname, boolean checked) {
        groupedApiMap.values().forEach(apiList -> {
            apiList.stream()
                    .filter(apiInfo ->
                            apiInfo.getPath().equals(path) &&
                                    apiInfo.getHttpMethod().equalsIgnoreCase(httpMethod)
                    )
                    .findFirst()
                    .ifPresent(apiInfo -> {
                        apiInfo.setNickname(checked ? nickname : "");
                        apiInfo.setChecked(checked);
                    });
        });

        saveToFile(); // Persist changes
    }

    /**
     * Returns all grouped APIs.
     *
     * @return Map of grouped APIs.
     */
    public Map<String, List<ApiInfo>> getAllGroupedApis() {
        return groupedApiMap;
    }

    /**
     * Loads API data from a file.
     */
    private void loadFromFile() {
        File file = new File(filePath);
        if (file.exists()) {
            try {
                Map<String, List<ApiInfo>> savedData = objectMapper.readValue(file, new TypeReference<>() {
                });
                groupedApiMap.putAll(savedData);
            } catch (IOException e) {
                System.err.println("Failed to load API data from file: " + e.getMessage());
            }
        }
    }

    /**
     * Saves API data to a file.
     */
    private void saveToFile() {
        try {
            objectMapper.writeValue(new File(filePath), groupedApiMap);
        } catch (IOException e) {
            System.err.println("Failed to save API data to file: " + e.getMessage());
        }
    }
}
