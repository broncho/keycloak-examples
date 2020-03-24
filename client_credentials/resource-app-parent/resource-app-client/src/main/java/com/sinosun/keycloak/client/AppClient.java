package com.sinosun.keycloak.client;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.keycloak.adapters.springboot.client.KeycloakSecurityContextClientRequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;

/**
 * Author: secondriver
 * Created: 2020/3/16
 */
@RunWith(JUnit4.class)
public class AppClient {
    
    private final Logger logger = LoggerFactory.getLogger(AppClient.class);
    
    private RestTemplate restTemplate = new RestTemplateBuilder()
            .customizers(new ClientRestTemplateCustomizer())
            .build();
    
    private String oneService = "http://127.0.0.1:9100";
    
    private String twoService = "http://127.0.0.1:9200";
    
    @Test
    public void testOneIndex() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(oneService + "/one/index", String.class);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        logger.info("/one/index ->  {}", responseEntity.getBody());
    }
    
    @Test
    public void testOneGreeting() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(oneService + "/one/greeting?name=one", String.class);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        logger.info("/one/greeting?name=one ->  {}", responseEntity.getBody());
    }
    
    
    @Test
    public void testTwoIndex() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(twoService + "/two/index", String.class);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        logger.info("/two/index ->  {}", responseEntity.getBody());
    }
    
    @Test
    public void testTwoGreeting() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(twoService + "/two/greeting?name=two", String.class);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        logger.info("/two/greeting?name=two ->  {}", responseEntity.getBody());
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
            httpRequest.getHeaders().set("Authorization", "Bearer " + KeycloakTokenContext.create().getToken());
            return clientHttpRequestExecution.execute(httpRequest, bytes);
        }
    }
}