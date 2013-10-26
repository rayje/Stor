package com.stor.commands;

import java.io.Serializable;

/**
 * User: rayje
 * Date: 10/14/13
 * Time: 9:47 PM
 */
public abstract class CommandResult implements Result, Serializable {

    private final ResultType type;

    public CommandResult(ResultType type) {
        this.type = type;
    }

    @Override
    public ResultType getResultType() {
        return type;
    }
}
