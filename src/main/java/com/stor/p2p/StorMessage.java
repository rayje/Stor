package com.stor.p2p;

import rice.p2p.commonapi.Id;
import rice.p2p.past.ContentHashPastContent;

public class StorMessage extends ContentHashPastContent {
    byte[] fileContent;

    public StorMessage(Id contentId, byte[] fileContent) {
        super(contentId);
        this.fileContent = fileContent;
    }

    public String toString() {
        return StorMessage.class.getName() + "[" + getId() + "]";
    }
}
