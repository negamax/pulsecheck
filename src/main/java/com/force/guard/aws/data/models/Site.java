package com.force.guard.aws.data.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Created by mohitaggarwal on 28/12/2015.
 */
@DynamoDBTable(tableName = "sites")
public class Site {
    private String name;

    @DynamoDBHashKey
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
