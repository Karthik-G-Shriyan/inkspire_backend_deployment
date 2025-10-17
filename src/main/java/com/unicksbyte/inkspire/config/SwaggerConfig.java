package com.unicksbyte.inkspire.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    /**
     * Main OpenAPI configuration
     */
    @Bean
    public OpenAPI inkspireOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Inkspire API")                      // API Name
                        .description("API documentation for Inkspire backend")
                        .version("1.0.0")                           // Version
                        .contact(new Contact()
                                .name("Karthik G Shriyan")
                                .email("karthikgsringeri@gmail.com")
                                .url(""))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT"))
                )
                .externalDocs(new ExternalDocumentation()
                        .description("Inkspire Project Wiki")
                        .url("https://your-website.com/docs"));
    }


}
