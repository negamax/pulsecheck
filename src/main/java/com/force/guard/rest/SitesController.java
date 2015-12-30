package com.force.guard.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.force.guard.aws.data.Sites;
import com.force.guard.services.ErrorReporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by mohitaggarwal on 30/12/2015.
 */
@RestController
public class SitesController {
    @Autowired
    private Sites sites;

    @Autowired
    private ErrorReporter errorReporter;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping("/sites")
    public String getSiteList() {
        return sites.getListAsJSON();
    }

    @RequestMapping("/sslcertconnectionerrors")
    public String getSSLCertConnectionErrors() {
        try {
            return objectMapper.writeValueAsString(errorReporter.getSslConnectionErrors());
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @RequestMapping("/sslcertexpirationerrors")
    public String getSSLCertExpirationErrors() {
        try {
            return objectMapper.writeValueAsString(errorReporter.getSslCertExpiringSoon());
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @RequestMapping("/javascripterrors")
    public String getJavaScriptErrors() {
        try {
            return objectMapper.writeValueAsString(errorReporter.getJsErrors());
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
