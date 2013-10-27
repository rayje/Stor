package com.stor.commands;

import java.io.Serializable;

/**
 * CommandResult - an implementation of the Result interface.
 * @see Result
 *
 * This class is implemented as an abstract class to prevent its direct use
 * @see GetCommandResult, PutCommandReult
 *
 */
public abstract class CommandResult implements Result, Serializable {

    private final ResultType resultType;

    public CommandResult(ResultType resultType) {
        this.resultType = resultType;
    }

    @Override
    public ResultType getResultType() {
        return resultType;
    }
}
