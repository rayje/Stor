package com.stor.p2p;

import com.stor.common.FileUtils;
import com.stor.common.StorException;
import rice.Continuation;
import rice.environment.Environment;
import rice.p2p.commonapi.Id;
import rice.p2p.past.Past;
import rice.p2p.past.PastContent;
import rice.p2p.past.PastImpl;
import rice.pastry.JoinFailedException;
import rice.pastry.PastryNode;
import rice.pastry.PastryNodeFactory;
import rice.pastry.commonapi.PastryIdFactory;
import rice.pastry.socket.SocketPastryNodeFactory;
import rice.pastry.standard.RandomNodeIdFactory;
import rice.persistence.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StorApplicationImpl implements StorApplication {
    private static final Logger logger = Logger.getLogger(StorApplicationImpl.class.getName());
    private static final Logger perfLogger = Logger.getLogger("performanceLog");

    private final Environment environment;

    //replica count
    private static final int REPLICA_COUNT = 5;
    //10M disk storage
    private static final long MAX_DISK_STORAGE_CAPACITY = 10 * 1024 * 1024;
    //default disk storage location
    private static final String DEFAULT_DISK_STORAGE_DIR = "Stor_Age/";
    //5M memory cache
    private static final int MAX_MEMORY_CACHE_CAPACITY = 5 * 1024 * 1024;
    private PastryNode node;
    private Past pastApp;
    private PastryIdFactory messageIdFactory;

    private long now() {
        return this.environment.getTimeSource().currentTimeMillis();
    }

    public StorApplicationImpl(int bindPort, InetSocketAddress bootAddress, final Environment environment) throws IOException, InterruptedException, JoinFailedException {
        this.environment = environment;

        logger.info("StorApplicationImpl - begin initialization");
        long begin = now();

        //generate a random node id
        PastryNodeFactory pastryNodeFactory = new SocketPastryNodeFactory(new RandomNodeIdFactory(environment), bindPort, environment);
        node = pastryNodeFactory.newNode();

        //this id generator is used for creating ids for messages
        messageIdFactory = new PastryIdFactory(node.getEnvironment());

        //storage
        Storage diskStorage = new PersistentStorage(messageIdFactory, DEFAULT_DISK_STORAGE_DIR, MAX_DISK_STORAGE_CAPACITY, node.getEnvironment());
        Cache inMemoryCache = new LRUCache(new MemoryStorage(messageIdFactory), MAX_MEMORY_CACHE_CAPACITY, node.getEnvironment());
        StorageManager storageManager = new StorageManagerImpl(messageIdFactory, diskStorage, inMemoryCache);

        //attach the Past application to the current node
        pastApp = new PastImpl(node, storageManager, REPLICA_COUNT, "");

        //boot node
        node.boot(bootAddress);
        perfLogger.log(Level.INFO, "Boot Complete: {0} ms", now() - begin);

        // the node may require sending several messages to fully boot into the ring
        synchronized (node) {
            while (!node.isReady()) {
                // abort if can't join
                if (node.joinFailed()) {
                    throw node.joinFailedReason();
                }

                // delay so we don't busy-wait
                node.wait(500);
            }
        }

        logger.info("StorApplicationImpl - initialization complete");
        perfLogger.log(Level.INFO, "Initialization completed: {0} ms", now() - begin);
    }

    public AppResponse put(String filePath) {
        logger.info("StorApplicationImpl - put");
        AppResponse<String> appResponse = new AppResponseImpl<>();
        try {
            byte[] fileContent = FileUtils.getFileData(filePath);
            if (fileContent == null) {
                throw new StorException("fileContent should not be null");
            }

            //Use the hash of the hostname and filePath for the Pastry Id.
            Id id = messageIdFactory.buildId(InetAddress.getLocalHost().getCanonicalHostName() + ":" + filePath);

            //save message
            StorMessage message = new StorMessage(id, fileContent);

            long begin = now();
            Continuation.ExternalContinuation<Boolean[], Exception> putContinuation = new Continuation.ExternalContinuation<>();

            //trigger past insert
            pastApp.insert(message, putContinuation);

            putContinuation.sleep();

            long duration = now() - begin;
            perfLogger.log(Level.INFO, "Insert Complete: {0}", duration);
            perfLogger.log(Level.INFO, "INSERT: Content: {0} bytes, Duration: {1} ms", new Object[]{fileContent.length, duration});

            if (putContinuation.exceptionThrown()) {
                throw putContinuation.getException();
            }

            logger.log(Level.INFO, "Successful PUT - {0}", id.toStringFull());
            appResponse.setResponse(id.toStringFull());
        } catch (Exception e) {
            logger.log(Level.WARNING, "", e);
            appResponse.setErrorMessage(e.getClass().getName() + ":" + e.getMessage());
        }

        return appResponse;
    }

    public AppResponse get(String fileId) {
        logger.info("StorApplicationImpl - get");
        AppResponse<String> appResponse = new AppResponseImpl<>();

        try {
            //generate a pastry content id from the given file id
            Id contentId = messageIdFactory.buildIdFromToString(fileId);

            /**
             * Continuation to use for handling lookup response. We will be using an ExternalContinuation type because it
             * permits us to sleep until content is ready to process
             */
            Continuation.ExternalContinuation<PastContent, Exception> getContinuation = new Continuation.ExternalContinuation<>();

            long begin = now();

            //trigger past lookup
            pastApp.lookup(contentId, getContinuation);

            //wait for the content to be ready.
            getContinuation.sleep();

            long duration = now() - begin;
            perfLogger.log(Level.INFO, "Lookup Complete: {0} ms", duration);

            /**
             * getContinuation sleep ended. check if the content is available and report back
             *
             * first we verify that an exception was not thrown while trying to retrieve content
             * then we obtain the result, check it matches what we need and process it
             */
            if (getContinuation.exceptionThrown()) {
                //an error occurred during lookup. for now we just log and respond with failure
                throw getContinuation.getException();
            }

            Object result = getContinuation.getResult();
            if (result == null) {
                //lookup failed
                throw new StorException("Lookup failed to locate content");
            }

            if (result instanceof StorMessage) {
                //a valid, non empty message was received. try to save it
                StorMessage message = (StorMessage) result;
                Path newFilePath = FileUtils.putFileData(message.getFileContent());
                if (newFilePath == null) {
                    throw new StorException("Failed to save file");
                } else {
                    logger.info("Located content store in: " + newFilePath.toString());
                    perfLogger.log(Level.INFO, "LOOKUP: Content: {0} bytes, Duration: {1} ms", new Object[]{message.getFileContent().length, duration});
                    appResponse.setResponse(newFilePath.toString());
                }
            } else {
                //unexpected result type
                throw new StorException("Unknown result - " + result.getClass().getName());
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "", e);
            appResponse.setErrorMessage(e.getClass().getName() + ":" + e.getMessage());
        }

        return appResponse;
    }

    @Override
    public AppResponse getStatus() {
        AppResponse<String> appResponse = new AppResponseImpl<>();

        /*
         * Add a printable version of the RoutingTable to the response.
         */
        appResponse.setResponse(node.getRoutingTable().printSelf());
        return appResponse;
    }
}
