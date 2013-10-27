package com.stor.commands;

/**
 * PutCommandResult - represents information returned by the Server
 * to the Client when a PetCommand is handled by the Server
 *
 * As part of handling a PutCommand, the Server generates a unique id
 * for the content to be stored. This value is represented by the fileId.
 *
 * Client is expected to save the fileId's returned for a PutCommand.
 * The fileId value will be used to perform a GetCommand to locate and retrieve stored content.
 */
public class PutCommandResult extends CommandResult {
    private final String fileId;

    public PutCommandResult(ResultType resultType, String fileId) {
        super(resultType);
        this.fileId = fileId;
    }

    /**
     * getFileId - returns the unique id for the content stored
     * @return String
     */
    public String getFileId()
    {
        return this.fileId;
    }
}
