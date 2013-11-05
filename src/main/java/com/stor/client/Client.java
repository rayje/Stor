package com.stor.client;

import com.stor.commands.Command;
import com.stor.commands.GetCommand;
import com.stor.commands.PutCommand;
import com.stor.commands.StatusCommand;
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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Client class is the implementation of the UI for
 * the Stor Server.
 *
 * The Client accepts the following commands:
 *      PUT: Puts a file into the P2P network.
 *      GET: Performs a lookup of a given file id.
 *      STATUS: Prints the status of the Stor server.
 */
public class Client {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 15080;
    private static final String PUTCMD = "PUT";
    private static final String GETCMD = "GET";
    private static final String STATUS = "STATUS";
    private static final List validCommands = Arrays.asList(new String[]{PUTCMD, GETCMD, STATUS});

    private static final Logger logger = Logger.getLogger(Client.class.getName());

    private Command command;

    public static void main(String[] args) {
        if (args.length <= 0) {
            quitOnError("default");
        }

        String cmd = args[0].toUpperCase();
        String cmdArg = null;

        if (args.length > 1) {
            cmdArg = args[1];
        }

        if (!validCommands.contains(cmd)) {
            System.err.println("INVALID CMD " + cmd);
            quitOnError("default");
        }

        if (cmd.equals(PUTCMD)) {
            try {
                cmdArg = resolveFilename(cmdArg);
            } catch (Exception e) {
                e.printStackTrace();
                quitOnError(e.getMessage());
            }
        }

        System.out.println("Command accepted: " + cmd + (cmdArg==null ? "" : " arg: " + cmdArg));

        try {
            new Client(cmd, cmdArg).run();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception thrown from Client", e);
            e.printStackTrace();
        }
    }

    /**
     * A Client that represents the UI for the P2P server implementation.
     *
     * @param cmd The String command to run.
     * @param cmdArg The argument associated with the cmd.
     */
    public Client(String cmd, String cmdArg) {
        if (cmd.equals(PUTCMD)) {
            this.command = new PutCommand(cmdArg);
        } else if (cmd.equals(GETCMD)) {
            this.command = new GetCommand(cmdArg);
        } else if (cmd.equals(STATUS)) {
            this.command = new StatusCommand();
        }
    }

    /**
     * Starts the client.
     *
     * @throws Exception If an error occurs while trying to execute the command.
     */
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

            ChannelFuture f = b.connect(HOST, PORT).sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    /**
     * Tries to ensure the file being PUT into the P2P network exists
     * and also tries to ensure the filename is an absolute path.
     *
     * @param cmdArg The argument provided with the PUT command.
     * @return A String representation of the fully qualifies path to the file.
     * @throws Exception If an error occurs while trying to get the path for the file.
     */
    private static String resolveFilename(String cmdArg) throws Exception {
        final File filePath = new File(cmdArg);
        String filename = cmdArg;

        if (!filePath.exists()) {
            quitOnError("File " + filePath.getAbsolutePath() + " does not exist!");
        }

        if (!filePath.isAbsolute()) {
            try {
                filename = filePath.getCanonicalPath();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Exception getting full path", e);
                throw e;
            }
        }

        return filename;
    }

    // private helper - quit and print error message
    private static void quitOnError(String error) {
        if (error.equals("default")) {
            StringBuilder builder = new StringBuilder()
                .append("Usage: Client <command> [options]\n")
                .append("  Commands:\n")
                .append("    PUT <filename>\n")
                .append("    GET <fileId>\n")
                .append("    STATUS\n");

            System.err.println(builder.toString());
        } else {
            System.err.println(error);
        }

        System.exit(1);
    }
}

