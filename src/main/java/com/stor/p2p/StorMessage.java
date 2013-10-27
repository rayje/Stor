package com.stor.p2p;

import rice.p2p.commonapi.Id;
import rice.p2p.past.ContentHashPastContent;

/**
 * StorMessage - is a wrapper around ContentHashPastContent which permits us to store data using PAST.
 * fileContent is stored as a byte[].
 */
public class StorMessage extends ContentHashPastContent {
    private byte[] fileContent;

    public StorMessage(Id contentId, byte[] fileContent) {
        super(contentId);
        this.fileContent = fileContent;
    }

    /**
     * @return byte[] - content of the file to store
     */
    public byte[] getFileContent()
    {
        return this.fileContent;
    }

    public String toString() {
        return StorMessage.class.getName() + "[" + getId() + "]";
    }
}
