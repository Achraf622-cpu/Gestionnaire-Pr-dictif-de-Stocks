package com.team.sys_ai.config;

import com.team.sys_ai.security.CustomUserDetailsService.CustomUserDetails;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

/**
 * OpenAPI/Swagger configuration for API documentation.
 */
@Configuration
public class OpenApiConfig {

        static {
                // Hide Pageable internals - show as simple page/size query params
                SpringDocUtils.getConfig().replaceWithClass(Pageable.class, PageableAsQueryParam.class);

                // Completely ignore CustomUserDetails - it's injected by Spring Security
                SpringDocUtils.getConfig().addAnnotationsToIgnore(AuthenticationPrincipal.class);
                SpringDocUtils.getConfig().removeRequestWrapperToIgnore(CustomUserDetails.class);
                SpringDocUtils.getConfig().addRequestWrapperToIgnore(CustomUserDetails.class);
        }

        /**
         * Simple class to represent Pageable as query parameters in Swagger UI.
         */
        public static class PageableAsQueryParam {
                @io.swagger.v3.oas.annotations.Parameter(description = "Page number (0-indexed)", example = "0")
                private Integer page;

                @io.swagger.v3.oas.annotations.Parameter(description = "Page size", example = "20")
                private Integer size;

                public Integer getPage() {
                        return page;
                }

                public void setPage(Integer page) {
                        this.page = page;
                }

                public Integer getSize() {
                        return size;
                }

                public void setSize(Integer size) {
                        this.size = size;
                }
        }

        @Bean
        public OpenAPI customOpenAPI() {
                final String securitySchemeName = "bearerAuth";

                return new OpenAPI()
                                .info(new Info()
                                                .title("Système AI de Prévision et Gestion de Stock")
                                                .description("API REST pour la gestion prédictive des stocks avec intelligence artificielle")
                                                .version("1.0.0")
                                                .contact(new Contact()
                                                                .name("Team Sys AI")
                                                                .email("contact@sysai.com"))
                                                .license(new License()
                                                                .name("MIT License")
                                                                .url("https://opensource.org/licenses/MIT")))
                                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                                .components(new Components()
                                                .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                                                .name(securitySchemeName)
                                                                .type(SecurityScheme.Type.HTTP)
                                                                .scheme("bearer")
                                                                .bearerFormat("JWT")
                                                                .description("Entrez votre token JWT")));
        }
}
