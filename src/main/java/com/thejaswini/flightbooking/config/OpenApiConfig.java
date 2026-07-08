package com.thejaswini.flightbooking.config;

import com.thejaswini.flightbooking.constant.AppConstants;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI metadata (title, version, description) surfaced in the Swagger UI and {@code /v3/api-docs}.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Builds the OpenAPI definition with the API's descriptive metadata.
     *
     * @return the configured {@link OpenAPI} bean
     */
    @Bean
    public OpenAPI flightBookingOpenApi() {
        return new OpenAPI().info(new Info()
                .title(AppConstants.API_TITLE)
                .version(AppConstants.API_VERSION)
                .description(AppConstants.API_DESCRIPTION));
    }
}
