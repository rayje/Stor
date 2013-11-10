package com.stor.common;

import java.io.IOException;
import java.util.logging.FileHandler;

/**
 * Handler for capturing performance related log entries
 */
public class PerformanceLogHandler extends FileHandler {
    public PerformanceLogHandler() throws IOException, SecurityException {
    }

    public PerformanceLogHandler(String s) throws IOException, SecurityException {
        super(s);
    }

    public PerformanceLogHandler(String s, boolean b) throws IOException, SecurityException {
        super(s, b);
    }

    public PerformanceLogHandler(String s, int i, int i2) throws IOException, SecurityException {
        super(s, i, i2);
    }

    public PerformanceLogHandler(String s, int i, int i2, boolean b) throws IOException, SecurityException {
        super(s, i, i2, b);
    }
}
