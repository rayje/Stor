package com.stor.client.result;


import com.stor.commands.Command;
import com.stor.commands.CommandResult;
import com.stor.commands.ResultHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A ResultHandler to handle the result of a GET request.
 *
 */
public class GetResultHandler implements ResultHandler {

    private static final Logger logger = Logger.getLogger(GetResultHandler.class.getName());

    private Command command;

    private String classname = GetResultHandler.class.getName();

    @Override
    public void handleSuccess(CommandResult commandResult) {
        logger.entering(classname, "handleSuccess");

        final Path serverFilePath = Paths.get((String) commandResult.getResult());
        logger.info("File saved to: " + serverFilePath);

        if (!serverFilePath.isAbsolute()) {
            logger.severe("Server must return absolute file path");
            return;
        }

        try {
            Files.move(serverFilePath, serverFilePath.getFileName());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Exception while moving file: " + serverFilePath, e);
            return;
        }

        logger.info("File moved successfully: From: " + serverFilePath + "To: " + serverFilePath.getFileName().toAbsolutePath());
    }

    @Override
    public void handleFailure(CommandResult commandResult) {
        logger.entering(classname, "handleFailure");

        logger.info("Failed to complete the get command: " + commandResult.getErrorMessage());
        System.err.println(commandResult.getErrorMessage());
    }

    @Override
    public void setCommand(Command command) {
        this.command = command;
    }
}
