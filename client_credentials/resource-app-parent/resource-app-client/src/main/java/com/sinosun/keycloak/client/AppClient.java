package com.sinosun.keycloak.client;

import org.keycloak.adapters.springboot.client.KeycloakSecurityContextClientRequestInterceptor;
import org.keycloak.authorization.client.AuthzClient;
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
 * Created: 2020/3/16
 */
public class AppClient {
    
    
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .customizers(new ClientRestTemplateCustomizer())
                .build();
        
        
        
        String oneService = "http://127.0.0.1:9100";
        
        String oneValue = restTemplate.getForObject(oneService + "/one/index", String.class);
        System.out.println(oneValue);
        
        String twoValue = restTemplate.getForObject(oneService + "/one/greeting?name=jack", String.class);
        System.out.println(twoValue);
    }
    
    
    static class ServiceContext {
        
        private static AuthzClient authzClient = AuthzClient.create();
        
        public static String getToken() {
            String token = authzClient.obtainAccessToken().getToken();
            System.out.println("accessToken:" + token);
            return token;
        }
    }
    
    static class ClientRestTemplateCustomizer implements RestTemplateCustomizer {
        
        @Override
        public void customize(RestTemplate restTemplate) {
            restTemplate.setInterceptors(Collections.singletonList(new ClientRestRequestInterceptor()));
        }
    }
    
    
    static class ClientRestRequestInterceptor extends KeycloakSecurityContextClientRequestInterceptor {
        
        @Override
        public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
            httpRequest.getHeaders().set("Authorization", "Bearer " + ServiceContext.getToken());
            return clientHttpRequestExecution.execute(httpRequest, bytes);
        }
    }
}
