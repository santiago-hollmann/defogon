package com.lookeate.java.api.model;

import java.io.Serializable;
import java.util.ArrayList;

import retrofit.RetrofitError;

@SuppressWarnings("serial")
public class APIResponse implements Serializable {
    public static final int INTERNAL_ERROR = -22;
    public static final int UNEXPECTED = -23;
    private int statusCode;
    private String statusMessage;
    private String requestId;
    private String action;
    private boolean cache;
    private boolean expired;
    private long updateDate;
    private ArrayList<Error> errors;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isSuccess() {
        return statusCode == 0;
    }

    public boolean isCache() {
        return cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean update) {
        this.expired = update;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public ArrayList<Error> getErrors() {
        return errors;
    }

    public void setErrors(ArrayList<Error> errors) {
        this.errors = errors;
    }

    public void setErrorContent(RetrofitError error) {
        ErrorResponse errorResponse;
        try {
            errorResponse = (ErrorResponse) error.getBodyAs(ErrorResponse.class);
            if (errorResponse == null) {
                errorResponse = new ErrorResponse();
                setStatusCode(UNEXPECTED);
                setStatusMessage(error.getLocalizedMessage());
            } else {
                setStatusCode(error.getResponse().getStatus());
                setStatusMessage(error.getResponse().getReason());
            }
        } catch (Exception ex) {
            errorResponse = new ErrorResponse();
            errorResponse.add(new Error(ex.getLocalizedMessage()));
            setStatusCode(INTERNAL_ERROR);
            setStatusMessage(error.getLocalizedMessage());
        }

        setErrors(errorResponse);
    }

    public String getLogMessage() {
        return String.format("[%s] %s(cache:%s expired:%s ...)", (requestId != null) ? requestId.substring(requestId.length() - 3) : "???",
                getClass().getSimpleName(), cache, expired);
    }
}
