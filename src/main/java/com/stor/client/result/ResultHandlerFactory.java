package com.stor.client.result;

import com.stor.commands.CommandType;
import com.stor.commands.ResultHandler;

/**
 * A factory class used to generate the handler capable of handling the
 * specific type of result returned by the server.
 */
public class ResultHandlerFactory {

    /**
     * A getter method to retrieve a ResultHandler instance capable of
     * processing the result associated with the {@link CommandType}.
     *
     * @param type An instance of a {@link CommandType}.
     * @return An instance of a ResultHandler if one is found that is capable
     * of handling the {@link CommandType}, null otherwise.
     */
    public static ResultHandler getResultHandler(CommandType type) {
        switch (type) {
            case PUT:
                return new PutResultHandler();
            case GET:
                return new GetResultHandler();
            case STATUS:
                return new StatusResultHandler();
            default:
                return null;
        }
    }

}
