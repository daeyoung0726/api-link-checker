package api.link.checker.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerExclusionConfig {

    static {
        System.setProperty("springdoc.paths-to-exclude", "/v1/api/link/checker/**");
    }
}