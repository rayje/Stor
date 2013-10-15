package com.stor.client;

import com.stor.commands.Command;
import com.stor.commands.Result;
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
        logger.log(Level.INFO, "Sending Command: " + command.getType());
        ctx.writeAndFlush(command);
    }

    public void channelRead(ChannelHandlerContext ctx, Object result) throws Exception {
        logger.log(Level.INFO, "Result: " + ((Result) result));

        ctx.close();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.log(Level.WARNING, "Unexpected exception from downstream.", cause);
        ctx.close();
    }
}
