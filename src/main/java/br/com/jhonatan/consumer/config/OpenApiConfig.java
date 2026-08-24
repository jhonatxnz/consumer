package br.com.jhonatan.consumer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    // Simple metadata only, no security scheme yet.
    // TODO: once authentication is implemented, add a SecurityScheme (e.g. bearerAuth)
    // here and reference it with @SecurityRequirement on the protected controllers.
    @Bean
    public OpenAPI consumerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Consumer API")
                        .description("API for managing subscription activation, cancellation and status, consumed on top of the provider API")
                        .version("v0.0.1")
                        .contact(new Contact().name("Jhonatan Willian dos Santos Silva").email("jw.jhonatan1705@gmail.com"))
                );
    }
}
