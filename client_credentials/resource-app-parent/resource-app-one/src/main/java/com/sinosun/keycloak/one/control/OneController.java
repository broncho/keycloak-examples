package com.sinosun.keycloak.one.control;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author: secondriver
 * Created: 2020/3/16
 */
@RestController
@RequestMapping(value = "/one")
public class OneController {
    
    @RequestMapping(value = {"/index"}, method = RequestMethod.GET)
    public String index() {
        return "path=/one/index get method return Hello ";
    }
    
    @RequestMapping(value = "/greeting", method = RequestMethod.GET)
    public String greeting(@RequestParam(name = "name") String name) {
        return "path /one/greeting get method return greeting=" + name;
    }
}