package com.taskmanager.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.customizers.OpenApiCustomizer;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Task Manager API")
                        .version("1.0")
                        .description("REST API for Task Management")
                        .contact(new Contact().name("Developer")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    @Bean
    public OpenApiCustomizer globalHeaderOpenApiCustomizer() {
        return openApi -> openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
            io.swagger.v3.oas.models.responses.ApiResponses apiResponses = operation.getResponses();
            
            // Add Global 400 Response (Validation)
            io.swagger.v3.oas.models.responses.ApiResponse badRequestResult = new io.swagger.v3.oas.models.responses.ApiResponse().description("Bad Request - Validation Failed or Missing Data");
            apiResponses.addApiResponse("400", badRequestResult);

            // Add Global 401 Response
            io.swagger.v3.oas.models.responses.ApiResponse unauthorizedResult = new io.swagger.v3.oas.models.responses.ApiResponse().description("Unauthorized - Invalid or Missing JWT Token");
            apiResponses.addApiResponse("401", unauthorizedResult);
            
            // Add Global 403 Response
            io.swagger.v3.oas.models.responses.ApiResponse forbiddenResult = new io.swagger.v3.oas.models.responses.ApiResponse().description("Forbidden - You do not have permission to access this resource");
            apiResponses.addApiResponse("403", forbiddenResult);
            
            // Add Global 500 Response
            io.swagger.v3.oas.models.responses.ApiResponse serverErrorResult = new io.swagger.v3.oas.models.responses.ApiResponse().description("Internal Server Error - Something went wrong on the server");
            apiResponses.addApiResponse("500", serverErrorResult);
        }));
    }
}
