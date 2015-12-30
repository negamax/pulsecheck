package com.force.guard.checkers;

import com.force.guard.aws.data.models.HttpError;
import com.force.guard.config.ApplicationConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

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
    void saveResult(String siteName) {
        HttpError error = new HttpError();
        error.setName(siteName);
        error.setTimestamp(timestamp);
        error.setHttpstatus(httpstatus);

        dynamoDBMapper.save(error);
    }

    @Override
    void getResultForSite(String siteName) {
        timestamp = Long.MAX_VALUE;
        httpstatus = -1;

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(siteName).openConnection();

            this.httpstatus = conn.getResponseCode();
            this.timestamp = new Date().getTime();

            doWait(10000);

            conn.disconnect();
        } catch (IOException ex) {
            httpstatus = ApplicationConfig.CONNECTION_FAILED_CODE;
        }
    }

    @Override
    String getSiteNameForErrorCheck(String name) {
        return "http://" + this.prependWWW(name);
    }
}
