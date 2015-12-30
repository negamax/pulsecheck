package com.force.guard.services;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.force.guard.aws.data.models.HttpError;
import com.force.guard.aws.data.models.JSError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Wake up after a while and do db cleaning etc
 * <p>
 * Created by mohitaggarwal on 30/12/2015.
 */
@Service
public class HouseKeeper implements Runnable {
    @Value("${housekeeper.interval}")
    private long waitInterval;

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Override
    public void run() {
        try {

            //delete all records older than 24 hours
            long oldRecords = new Date().getTime() - 86400000;

            DynamoDBScanExpression expression = new DynamoDBScanExpression();

            AttributeValue value = new AttributeValue();
            value.setN("" + oldRecords);

            expression.addFilterCondition("timestamp", new Condition().withComparisonOperator(ComparisonOperator.LT).withAttributeValueList(value));

            List<HttpError> httpErrorRecords = dynamoDBMapper.scan(HttpError.class, expression);
            List<JSError> jsErrorRecords = dynamoDBMapper.scan(JSError.class, expression);

            deleteAllRecords(httpErrorRecords);
            deleteAllRecords(jsErrorRecords);

            Thread.sleep(waitInterval);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void deleteAllRecords(List<?> list) {
        for(Object obj : list) {
            dynamoDBMapper.delete(obj);
        }
    }
}
