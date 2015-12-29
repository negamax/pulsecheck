package com.force.guard.aws.data.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.List;

/**
 * Created by mohitaggarwal on 28/12/2015.
 */
@DynamoDBTable(tableName = "jserrors")
public class JSError {
    private String name;
    private long timestamp;
    private List<String> errors;

    @DynamoDBHashKey
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBRangeKey
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @DynamoDBAttribute
    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
