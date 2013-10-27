package com.stor.commands;

import java.io.Serializable;

/**
 * PutCommand - represents a command sent from the Client to the Server.
 * Parameter filePath identifies the file to save.
 */
public class PutCommand implements Command, Serializable {

    private CommandType type = CommandType.PUT;

    private final String filePath;

    public PutCommand(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public CommandType getType() {
        return type;
    }

    public String toString() {
        return "PutCommand [" + filePath + "]";
    }
}
