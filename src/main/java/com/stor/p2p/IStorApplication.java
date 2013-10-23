package com.stor.p2p;

import rice.p2p.commonapi.Id;

public interface IStorApplication {
    /**
     * put - given a file path, stores it into the p2p system
     * @param filePath - absolute file path (including name) of the file
     * @return Id - pastry content Id
     */
    public Id put(String filePath);

    /**
     * get - given an id, retrieves it from the p2p system (if possible)
     * @param fileId - pastry content Id
     * @return String - absolute file path (including name) of the retrieved file location
     */
    public String get(Id fileId);
}
