package com.force.guard.checkers;

import com.force.guard.aws.data.models.SSLCert;
import com.force.guard.config.ApplicationConfig;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.logging.Level;

/**
 * Created by mohitaggarwal on 29/12/2015.
 */
@Component
public class SSLCertChecker extends ErrorChecker {
    @Value("${sslcert.cron.interval}")
    private void setWaitInterval(long interval) {
        this.waitInterval = interval;
    }

    private long expiryTimeStamp;

    @Override
    protected void saveResult(String siteName) {
        SSLCert cert = new SSLCert();
        cert.setName(siteName);
        cert.setExpiryDate(expiryTimeStamp);
        dynamoDBMapper.save(cert);
    }

    @Override
    protected void getResultForSite(String siteName) {
        expiryTimeStamp = Long.MAX_VALUE;
        try {
            HttpsURLConnection conn = (HttpsURLConnection) new URL(siteName).openConnection();

            //call to getservercert will fail without this
            conn.getResponseCode();

            doWait(10000);

            Certificate[] certs = conn.getServerCertificates();

            //don't know why there can be multiple certificates (but there can be)
            //we take the earliest expiration date
            for (Certificate cert : certs) {
                long expiryTime = ((X509Certificate) cert).getNotAfter().getTime();

                if (expiryTime < expiryTimeStamp) {
                    expiryTimeStamp = expiryTime;
                }
            }

            conn.disconnect();
        } catch (IOException ex) {
            //error
            expiryTimeStamp = ApplicationConfig.CONNECTION_FAILED_CODE;
            logger.log(Level.WARNING, ExceptionUtils.getFullStackTrace(ex));
        }
    }

    @Override
    protected void newResultsAdded() {
        this.cacheEvicter.clearSSLErrorsCache();
    }

    @Override
    protected String getSiteNameForErrorCheck(String name) {
        return "https://" + this.prependWWW(name);
    }
}
