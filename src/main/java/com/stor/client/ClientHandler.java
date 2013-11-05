package com.stor.client;


import com.stor.client.result.ResultHandlerFactory;
import com.stor.commands.Command;
import com.stor.commands.CommandResult;
import com.stor.commands.ResultHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    private final Command command;

    public ClientHandler(Command command) {
        this.command = command;
    }

    public void channelActive(ChannelHandlerContext ctx) {
        logger.log(Level.INFO, "Sending Command: " + command);
        ctx.writeAndFlush(command);
    }

    public void channelRead(ChannelHandlerContext ctx, Object result) throws Exception {
        logger.log(Level.INFO, "channelRead: " + command.getType());

        CommandResult commandResult = (CommandResult) result;
        ResultHandler resultHandler = ResultHandlerFactory.getResultHandler(command.getType());

        if (resultHandler != null) {
            logger.log(Level.INFO, resultHandler.getClass().getName());

            resultHandler.setCommand(command);
            switch(commandResult.getResultType()) {
                case SUCCESS:
                    resultHandler.handleSuccess(commandResult);
                    break;
                case FAILURE:
                    resultHandler.handleFailure(commandResult);
                    break;
                default:
                    logger.warning(commandResult.getResultType() + " is not a recognized result type.");
                    break;
            }
        } else {
            logger.warning("No handler configured for: " + command.getType());
        }

        ctx.close();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.log(Level.WARNING, "Unexpected exception from downstream.", cause);
        ctx.close();
    }
}
