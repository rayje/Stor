package com.stor.common;

import java.io.IOException;
import java.nio.file.*;
import java.util.logging.Logger;

public class FileUtils {

    private static final Logger logger = Logger.getLogger(FileUtils.class.getName());

    /**
     * Fetches the content of the provided filePath
     * @param filePath - absolute file path
     * @return byte[]
     *
     * null indicates failure
     */
    public static byte[] getFileData(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        logger.info("getFileData attempt to read content from: " + path.toAbsolutePath());
        return Files.readAllBytes(path);
    }

    /**
     * Stores provided byte[] into a temporary file
     * @param fileData byte content fo the file
     * @return Path
     *
     * null indicates failure
     */
    public static Path putFileData(byte[] fileData) throws IOException {
        Path tempFile = Files.createTempFile("putFileData_", null);
        logger.info("putFileData attempt to write content to: " + tempFile.toAbsolutePath());
        Files.write(tempFile, fileData, StandardOpenOption.CREATE);
        return tempFile;

    }
}
