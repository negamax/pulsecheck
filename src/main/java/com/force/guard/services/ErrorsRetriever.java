package com.force.guard.services;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ConditionalOperator;
import com.force.guard.aws.data.models.HttpError;
import com.force.guard.aws.data.models.JSError;
import com.force.guard.aws.data.models.SSLCert;
import com.force.guard.config.ApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Crunches through collected data and determines applicable errors based on business rules
 *
 * Also caches the results, until cache is explicitly invalidated
 *
 * Created by mohitaggarwal on 06/01/2016.
 */
@Service
public class ErrorsRetriever {
    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Cacheable("jsErrors")
    public Map<String, List<JSError>> findJSErrors() {
        //find entries in last two hours and remove duplicate errors
        long fromtime = new Date().getTime() - (3600000 * 2l);

        AttributeValue value = new AttributeValue();
        value.setN("" + fromtime);

        DynamoDBScanExpression jserrorsexpression = new DynamoDBScanExpression();
        jserrorsexpression.addFilterCondition("timestamp", new Condition().withComparisonOperator(ComparisonOperator.GT).withAttributeValueList(value));

        List<JSError> latestJSErrors = dynamoDBMapper.scan(JSError.class, jserrorsexpression);

        Map<String, List<JSError>> jsErrors = new HashMap<>();

        for(JSError error : latestJSErrors) {
            if(!jsErrors.containsKey(error.getName())) {
                List<JSError> jsErrorList = new ArrayList<>();
                jsErrorList.add(error);
                jsErrors.put(error.getName(), jsErrorList);
            }
            else
            {
                List<JSError> jsErrorList = jsErrors.get(error.getName());

                boolean duplicate = false;

                for(JSError knownerror : jsErrorList) {
                    if(knownerror.getErrors().equals(error.getErrors())) {
                        duplicate = true;
                        break;
                    }
                }

                if(!duplicate) {
                    jsErrorList.add(error);
                }

                jsErrors.put(error.getName(), jsErrorList);
            }
        }

        return jsErrors;
    }

    @Cacheable("httpErrors")
    public List<HttpError> findHttpErrors() {
        //in last one hour httpstatus was different from 200 or 310
        long fromtime = new Date().getTime() - (3600000l);

        AttributeValue value = new AttributeValue();
        value.setN("" + fromtime);

        DynamoDBScanExpression httpErrorExpression = new DynamoDBScanExpression();

        httpErrorExpression.addFilterCondition("timestamp", new Condition().withComparisonOperator(ComparisonOperator.GT).withAttributeValueList(value));

        AttributeValue successCode = new AttributeValue();
        successCode.setN("" + 200);

        AttributeValue redirectCode = new AttributeValue();
        redirectCode.setN("" + 301);

        httpErrorExpression.addFilterCondition("httpstatus", new Condition().withComparisonOperator(ComparisonOperator.NE).withAttributeValueList(successCode));
        httpErrorExpression.addFilterCondition("httpstatus", new Condition().withComparisonOperator(ComparisonOperator.NE).withAttributeValueList(redirectCode));
        httpErrorExpression.setConditionalOperator(ConditionalOperator.AND);


        List<HttpError> httpErrors = dynamoDBMapper.scan(HttpError.class, httpErrorExpression);

        List<HttpError> httpErrorsCopy = new ArrayList<>(httpErrors);

        //for some reason second filter (301) is overwriting first(200)
        //filtering here
        List<HttpError> errorList = new ArrayList<>();

        for(HttpError httpError : httpErrors) {
            if(httpError.getHttpstatus() == 200 || httpError.getHttpstatus() == 302) {
                errorList.add(httpError);
            }
        }

        httpErrorsCopy.removeAll(errorList);

        return httpErrorsCopy;
    }

    @Cacheable("sslCertExpiringSoon")
    public List<SSLCert> findSSLCertExpirationErrors() {
        long soonExpiry = new Date().getTime() + (86400000l * 180l);

        AttributeValue value = new AttributeValue();
        value.setN("" + soonExpiry);

        DynamoDBScanExpression expirationExpression = new DynamoDBScanExpression();
        expirationExpression.addFilterCondition("expiryDate", new Condition().withComparisonOperator(ComparisonOperator.LT).withAttributeValueList(value));

        List<SSLCert> sslCertExpiringSoon  = dynamoDBMapper.scan(SSLCert.class, expirationExpression);

        List<SSLCert> sslCertExpiringSoonCopy = new ArrayList<>();

        sslCertExpiringSoonCopy.addAll(sslCertExpiringSoon);

        List<SSLCert> duplicates = new ArrayList<>();

        for(SSLCert sslCert : sslCertExpiringSoon) {
            if(sslCert.getExpiryDate() == ApplicationConfig.CONNECTION_FAILED_CODE) {
                duplicates.add(sslCert);
            }
        }

        sslCertExpiringSoonCopy.removeAll(duplicates);
        return sslCertExpiringSoonCopy;
    }


    @Cacheable("sslCertConnectionErrors")
    public List<SSLCert> findSSLConnectionErrors() {
        DynamoDBScanExpression sslConnectionError = new DynamoDBScanExpression();

        AttributeValue value = new AttributeValue();
        value.setN("" + ApplicationConfig.CONNECTION_FAILED_CODE);

        sslConnectionError.addFilterCondition("expiryDate", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(value));

        return dynamoDBMapper.scan(SSLCert.class, sslConnectionError);
    }
}
