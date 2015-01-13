package com.lookeate.java.api.contract;

import com.squareup.okhttp.OkHttpClient;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.OkClient;
import retrofit.converter.Converter;

public class LookeateAPI {

    private static final int DEFAULT_TIMEOUT = 60;

    private String url;
    private String languageCode;
    private String version;
    private final boolean log;
    private final String platform;

    private String token;

    public void setToken(String token) {
        this.token = token;
    }

    public LookeateAPI(String url, String version, String platform, String languageCode, boolean log) {
        this.url = url;
        this.version = version;
        this.platform = platform;
        this.languageCode = languageCode;
        this.log = log;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    Object getRestAdapter(Class<?> aClass, long timeout) {
        return getRestAdapter(aClass, timeout, url, null, null);
    }

    Object getRestAdapter(Class<?> aClass, long timeout, Converter converter, Map<String, String> filters) {
        return getRestAdapter(aClass, timeout, url, converter, filters);
    }

    Object getRestAdapter(Class<?> aClass, long timeout, String url, Converter converter, Map<String, String> filters) {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(timeout, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(timeout, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(timeout, TimeUnit.SECONDS);
        final RestAdapter.Builder restAdapterBuilder = new RestAdapter.Builder().setEndpoint(url).setClient(new OkClient(okHttpClient))
                .setRequestInterceptor(getRequestInterceptor(filters));
        if (log) {
            restAdapterBuilder.setLogLevel(LogLevel.FULL);
        }
        if (converter != null) {
            restAdapterBuilder.setConverter(converter);
        }
        final RestAdapter restAdapter = restAdapterBuilder.build();
        return restAdapter.create(aClass);
    }

    DomainContract getDomainContract() {
        return getDomainContract(DEFAULT_TIMEOUT);
    }

    DomainContract getDomainContract(long timeout) {
        return (DomainContract) getRestAdapter(DomainContract.class, timeout);
    }

    RequestInterceptor getRequestInterceptor(final Map<String, String> filters) {
        return new RequestInterceptor() {

            @Override
            public void intercept(RequestFacade request) {
                request.addQueryParam("platform", platform);
                request.addQueryParam("version", version);
                request.addQueryParam("languageCode", languageCode);
                if (token != null) {
                    request.addQueryParam("token", token);
                }
                if (filters != null) {
                    for (String key : filters.keySet()) {
                        request.addQueryParam(key, filters.get(key));
                    }
                }
            }
        };
    }


    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String appVersion) {
        version = appVersion;
    }

}