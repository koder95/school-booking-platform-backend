package pl.koder95.sbp.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenApi(@Value("${sbp.backend.version}") String version) {
        return new OpenAPI().info(new Info()
                .title("School Booking Platform API")
                .version(version)
                .contact(new Contact().name("Koder95").url("https://github.com/koder95"))
                .license(new License()
                        .name("The MIT License")
                        .url("https://opensource.org/licenses/MIT")
                )
        );
    }
}
