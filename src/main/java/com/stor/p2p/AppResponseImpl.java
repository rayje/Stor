package com.stor.p2p;

import java.io.Serializable;

/**
 * Implementation of interface AppResponse
 *
 * @see AppResponse
 */
public class AppResponseImpl<T> implements AppResponse<T>, Serializable {
    private String errorMessage;
    private T response;

    @Override
    public boolean hasError() {
        return (errorMessage != null);
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public T getResponse() {
        return this.response;
    }

    @Override
    public void setResponse(T response) {
        this.response = response;
    }

    @Override
    public void setErrorMessage(String message) {
        this.errorMessage = message;
    }
}
