package com.stor.commands;

import java.io.Serializable;

/**
 * CommandResult - an implementation of the Result interface.
 * @see Result
 */
public class CommandResult<T> implements Result<T>, Serializable {
    private final ResultType resultType;
    private T result;
    String errorMessage;

    public CommandResult(ResultType resultType, T result, String errorMessage) {
        this.resultType = resultType;
        this.result = result;
        this.errorMessage = errorMessage;
    }

    @Override
    public ResultType getResultType() {
        return resultType;
    }

    @Override
    public T getResult() {
        return result;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    public String toString() {
        return String.format("CommandResult[resultType=%s, result=%s, errorMessage=%s]", getResultType(), (getResult() != null) ? getResult().toString() : null, getErrorMessage());
    }
}
