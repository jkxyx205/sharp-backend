package com.rick.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Rick.Xu
 * @date 2025/11/13 13:38
 */
@Configuration
public class SwaggerConfig {

    /**
     * http://localhost:9090/swagger-ui/index.html
     * @return
     */
    @Bean
    public GroupedOpenApi stockApi() {
        return GroupedOpenApi.builder()
                .displayName("Course API")
                .group("course-api")                  // 分组名称
                .packagesToScan("com.rick.backend.module.demo.controller") // 扫描包
                .pathsToMatch("/courses/**")         // 可选，按路径匹配
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("我的SpringBoot项目 API 文档")
                        .version("1.0")
                        .description("基于 SpringDoc OpenAPI 生成的接口文档")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("dev@example.com")))
                // 在 Swagger UI 页面上添加一个“Authorize”按钮，用户输入 Token 后，后续所有接口请求都会自动在 Header 中携带该 Token。
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("请输入 JWT Token")))
                // 2. 全局应用安全方案 (所有接口默认都需要认证，除非接口上单独覆盖)
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

}