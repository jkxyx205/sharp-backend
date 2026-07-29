package com.rick.backend.module.common.servlet;

import com.rick.backend.module.common.model.ApiVersion;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

@Configuration
public class WebMvcRegistrationsConfig implements WebMvcRegistrations {
    private static final String VERSION_PARAM_NAME = "version";
    private static final String HEADER_VERSION = "X-VERSION";

    /**
     * RequestMappingHandlerMapping 被 VersionRequestMappingHandlerMappingHandlerMapping 替换
     * 按照VersionRequestMappingHandlerMappingHandlerMapping映射逻辑进行映射
     *
     * @return
     */
    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new VersionRequestMappingHandlerMapping();
    }

    private class VersionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
        @Override
        protected RequestCondition<?> getCustomMethodCondition(Method method) {
            ApiVersion apiVersion = AnnotationUtils.findAnnotation(method, ApiVersion.class);
            if (apiVersion == null) {
                return null;
            }
            return new RequestCondition() {
                @Override
                public Object combine(Object o) {
                    return null;
                }

                @Override
                public Object getMatchingCondition(HttpServletRequest request) {
                    String version = resolveVersion(method, request);
                    if (Objects.equals(version, apiVersion.value())) {
                        return this;
                    }
                    return null;
                }

                @Override
                public int compareTo(Object o, HttpServletRequest request) {
                    return 0;
                }
            };
        }

        private String resolveVersion(Method method, HttpServletRequest request) {
            String version = request.getParameter(VERSION_PARAM_NAME);
            if (StringUtils.hasText(version)) {
                return version;
            }
            version = request.getHeader(HEADER_VERSION);
            if (StringUtils.hasText(version)) {
                return version;
            }
            return resolvePathVersion(method, request);
        }

        private String resolvePathVersion(Method method, HttpServletRequest request) {
            String bastPath = null;
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == RequestMapping.class) {
                    bastPath = ((RequestMapping) annotation).value()[0];
                    break;
                } else if (annotation.annotationType() == GetMapping.class) {
                    bastPath = ((GetMapping) annotation).value()[0];
                    break;
                } else if (annotation.annotationType() == PostMapping.class) {
                    bastPath = ((PostMapping) annotation).value()[0];
                    break;
                } else if (annotation.annotationType() == DeleteMapping.class) {
                    bastPath = ((DeleteMapping) annotation).value()[0];
                    break;
                } else if (annotation.annotationType() == PutMapping.class) {
                    bastPath = ((PutMapping) annotation).value()[0];
                    break;
                }
            }
            Map<String, String> uriVariables = getPathMatcher().extractUriTemplateVariables(bastPath.startsWith("/") ? bastPath : "/" + bastPath,
                    request.getServletPath());
            return uriVariables.get(VERSION_PARAM_NAME);
        }
    }
}