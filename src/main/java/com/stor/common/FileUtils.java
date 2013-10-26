package com.stor.common;

import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUtils {

    private static final Logger logger = Logger.getLogger(FileUtils.class.getName());

    private static final FileAttribute<Set<PosixFilePermission>> ownerReadWriteAttributes = PosixFilePermissions.asFileAttribute(
            PosixFilePermissions.fromString("rw-------")
    );

    /**
     * Fetches the content of the provided filePath
     * @param filePath - absolute file path
     * @return byte[]
     *
     * null indicates failure
     */
    public static byte[] getFileData(String filePath) {
        try {
            Path path = Paths.get(filePath);
            logger.info("getFileData attempt to read content from: " + path.toAbsolutePath());
            return Files.readAllBytes(path);
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Exception", ex);
            return null;
        }
    }

    /**
     * Stores provided byte[] into a temporary file
     * @param fileData byte content fo the file
     * @return Path
     *
     * null indicates failure
     */
    public static Path putFileData(byte[] fileData)
    {
        try
        {
            Path tempFile = Files.createTempFile("putFileData_", null, ownerReadWriteAttributes);
            logger.info("putFileData attempt to write content to: " + tempFile.toAbsolutePath());
            Files.write(tempFile, fileData, StandardOpenOption.CREATE);
            return tempFile;
        }
        catch(Exception ex)
        {
            logger.log(Level.WARNING, "Exception", ex);
            return null;
        }
    }
}
