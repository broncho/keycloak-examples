package com.sinosun.keycloak.client;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Author: secondriver
 * Created: 2020/3/16
 */
@RunWith(JUnit4.class)
public class AppClient {
    
    private final Logger logger = LoggerFactory.getLogger(AppClient.class);
    
   private RestTemplate restTemplate = RestTemplateFactory.create();
    
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
    
  
}