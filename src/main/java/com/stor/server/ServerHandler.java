package com.stor.server;

import com.stor.commands.*;
import com.stor.p2p.AppResponse;
import com.stor.p2p.AppResponseImpl;
import com.stor.p2p.StorApplication;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The ServerHandler to handle messages from the client.
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = Logger.getLogger(ServerHandler.class.getName());

    private StorApplication application;

    public ServerHandler(StorApplication application) {
        this.application = application;
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object command) throws Exception {
        AppResponse response;

        switch (((Command) command).getType()) {
            case GET:
                GetCommand getCommand = (GetCommand) command;
                logger.log(Level.INFO, "Server received GET command" + getCommand);
                response = application.get(getCommand.getFileId());
                break;
            case PUT:
                PutCommand putCommand = (PutCommand) command;
                logger.log(Level.INFO, "Server received PUT command" + putCommand);
                response = application.put(putCommand.getFilePath());
                break;
            default:
                response = new AppResponseImpl<Boolean>();
                response.setErrorMessage("Unsupported command: " + ((Command) command).getType());
        }

        mapAndSendResponse(ctx, response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.log(Level.WARNING, "Unexpected exception from downstream.", cause);
        ctx.close();
    }

    private void mapAndSendResponse(final ChannelHandlerContext ctx, AppResponse appResponse) {
        CommandResult commandResult;
        if (appResponse == null) {
            commandResult = new CommandResult<>(ResultType.FAILURE, Boolean.FALSE, "Unknown error");
        } else {
            commandResult = new CommandResult<>(
                    appResponse.hasError() ? ResultType.FAILURE : ResultType.SUCCESS,
                    appResponse.getResponse(),
                    appResponse.getErrorMessage()
            );
        }

        final ChannelFuture f = ctx.writeAndFlush(commandResult);
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                ctx.close();
            }
        });
    }
}
