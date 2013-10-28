package com.stor.client;

import com.stor.commands.*;
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
        if (command.getType() == CommandType.PUT) {
            logger.log(Level.INFO, "" + (PutCommandResult) result);
        } else if (command.getType() == CommandType.GET) {
            logger.log(Level.INFO, "" + (GetCommandResult) result);
        } else {
            logger.log(Level.INFO, "" + (Result) result);
        }
        ctx.close();
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
