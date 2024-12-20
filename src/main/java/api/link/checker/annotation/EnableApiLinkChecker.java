package api.link.checker.annotation;

import api.link.checker.config.ApiLinkCheckerConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ApiLinkCheckerConfig.class)
public @interface EnableApiLinkChecker {
}