package api.link.checker.core;

import api.link.checker.annotation.ApiGroup;
import api.link.checker.annotation.TrackApi;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Scans APIs annotated with @ApiGroup and @TrackApi.
 * Updates the repository with newly scanned API data.
 */
@Component
public class ApiScanner implements ApplicationListener<ContextRefreshedEvent> {
    private final ApiCheckRepository apiCheckRepository;

    /**
     * Constructor with dependency injection.
     *
     * @param apiCheckRepository Repository for storing API metadata.
     */
    public ApiScanner(ApiCheckRepository apiCheckRepository) {
        this.apiCheckRepository = apiCheckRepository;
    }

    /**
     * Event listener for context refresh, triggers API scanning.
     *
     * @param event Context refreshed event.
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Map<String, List<ApiInfo>> scannedApis = new HashMap<>();

        // Retrieve all beans annotated with @ApiGroup
        Map<String, Object> beans = event.getApplicationContext().getBeansWithAnnotation(ApiGroup.class);

        for (Object bean : beans.values()) {
            Class<?> beanClass = AopUtils.getTargetClass(bean);
            ApiGroup apiGroup = AnnotationUtils.findAnnotation(beanClass, ApiGroup.class);

            if (apiGroup == null) continue;

            String groupName = apiGroup.value();
            List<ApiInfo> apiInfos = new ArrayList<>();

            for (Method method : beanClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(TrackApi.class)) {
                    TrackApi trackApi = method.getAnnotation(TrackApi.class);
                    String httpMethod = findHttpMethod(method);
                    String path = findPath(beanClass, method);

                    apiInfos.add(new ApiInfo(httpMethod, path, trackApi.description(), false));
                }
            }
            scannedApis.put(groupName, apiInfos);
        }

        // Update repository with scanned APIs
        updateRepositoryWithScannedApis(scannedApis);
    }

    /**
     * Updates the repository with scanned API data.
     *
     * @param scannedApis Newly scanned API data.
     */
    private void updateRepositoryWithScannedApis(Map<String, List<ApiInfo>> scannedApis) {
        Map<String, List<ApiInfo>> existingApis = apiCheckRepository.getAllGroupedApis();

        Map<String, List<ApiInfo>> updatedApis = new HashMap<>();

        // Map of existing API states
        Map<String, ApiInfo> existingApiMap = existingApis.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toMap(
                        api -> api.getHttpMethod() + "_" + api.getPath(),
                        api -> api
                ));

        // Merge new data with existing data
        scannedApis.forEach((groupName, scannedApiList) -> {
            List<ApiInfo> finalApiList = new ArrayList<>();

            for (ApiInfo scannedApi : scannedApiList) {
                String key = scannedApi.getHttpMethod() + "_" + scannedApi.getPath();

                // Preserve check state if already exists
                if (existingApiMap.containsKey(key)) {
                    scannedApi.setChecked(existingApiMap.get(key).isChecked());
                }

                finalApiList.add(scannedApi);
            }

            updatedApis.put(groupName, finalApiList);
        });

        // Save updated data to repository
        apiCheckRepository.initializeApis(updatedApis);
    }

    /**
     * Detects HTTP method for a given method.
     *
     * @param method The method to analyze.
     * @return The HTTP method as a string.
     */
    private String findHttpMethod(Method method) {
        if (method.isAnnotationPresent(GetMapping.class)) return "GET";
        if (method.isAnnotationPresent(PostMapping.class)) return "POST";
        if (method.isAnnotationPresent(DeleteMapping.class)) return "DELETE";
        if (method.isAnnotationPresent(PutMapping.class)) return "PUT";
        if (method.isAnnotationPresent(PatchMapping.class)) return "PATCH";
        return "UNKNOWN";
    }

    /**
     * Retrieves the full path of an API method.
     *
     * @param beanClass The class containing the API.
     * @param method    The method to analyze.
     * @return The full API path as a string.
     */
    private String findPath(Class<?> beanClass, Method method) {
        RequestMapping classMapping = AnnotationUtils.findAnnotation(beanClass, RequestMapping.class);
        String basePath = (classMapping != null && classMapping.value().length > 0)
                ? classMapping.value()[0]
                : "";

        if (method.isAnnotationPresent(GetMapping.class)) {
            return combinePath(basePath, method.getAnnotation(GetMapping.class).value());
        }

        if (method.isAnnotationPresent(PostMapping.class)) {
            return combinePath(basePath, method.getAnnotation(PostMapping.class).value());
        }

        if (method.isAnnotationPresent(DeleteMapping.class)) {
            return combinePath(basePath, method.getAnnotation(DeleteMapping.class).value());
        }

        if (method.isAnnotationPresent(PutMapping.class)) {
            return combinePath(basePath, method.getAnnotation(PutMapping.class).value());
        }

        if (method.isAnnotationPresent(PatchMapping.class)) {
            return combinePath(basePath, method.getAnnotation(PatchMapping.class).value());
        }

        return basePath;
    }

    /**
     * Combines the base path and method-specific paths.
     *
     * @param basePath The base path.
     * @param paths    The method-specific paths.
     * @return The combined path as a string.
     */
    private String combinePath(String basePath, String[] paths) {
        return basePath + (paths.length > 0 ? paths[0] : "");
    }
}