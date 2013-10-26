package com.stor.commands;

import rice.p2p.commonapi.Id;

public class PutCommandResult extends CommandResult implements PutResult {
    private Id fileId;

    public PutCommandResult(ResultType type, Id fileId) {
        super(type);
        this.fileId = fileId;
    }

    @Override
    public Id getFileId() {
        return this.fileId;
    }
}
