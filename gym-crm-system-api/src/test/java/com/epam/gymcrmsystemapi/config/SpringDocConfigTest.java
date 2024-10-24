package com.epam.gymcrmsystemapi.config;

import com.epam.gymcrmsystemapi.config.SpringDocConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import io.swagger.v3.oas.models.security.SecurityRequirement;

import static org.assertj.core.api.Assertions.assertThat;

public class SpringDocConfigTest {

    @Test
    void customOpenAPI_ShouldReturnValidOpenAPIConfiguration() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringDocConfig.class);

        OpenAPI openAPI = context.getBean(OpenAPI.class);

        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getInfo()).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).isEqualTo("Gym crm system API");

        assertThat(openAPI.getComponents()).isNotNull();
        assertThat(openAPI.getComponents().getSecuritySchemes()).containsKey("bearer-key");

        SecurityScheme securityScheme = openAPI.getComponents().getSecuritySchemes().get("bearer-key");
        assertThat(securityScheme).isNotNull();
        assertThat(securityScheme.getType()).isEqualTo(SecurityScheme.Type.HTTP);
        assertThat(securityScheme.getScheme()).isEqualTo("bearer");
        assertThat(securityScheme.getBearerFormat()).isEqualTo("JWT");

        assertThat(openAPI.getSecurity()).isNotNull();
        assertThat(openAPI.getSecurity()).hasSize(1);
        assertThat(openAPI.getSecurity().get(0)).containsExactlyInAnyOrderEntriesOf(new SecurityRequirement().addList("bearer-key"));

        context.close();
    }
}