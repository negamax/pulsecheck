package com.force.guard.aws.data.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Created by mohitaggarwal on 29/12/2015.
 *
 * There's only partition key so save will result in update. Which is good as we don't want multiple records for sslcert info
 * for the same domain
 */
@DynamoDBTable(tableName = "sslcert")
public class SSLCert {
    @DynamoDBHashKey
    private String name;

    @DynamoDBAttribute
    private long expiryDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(long expiryDate) {
        this.expiryDate = expiryDate;
    }
}
