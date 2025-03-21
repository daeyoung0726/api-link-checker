package api.link.checker.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "api.link.checker.core",
        "api.link.checker.controller",
        "api.link.checker.config"
})
public class ApiLinkCheckerConfig {
}