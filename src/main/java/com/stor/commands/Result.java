package com.stor.commands;

/**
 * The Result interface defines the common methods that should
 * be implemented by all Result classes.
 *
 * Implementers of this class should use the ResultType class
 * to indicate the type of result given by the implementation.
 */
public interface Result<T> {

    /**
     * A getter method to retrieve the ResultType.
     *
     * @return An instance of a ResultType.
     * @see ResultType
     */
    ResultType getResultType();

    /**
     * getter for the actual Result
     * @return T
     */
    T getResult();

    /**
     * getter for the errorMessage
     */
    String getErrorMessage();
}
