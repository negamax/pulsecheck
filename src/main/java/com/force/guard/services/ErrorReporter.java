package com.force.guard.services;

import com.force.guard.aws.data.models.HttpError;
import com.force.guard.aws.data.models.JSError;
import com.force.guard.aws.data.models.SSLCert;
import com.force.guard.util.CacheEvicter;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by mohitaggarwal on 30/12/2015.
 */
@Service
public class ErrorReporter implements Runnable {
    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Value("${errorreporter.interval}")
    private long waitInterval;

    @Autowired
    private CacheEvicter cacheEvicter;

    @Autowired
    private ErrorsRetriever errorsRetriever;

    @Override
    public void run() {

        while(true) {
            try {
                //just warm up the cache
                //multiple calls won't affect performance once results are cached
                errorsRetriever.findHttpErrors();
                errorsRetriever.findJSErrors();
                errorsRetriever.findSSLCertExpirationErrors();
                errorsRetriever.findSSLConnectionErrors();

                this.cacheEvicter.clearSitesCache();
                Thread.sleep(waitInterval);
            } catch (Exception ex) {
                logger.log(Level.WARNING, ExceptionUtils.getFullStackTrace(ex));
            }
        }
    }

    public List<SSLCert> getSslConnectionErrors() {
        return errorsRetriever.findSSLConnectionErrors();
    }

    public List<SSLCert> getSslCertExpiringSoon() {
        return errorsRetriever.findSSLCertExpirationErrors();
    }

    public Map<String, List<JSError>> getJsErrors() {
        return errorsRetriever.findJSErrors();
    }

    public List<HttpError> getHttpErrors() {
        return errorsRetriever.findHttpErrors();
    }

    public boolean hasJSError(String name) {
        return this.getJsErrors().containsKey(name);
    }

    public boolean hasHttpError(String name) {
        for(HttpError httpError : this.getHttpErrors()) {
            if(httpError.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    public boolean hashSSLConnectionError(String name) {
        for(SSLCert sslCert : this.getSslConnectionErrors()) {
            if(sslCert.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    public boolean hasSSLCertError(String name) {
        for(SSLCert sslCert : this.getSslCertExpiringSoon()) {
            if(sslCert.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }
}
