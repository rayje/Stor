package com.stor.p2p;

public interface StorApplication {
    /**
     * put - given a file path, stores it into the p2p system
     * @param filePath - absolute file path (including name) of the file
     * @return String - pastry content Id
     */
    public AppResponse put(String filePath);

    /**
     * get - given an id, retrieves it from the p2p system (if possible)
     * @param fileId - pastry content Id
     * @return String - absolute file path (including name) of the retrieved file location
     */
    public AppResponse get(String fileId);
}
