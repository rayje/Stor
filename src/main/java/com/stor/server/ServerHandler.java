package com.stor.server;

import com.stor.commands.Command;
import com.stor.commands.CommandResult;
import com.stor.commands.Result;
import com.stor.commands.ResultType;
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

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object command) throws Exception {
        logger.log(Level.INFO, "CommandType: " + ((Command) command).getType());

        Result result = new CommandResult(ResultType.SUCCESS);
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
