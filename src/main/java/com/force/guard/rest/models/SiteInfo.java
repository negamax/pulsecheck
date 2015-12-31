package com.force.guard.rest.models;

/**
 * Site with flags for error checks
 *
 * Created by mohitaggarwal on 31/12/2015.
 */
public class SiteInfo {
    private String name;
    private boolean httpError;
    private boolean jsError;
    private boolean sslCertError;
    private boolean sslConnectionError;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHttpError() {
        return httpError;
    }

    public void setHttpError(boolean httpError) {
        this.httpError = httpError;
    }

    public boolean isJsError() {
        return jsError;
    }

    public void setJsError(boolean jsError) {
        this.jsError = jsError;
    }

    public boolean isSslCertError() {
        return sslCertError;
    }

    public void setSslCertError(boolean sslCertError) {
        this.sslCertError = sslCertError;
    }

    public boolean isSslConnectionError() {
        return sslConnectionError;
    }

    public void setSslConnectionError(boolean sslConnectionError) {
        this.sslConnectionError = sslConnectionError;
    }
}
