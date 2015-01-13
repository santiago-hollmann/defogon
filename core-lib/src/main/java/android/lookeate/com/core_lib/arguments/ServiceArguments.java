package android.lookeate.com.core_lib.arguments;

import android.lookeate.com.core_lib.helpers.DateHelper;

import com.lookeate.java.api.model.APIResponse;

import java.io.Serializable;
import java.lang.reflect.Type;

@SuppressWarnings("serial")
public abstract class ServiceArguments implements Serializable {
    private static final int MAX_RETRIES = 3;

    protected long TTL = 0;
    protected boolean cache = false;
    protected boolean allowShowCache = false;
    protected boolean offlineOnly = false;
    private String requestId;
    private int retries = 0;

    public long getTTL() {
        return TTL;
    }

    public void setTTL(long tTL) {
        TTL = tTL;
    }

    public boolean useCache() {
        return cache;
    }

    public void useCache(boolean cache) {
        this.cache = cache;
    }

    public boolean isOfflineOnly() {
        return offlineOnly;
    }

    public void setOfflineOnly(boolean offlineOnly) {
        this.offlineOnly = offlineOnly;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Type getType() {
        return null;
    }

    public APIResponse preProcess(APIResponse response) {
        return response;
    }

    public APIResponse postProcess(APIResponse response) {
        return response;
    }

    public String getCacheKey() {
        return null;
    }

    public boolean isAllowShowCache() {
        return allowShowCache;
    }

    public void setAllowShowCache(boolean allowShowCache) {
        this.allowShowCache = allowShowCache;
    }

    public boolean canRetry() {
        return retries <= MAX_RETRIES;
    }

    public void incrementRetries() {
        retries++;
    }

    public int getRetries() {
        return retries;
    }

    public void resetRetries() {
        retries = 0;
    }

    public String getLogMessage() {
        return String.format("[%s] %s(cache:%s allowShowCache:%s offlineOnly:%s TTL:%s ...)",
                (requestId != null) ? requestId.substring(requestId.length() - 3) : "???", getClass().getSimpleName(), cache,
                allowShowCache, offlineOnly, DateHelper.formatTimeSpan(TTL));
    }
}
