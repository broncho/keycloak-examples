package com.sinosun.keycloak.client;

import org.keycloak.adapters.springboot.client.KeycloakSecurityContextClientRequestInterceptor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;

/**
 * Author: secondriver
 * Created: 2020/3/25
 */
public class RestTemplateFactory {
    
    private RestTemplateFactory() {
    
    }
    
    public static RestTemplate create() {
        return new RestTemplateBuilder()
                .customizers(new ClientRestTemplateCustomizer())
                .build();
    }
    
    static class ClientRestTemplateCustomizer implements RestTemplateCustomizer {
        
        @Override
        public void customize(RestTemplate restTemplate) {
            restTemplate.setInterceptors(Collections.singletonList(new ClientRestRequestInterceptor()));
        }
    }
    
    
    static class ClientRestRequestInterceptor extends KeycloakSecurityContextClientRequestInterceptor {
        
        private KeycloakTokenContext keycloakTokenContext = KeycloakTokenContext.create();
        
        @Override
        public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
            httpRequest.getHeaders().set("Authorization", "Bearer " + keycloakTokenContext.getToken());
            return clientHttpRequestExecution.execute(httpRequest, bytes);
        }
    }
}
