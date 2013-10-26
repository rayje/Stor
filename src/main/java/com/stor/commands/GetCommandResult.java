package com.stor.commands;

public class GetCommandResult extends CommandResult implements GetResult {
    private String filePath;

    public GetCommandResult(ResultType type, String filePath) {
        super(type);
        this.filePath = filePath;
    }

    @Override
    public String getFilePath() {
        return this.filePath;
    }
}
