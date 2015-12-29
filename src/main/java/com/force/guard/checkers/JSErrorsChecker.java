package com.force.guard.checkers;

import com.force.guard.aws.data.models.JSError;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mohitaggarwal on 29/12/2015.
 */
@Component
public class JSErrorsChecker extends ErrorChecker {
    @Autowired
    private WebDriver webDriver;

    @Value("${jserrors.cron.interval}")
    private void setWaitInterval(long interval) {
        this.waitInterval = interval;
    }

    //we ideally should have an abstract result object in
    //parent class but this is okay for now
    private List<String> errors = new ArrayList<>();
    private long errorTimeStamp;

    @Override
    void saveResult(String siteName) {
        if(this.errors.size() > 0) {
            JSError jsError = new JSError();

            jsError.setName(siteName);
            jsError.setTimestamp(errorTimeStamp);
            jsError.setErrors(errors);

            dynamoDBMapper.save(jsError);
        }
    }

    @Override
    void getResultForSite(String siteName) {
        errors = new ArrayList<>();
        this.webDriver.get(siteName);

        //wait for site to fully load and js to initialize
        doWait(10000);

        LogEntries logEntries = webDriver.manage().logs().get(LogType.BROWSER);
        for (LogEntry entry : logEntries) {
            this.errorTimeStamp = entry.getTimestamp();
            this.errors.add(entry.getLevel() + " " + entry.getMessage());
        }
    }

    @Override
    String getSiteNameForErrorCheck(String name) {
        return "http://" + name;
    }

    @Override
    public void run() {
        //we run forever and ever!
        while(true) {

            for(final String siteName : sites.getSiteNames()) {
                String siteNameHttp = "http://" + siteName;

                this.getResultForSite(siteNameHttp);
                this.saveResult(siteName);
            }

            doWait(this.waitInterval);
        }
    }
}
