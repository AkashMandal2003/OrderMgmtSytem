package com.jocata.oms.common.response;

import javax.xml.crypto.Data;

public class GenericResponsePayload<T> {

    private String requestId;

    private String timeStamp;

    private String statusCode;

    private String statusMessage;

    private T data;

    public GenericResponsePayload(String requestId, String timeStamp, String statusCode, String statusMessage, T data) {
        this.requestId = requestId;
        this.timeStamp = timeStamp;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.data = data;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
