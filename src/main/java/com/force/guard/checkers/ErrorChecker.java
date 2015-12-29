package com.force.guard.checkers;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.force.guard.aws.data.Sites;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by mohitaggarwal on 28/12/2015.
 */
@Component
public abstract class ErrorChecker implements Runnable {
    @Autowired
    protected Sites sites;

    @Autowired
    protected DynamoDBMapper dynamoDBMapper;

    protected long waitInterval;

    protected void doWait(long interval) {
        try {
            Thread.sleep(interval);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        //we run forever and ever!
        while(true) {

            for(final String siteName : sites.getSiteNames()) {
                String siteNameHttp = getSiteNameForErrorCheck(siteName);

                this.getResultForSite(siteNameHttp);
                this.saveResult(siteName);
            }

            doWait(this.waitInterval);
        }
    }

    //different checkers will save results in different tables
    abstract void saveResult(String siteName);

    //site name is our hash (primary) key and all tables should have it entry for it
    abstract void getResultForSite(String siteName);

    //could be different from name in db (for e.g. we may want to prepend http or https)
    abstract String getSiteNameForErrorCheck(String name);
}