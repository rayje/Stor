package com.stor.commands;

/**
 * User: rayje
 * Date: 10/14/13
 * Time: 9:34 PM
 */
public class GetCommand implements Command {

    private final CommandType type = CommandType.GET;

    private final String fileName;

    public GetCommand(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public CommandType getType() {
        return type;
    }
}
