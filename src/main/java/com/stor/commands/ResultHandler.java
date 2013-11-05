package com.stor.commands;

/**
 * An interface that defines the common methods that
 * can be used to handle the results returned from the server.
 */
public interface ResultHandler {

    /**
     * The method to be called to handle the processing
     * of a successful {@link CommandResult} object.
     *
     * @param commandResult An instance of a {@link CommandResult}.
     */
    void handleSuccess(CommandResult commandResult);

    /**
     * The method to be called to handle the processing
     * of a failure {@link CommandResult} object.
     *
     * @param commandResult An instance of a {@link CommandResult}.
     */
    void handleFailure(CommandResult commandResult);

    void setCommand(Command command);

}
