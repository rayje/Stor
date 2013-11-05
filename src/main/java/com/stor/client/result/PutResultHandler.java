package com.stor.client.result;

import com.stor.commands.Command;
import com.stor.commands.CommandResult;
import com.stor.commands.ResultHandler;

import java.util.logging.Logger;

/**
 * User: rayje
 * Date: 11/4/13
 * Time: 7:00 PM
 */
public class PutResultHandler implements ResultHandler {

    private static final Logger logger = Logger.getLogger(PutResultHandler.class.getName());

    private Command command;

    private String classname = PutResultHandler.class.getName();

    @Override
    public void handleSuccess(CommandResult commandResult) {
        logger.entering(classname, "handleSuccess");

        logger.info("PutResult: " + commandResult);
        System.out.println(commandResult.getResult());
    }

    @Override
    public void handleFailure(CommandResult commandResult) {
        logger.entering(classname, "handleFailure");

        logger.info("PutResult Error: " + commandResult.getErrorMessage());
        System.err.println(commandResult.getErrorMessage());
    }

    @Override
    public void setCommand(Command command) {
        this.command = command;
    }
}
