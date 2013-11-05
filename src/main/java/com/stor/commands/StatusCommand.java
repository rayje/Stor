package com.stor.commands;

import java.io.Serializable;

/**
 * An instance of the Command interface used to query the status
 * of the server.
 */
public class StatusCommand implements Command, Serializable {

    public CommandType getType() {
        return CommandType.STATUS;
    }

    public String toString() {
        return "Status Command";
    }
}
