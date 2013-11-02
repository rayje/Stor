package com.stor.client;

import com.stor.commands.Command;
import com.stor.commands.GetCommand;
import com.stor.commands.PutCommand;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import java.nio.file.*;
import java.io.File;
import java.io.IOException;

/**
 * Stor P2P Project
 * @param host hostname or host IP address
 * @param port
 * @param command
 * @param PUTCMD "PUT"
 * @param GETCMD "GET"
 * @param NUMARGS number of arguments expected from the main() method
 */


public class Client {

    private final String host;
    private final int port;
    private Command command;
    private static final String PUTCMD = "PUT";
    private static final String GETCMD = "GET";

    private static final int NUMARGS = 2; //expected number of arguments

    public static void main(String[] args) throws Exception {

        if (args.length != NUMARGS) QuitOnError("default");

        final String host = "127.0.0.1";
        final int port = 15080;
        final String cmd = args[0].toUpperCase();
        String fileName = args[1];

        if (!cmd.equals(PUTCMD) && !cmd.equals(GETCMD)) {
            QuitOnError("default");
        }

        if (cmd.equals(PUTCMD))
        {
            final File filePath = new File(fileName);
            if (!filePath.exists()) QuitOnError("File does not exist!");
            fileName = filePath.toString();

            if (!filePath.isAbsolute())
            {
                try {
                    fileName = filePath.getCanonicalPath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        System.out.println("Command accepted: " + cmd + " file: " + fileName);
        new Client(host, port, cmd, fileName).run();
    }

    public Client(String host, int port, String cmd, String fName) {
        this.host = host;
        this.port = port;

        if (cmd.equals(PUTCMD)) {
            this.command = new PutCommand(fName);
        } else if (cmd.equals(GETCMD)) {
            this.command = new GetCommand(fName);
        }
    }

    public void run() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new ClientHandler(command));
                        }
                    });

            ChannelFuture f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    // private helper - quit and print error message
    private static void QuitOnError(String error) {

        if (error.equals("default")) System.err.println("Usage: Client <PUT> <fileName> or <GET> <fileId>");
        else System.err.println(error);

        System.exit(1);
    }
}

