package com.force.guard.aws.data;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.force.guard.aws.data.models.Site;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mohitaggarwal on 28/12/2015.
 */
@Component
public class Sites {
    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Cacheable("sites")
    public List<String> getSiteNames() {
        List<Site> sites = dynamoDBMapper.scan(Site.class, new DynamoDBScanExpression());

        List<String> siteNames = new ArrayList<>();

        for(Site site : sites) {
            siteNames.add(site.getName());
        }

        return siteNames;
    }

    public String getListAsJSON() {
        try {
            return objectMapper.writeValueAsString(dynamoDBMapper.scan(Site.class, new DynamoDBScanExpression()));
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
