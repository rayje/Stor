package com.stor.client;

import com.stor.commands.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        logger.log(Level.INFO, "" + result);

        if (result instanceof CommandResult) {
            CommandResult commandResult = (CommandResult) result;
            switch (command.getType()) {
                case GET:
                    handleGetResponse(commandResult);
                    break;
                case PUT:
                    handlePutResponse(commandResult);
                    break;
                default:
                    logger.warning("Unexpected Command: " + command);
            }
        } else {
            logger.warning("Unknown response for a command from the server");
        }

        ctx.close();
    }

    private void handlePutResponse(CommandResult commandResult) {
        logger.info("handlePutResponse is not implemented yet. Result: " + commandResult);
    }

    private void handleGetResponse(CommandResult commandResult) throws IOException {
        logger.info("handleGetResponse");

        if (commandResult.getResultType() == ResultType.FAILURE) {
            logger.info("Failed to complete the get command: " + commandResult.getErrorMessage());
        } else {
            final Path serverFilePath = Paths.get((String) commandResult.getResult());
            logger.info("File saved to: " + serverFilePath);

            if (!serverFilePath.isAbsolute()) {
                logger.severe("Server must return absolute file path");
            } else {
                Files.move(serverFilePath, serverFilePath.getFileName());
                logger.info("File moved successfully: From: " + serverFilePath + "To: " + serverFilePath.getFileName().toAbsolutePath());
            }
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.log(Level.WARNING, "Unexpected exception from downstream.", cause);
        ctx.close();
    }

    public void messageReceived(ChannelHandlerContext ctx, CommandResult e) {
        ResultType result = e.getResultType();

        if (result.toString().equals("SUCCESS")) System.out.println("Command success.");
        else System.out.println("Command faied.");
    }
}
