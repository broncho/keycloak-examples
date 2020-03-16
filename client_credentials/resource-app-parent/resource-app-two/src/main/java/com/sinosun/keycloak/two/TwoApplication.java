package com.sinosun.keycloak.two;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Author: secondriver
 * Created: 2020/3/16
 */
@SpringBootApplication
public class TwoApplication {
    
    
    @Bean
    public KeycloakSpringBootConfigResolver keycloakSpringBootConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }
    
    public static void main(String[] args) {
        SpringApplication.run(TwoApplication.class,args);
    }
}
