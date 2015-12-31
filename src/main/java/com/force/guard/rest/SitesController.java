package com.force.guard.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.force.guard.aws.data.Sites;
import com.force.guard.rest.models.SiteInfo;
import com.force.guard.services.ErrorReporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
        List<SiteInfo> siteInfos = new ArrayList<>();

        for(String name : sites.getSiteNames()) {
            SiteInfo info = new SiteInfo();
            info.setName(name);
            info.setJsError(errorReporter.hasJSError(name));
            info.setHttpError(errorReporter.hasHttpError(name));
            info.setSslCertError(errorReporter.hasSSLCertError(name));
            info.setSslConnectionError(errorReporter.hashSSLConnectionError(name));

            siteInfos.add(info);
        }

        try {
            return objectMapper.writeValueAsString(siteInfos);
        } catch (JsonProcessingException e) {
            return null;
        }
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

    @RequestMapping("/httperrors")
    public String getHttpErrors() {
        try {
            return objectMapper.writeValueAsString(errorReporter.getHttpErrors());
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
