package com.stor.commands;

import java.io.Serializable;

/**
 * GetCommandResult -represents information returned by the Server
 * to the Client when a GetCommand is handled by the Server
 *
 * As part of completing the GetCommand, the Server performs a lookup of the requested
 * content on the network and stores the content on the local filesystem.
 * filePath represents the location (absolute path) of the retrieved/stored content
 */
public class GetCommandResult extends CommandResult implements Serializable {
    private final String filePath;

    public GetCommandResult(ResultType resultType, String filePath) {
        super(resultType);
        this.filePath = filePath;
    }

    /**
     * getFilePath - returns the absolute path where the content retrieved
     * by the Server is stored.
     * @return String
     */
    public String getFilePath()
    {
       return filePath;
    }

    public String toString() {
        return "GetCommandResult [" + filePath + "]";
    }
}
