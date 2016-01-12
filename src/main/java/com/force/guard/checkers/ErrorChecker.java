package com.force.guard.checkers;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.force.guard.aws.data.Sites;
import com.force.guard.util.CacheEvicter;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by mohitaggarwal on 28/12/2015.
 */
@Component
public abstract class ErrorChecker implements Runnable {
    protected Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    protected Sites sites;

    @Autowired
    protected DynamoDBMapper dynamoDBMapper;

    @Autowired
    protected CacheEvicter cacheEvicter;

    protected long waitInterval;

    protected void doWait(long interval) {
        try {
            Thread.sleep(interval);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected String prependWWW(String name) {
        StringTokenizer st = new StringTokenizer(name, ".");

        //convert abc.com -> www.abc.com
        if(st.countTokens() == 2) {
            name = "www." + name;
        }

        return name;
    }

    @Override
    public void run() {
        //we run forever and ever!
        while(true) {
            try
            {
                for(final String siteName : sites.getSiteNames()) {
                    String siteNameHttp = getSiteNameForErrorCheck(siteName);

                    this.getResultForSite(siteNameHttp);
                    this.saveResult(siteName);
                }

                this.newResultsAdded();

                doWait(this.waitInterval);
            }
            catch (Exception ex) {
                //never die
                logger.log(Level.WARNING, ExceptionUtils.getFullStackTrace(ex));
            }
        }
    }

    //called after errors collection for all sites is done
    protected abstract void newResultsAdded();

    //different checkers will save results in different tables
    protected abstract void saveResult(String siteName);

    //site name is our hash (primary) key and all tables should have it entry for it
    protected abstract void getResultForSite(String siteName);

    //could be different from name in db (for e.g. we may want to prepend http or https)
    protected abstract String getSiteNameForErrorCheck(String name);
}
