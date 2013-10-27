package com.stor.commands;

/**
 * GetCommand - represents requests sent by the client to the server
 * to initiate a content lookup request.
 * The fileId is the identifier which the server will use to perform the lookup
 *
 * @see PutCommandResult
 */
public class GetCommand implements Command {

    private final CommandType type = CommandType.GET;

    private final String fileId;

    public GetCommand(String fileId) {
        this.fileId = fileId;
    }

    public String getFileId() {
        return fileId;
    }

    @Override
    public CommandType getType() {
        return type;
    }
}
