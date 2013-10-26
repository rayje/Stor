package com.stor.commands;

import rice.p2p.commonapi.Id;

/**
 * User: rayje
 * Date: 10/14/13
 * Time: 9:34 PM
 */
public class GetCommand implements Command {

    private final CommandType type = CommandType.GET;

    private final Id fileId;

    public GetCommand(Id fileId) {
        this.fileId = fileId;
    }

    public Id getId() {
        return fileId;
    }

    @Override
    public CommandType getType() {
        return type;
    }
}
