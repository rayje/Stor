package com.stor.p2p;

/**
 * Interface for wrapping responses that are returned from the Application to the Server
 */
public interface AppResponse<T> {

    /**
     * Check whether the response is associated with a pre-mature termination due to an Exception
     * @return boolean
     */
    public boolean hasError();

    /**
     * Exception message associated with the current state of the Application
     * @return String
     */
    public String getErrorMessage();

    /**
     * Response from the server, in case the request completed successfully
     * @return String
     */
    public T getResponse();

    //setResponse
    public void setResponse(T value);

    //setException
    public void setErrorMessage(String message);
}
