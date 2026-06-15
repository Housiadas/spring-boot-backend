package com.housi.backend.infrastructure.config;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.housi.backend.infrastructure.web.response.shared.ApiProblemDetail;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "API Documentation"),
        security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT")
public class SwaggerConfiguration {

    private static final String PROBLEM_DETAIL_MEDIA_TYPE = "application/problem+json";
    private static final String PROBLEM_DETAIL_SCHEMA_REF = "#/components/schemas/ApiProblemDetail";

    @Bean
    public OpenApiCustomizer globalErrorResponsesCustomizer() {
        return openApi -> {
            final var schemas =
                    ModelConverters.getInstance()
                            .readAll(ApiProblemDetail.class);
            if (openApi.getComponents() == null) {
                openApi.setComponents(new io.swagger.v3.oas.models.Components());
            }
            schemas.forEach((name, schema) -> openApi.getComponents().addSchemas(name, schema));

            final ApiResponse r401 = problemDetailResponse("Authentication required — valid Bearer token missing or expired");
            final ApiResponse r403 = problemDetailResponse("Access denied — insufficient permissions");
            final ApiResponse r500 = problemDetailResponse("Unexpected internal server error");

            openApi.getPaths()
                    .values()
                    .forEach(
                            pathItem ->
                                    pathItem
                                            .readOperations()
                                            .forEach(
                                                    operation -> {
                                                        if (operation.getResponses() == null) {
                                                            operation.setResponses(new ApiResponses());
                                                        }
                                                        operation.getResponses()
                                                                .addApiResponse("401", r401)
                                                                .addApiResponse("403", r403)
                                                                .addApiResponse("500", r500);
                                                    }));
        };
    }

    private ApiResponse problemDetailResponse(final String description) {
        return new ApiResponse()
                .description(description)
                .content(
                        new Content()
                                .addMediaType(
                                        PROBLEM_DETAIL_MEDIA_TYPE,
                                        new MediaType()
                                                .schema(
                                                        new Schema<>()
                                                                .$ref(PROBLEM_DETAIL_SCHEMA_REF))));
    }
}
