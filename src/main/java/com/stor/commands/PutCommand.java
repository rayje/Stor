package com.stor.commands;

import java.io.Serializable;

/**
 * An implementation of the Put command.
 */
public class PutCommand implements Command, Serializable {

    private CommandType type = CommandType.PUT;

    private final String fileName;

    public PutCommand(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public CommandType getType() {
        return type;
    }

}
