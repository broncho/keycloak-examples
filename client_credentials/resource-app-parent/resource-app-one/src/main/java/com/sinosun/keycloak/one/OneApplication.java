package com.sinosun.keycloak.one;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springboot.client.KeycloakRestTemplateCustomizer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Author: secondriver
 * Created: 2020/3/16
 */
@SpringBootApplication
public class OneApplication {
    
    
    @Bean
    public KeycloakSpringBootConfigResolver keycloakSpringBootConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }
    
    
    @Bean
    public KeycloakRestTemplateCustomizer keycloakRestTemplateCustomizer() {
        return new KeycloakRestTemplateCustomizer();
    }
    
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }
    
    public static void main(String[] args) {
        SpringApplication.run(OneApplication.class, args);
    }
}
