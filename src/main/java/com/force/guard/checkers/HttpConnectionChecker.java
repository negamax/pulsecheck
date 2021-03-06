package com.force.guard.checkers;

import com.force.guard.aws.data.models.HttpError;
import com.force.guard.config.ApplicationConfig;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.logging.Level;

/**
 * Created by mohitaggarwal on 30/12/2015.
 */
@Component
public class HttpConnectionChecker extends ErrorChecker {
    @Value("${httpconnection.cron.interval}")
    private void setWaitInterval(long interval) {
        this.waitInterval = interval;
    }

    private long timestamp;
    private int httpstatus;

    @Override
    protected void saveResult(String siteName) {
        HttpError error = new HttpError();
        error.setName(siteName);
        error.setTimestamp(timestamp);
        error.setHttpstatus(httpstatus);

        dynamoDBMapper.save(error);
    }

    @Override
    protected void getResultForSite(String siteName) {
        this.timestamp = new Date().getTime();
        httpstatus = -1;

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(siteName).openConnection();

            this.httpstatus = conn.getResponseCode();

            doWait(10000);

            conn.disconnect();
        } catch (IOException ex) {
            httpstatus = ApplicationConfig.CONNECTION_FAILED_CODE;
            logger.log(Level.WARNING, ExceptionUtils.getFullStackTrace(ex));
        }
    }

    @Override
    protected void newResultsAdded() {
        this.cacheEvicter.clearHttpErrorsCache();
    }

    @Override
    protected String getSiteNameForErrorCheck(String name) {
        return "http://" + this.prependWWW(name);
    }
}
