package com.stor.server;

import com.stor.commands.*;
import com.stor.p2p.IStorApplication;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import rice.p2p.commonapi.Id;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The ServerHandler to handle messages from the client.
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = Logger.getLogger(ServerHandler.class.getName());

    private IStorApplication application;

    public ServerHandler(IStorApplication application) {
        this.application = application;
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object command) throws Exception {
        CommandResult result;

        switch (((Command)command).getType()) {
            case GET:
                GetCommand getCommand = (GetCommand) command;
                logger.log(Level.INFO, "Server received GET command" + getCommand);
                String filePath = application.get(getCommand.getId());
                result = new GetCommandResult(ResultType.FAILURE, filePath);
                break;
            case PUT:
                logger.log(Level.INFO, "Server received PUT command");
                PutCommand putCommand = (PutCommand) command;
                Id fileId = application.put(putCommand.getFileName());
                result = new PutCommandResult(ResultType.FAILURE, fileId);
                break;
            default:
                result = null;
                logger.log(Level.INFO, "Unsupported command: " + ((Command)command).getType());
        }

        final ChannelFuture f = ctx.writeAndFlush(result);
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                ctx.close();
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.log(Level.WARNING, "Unexpected exception from downstream.", cause);
        ctx.close();
    }
}
