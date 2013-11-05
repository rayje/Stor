package com.stor.client.result;

import com.stor.commands.Command;
import com.stor.commands.CommandResult;
import com.stor.commands.ResultHandler;

import java.util.logging.Logger;

/**
 * An implementation of the ResultHandler interface.
 *
 * The StatusResultHandler is used to report the status of the
 * server as a result of the {@link com.stor.commands.StatusCommand}
 */
public class StatusResultHandler implements ResultHandler {

    private static final Logger logger = Logger.getLogger(StatusResultHandler.class.getName());

    private Command command;

    private String classname = StatusResultHandler.class.getName();

    @Override
    public void handleSuccess(CommandResult commandResult) {
        logger.entering(classname, "handleSuccess");

        System.out.println(commandResult.getResult());
    }

    @Override
    public void handleFailure(CommandResult commandResult) {
        logger.entering(classname, "handleFailure");

        logger.info("Failed to complete the status command: " + commandResult.getErrorMessage());
        System.err.println(commandResult.getErrorMessage());
    }

    @Override
    public void setCommand(Command command) {
        this.command = command;
    }
}
