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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Crunches through collected data and determines applicable errors based on business rules
 *
 * This also acts as a cache for reports, so we don't hit the db. yay!
 *
 * Created by mohitaggarwal on 30/12/2015.
 */
@Service
public class ErrorReporter implements Runnable {
    @Value("${errorreporter.interval}")
    private long waitInterval;

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    private List<SSLCert> sslCertConnectionErrors = new ArrayList<>();
    private List<SSLCert> sslCertExpiringSoon = new ArrayList<>();
    private Map<String, List<JSError>> jsErrors = new HashMap<>();
    private List<HttpError> httpErrors = new ArrayList<>();

    @Override
    public void run() {

        while(true) {
            try {
                findSSLCertErrors();
                findHttpErrors();
                findJSErrors();

                Thread.sleep(waitInterval);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void findJSErrors() {
        //find entries in last two hours and remove duplicate errors
        long fromtime = new Date().getTime() - (3600000 * 2l);

        AttributeValue value = new AttributeValue();
        value.setN("" + fromtime);

        DynamoDBScanExpression jserrorsexpression = new DynamoDBScanExpression();
        jserrorsexpression.addFilterCondition("timestamp", new Condition().withComparisonOperator(ComparisonOperator.GT).withAttributeValueList(value));

        List<JSError> latestJSErrors = dynamoDBMapper.scan(JSError.class, jserrorsexpression);

        synchronized (this) {
            this.jsErrors.clear();

            for(JSError error : latestJSErrors) {
                if(!this.jsErrors.containsKey(error.getName())) {
                    List<JSError> jsErrorList = new ArrayList<>();
                    jsErrorList.add(error);
                    this.jsErrors.put(error.getName(), jsErrorList);
                }
                else
                {
                    List<JSError> jsErrorList = this.jsErrors.get(error.getName());

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

                    this.jsErrors.put(error.getName(), jsErrorList);
                }
            }
        }
    }

    private void findHttpErrors() {
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

        synchronized (this) {
            this.httpErrors = dynamoDBMapper.scan(HttpError.class, httpErrorExpression);

            List<HttpError> httpErrorsCopy = new ArrayList<>(this.httpErrors);

            //for some reason second filter (301) is overwriting first(200)
            //filtering here
            List<HttpError> errorList = new ArrayList<>();

            for(HttpError httpError : this.httpErrors) {
                if(httpError.getHttpstatus() == 200 || httpError.getHttpstatus() == 302) {
                    errorList.add(httpError);
                }
            }

            httpErrorsCopy.removeAll(errorList);

            this.httpErrors = httpErrorsCopy;
        }
    }

    private void findSSLCertErrors() {
        //-10 or expiration in 6 or less months
        findSSLConnectionErrors();
        findSSLCertExpirationErrors();
    }

    private void findSSLCertExpirationErrors() {
        long soonExpiry = new Date().getTime() + (86400000l * 180l);

        AttributeValue value = new AttributeValue();
        value.setN("" + soonExpiry);

        DynamoDBScanExpression expirationExpression = new DynamoDBScanExpression();
        expirationExpression.addFilterCondition("expiryDate", new Condition().withComparisonOperator(ComparisonOperator.LT).withAttributeValueList(value));

        synchronized (this) {
            this.sslCertExpiringSoon = dynamoDBMapper.scan(SSLCert.class, expirationExpression);

            List<SSLCert> sslCertExpiringSoonCopy = new ArrayList<>();
            sslCertExpiringSoonCopy.addAll(this.sslCertExpiringSoon);

            List<SSLCert> duplicates = new ArrayList<>();

            for(SSLCert sslCert : sslCertExpiringSoon) {
                if(sslCert.getExpiryDate() == ApplicationConfig.CONNECTION_FAILED_CODE) {
                    duplicates.add(sslCert);
                }
            }

            sslCertExpiringSoonCopy.removeAll(duplicates);
            this.sslCertExpiringSoon = sslCertExpiringSoonCopy;
        }
    }

    private void findSSLConnectionErrors() {
        DynamoDBScanExpression sslConnectionError = new DynamoDBScanExpression();

        AttributeValue value = new AttributeValue();
        value.setN("" + ApplicationConfig.CONNECTION_FAILED_CODE);

        sslConnectionError.addFilterCondition("expiryDate", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(value));

        synchronized (this) {
            this.sslCertConnectionErrors = dynamoDBMapper.scan(SSLCert.class, sslConnectionError);
        }
    }

    public synchronized List<SSLCert> getSslConnectionErrors() {
        return this.sslCertConnectionErrors;
    }

    public synchronized List<SSLCert> getSslCertExpiringSoon() {
        return this.sslCertExpiringSoon;
    }

    public synchronized Map<String, List<JSError>> getJsErrors() {
        return jsErrors;
    }

    public synchronized List<HttpError> getHttpErrors() {
        return httpErrors;
    }

    public synchronized boolean hasJSError(String name) {
        return this.jsErrors.containsKey(name);
    }

    public synchronized boolean hasHttpError(String name) {
        for(HttpError httpError : this.httpErrors) {
            if(httpError.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    public synchronized boolean hashSSLConnectionError(String name) {
        for(SSLCert sslCert : this.sslCertConnectionErrors) {
            if(sslCert.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    public synchronized boolean hasSSLCertError(String name) {
        for(SSLCert sslCert : this.sslCertExpiringSoon) {
            if(sslCert.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }
}
