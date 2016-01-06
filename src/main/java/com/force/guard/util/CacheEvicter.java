package com.force.guard.util;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

/**
 * CacheEvict and other Cache marked methods can only be called from outside to have any effect
 * This class acts as a place holder for cacheevict and can/should be called for eviction from
 * outside based on required delay etc
 *
 * Created by mohitaggarwal on 05/01/2016.
 */
@Component
public class CacheEvicter {
    @CacheEvict(cacheNames = "sites", allEntries = true)
    public void clearSitesCache() {
    }

    @CacheEvict(cacheNames = "httpErrors", allEntries = true)
    public void clearHttpErrorsCache() {
    }

    @CacheEvict(cacheNames = "jsErrors", allEntries = true)
    public void clearJSErrorsCache() {
    }

    @CacheEvict(value = {"sslCertConnectionErrors", "sslCertExpiringSoon"}, allEntries = true)
    public void clearSSLErrorsCache() {
    }
}
