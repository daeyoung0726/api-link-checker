package api.link.checker.controller;

import api.link.checker.core.ApiCheckRepository;
import api.link.checker.core.ApiInfo;
import api.link.checker.core.SwaggerService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Hidden
@RestController
@RequestMapping("/v1/api/link/checker")
public class TrackedApiController {
    private final ApiCheckRepository apiCheckRepository;
    private final SwaggerService swaggerService;

    public TrackedApiController(ApiCheckRepository apiCheckRepository,
                                SwaggerService swaggerService) {
        this.apiCheckRepository = apiCheckRepository;
        this.swaggerService = swaggerService;
    }

    @GetMapping
    public Map<String, List<ApiInfo>> getAllGroupedApis() {
        return apiCheckRepository.getAllGroupedApis();
    }

    @GetMapping("/check")
    public void updateCheckStatus(
            @RequestParam("httpMethod") String httpMethod,
            @RequestParam("path") String path,
            @RequestParam("checked") boolean checked) {
        apiCheckRepository.updateCheckStatus(httpMethod, path, checked);
    }

    @GetMapping("/swagger-links")
    public Map<String, String> getSwaggerLinks() throws IOException {
        return swaggerService.fetchSwaggerLinks(apiCheckRepository.getAllGroupedApis());
    }
}