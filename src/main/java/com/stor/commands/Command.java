package com.stor.commands;

/**
 * The Command interface defines the common methods all
 * Command classes are expected to implement. The methods
 * in the Command interface define the public methods to interact
 * with a Command class.
 *
 * Command specific methods should be defined in implementation
 * of the Command interface.
 *
 * Commands should use CommandType enums to define their types. This
 * should assist in deciding how to process the command.
 */
public interface Command {

    /**
     * Gets the type of Command from the implementation.
     *
     * @return An instance of a CommandType.
     * @see CommandType
     */
    CommandType getType();

}
