package com.stor.p2p;

import com.stor.common.FileUtils;
import rice.Continuation;
import rice.environment.Environment;
import rice.p2p.commonapi.Id;
import rice.p2p.past.Past;
import rice.p2p.past.PastContent;
import rice.p2p.past.PastImpl;
import rice.pastry.PastryNode;
import rice.pastry.PastryNodeFactory;
import rice.pastry.commonapi.PastryIdFactory;
import rice.pastry.socket.SocketPastryNodeFactory;
import rice.pastry.standard.RandomNodeIdFactory;
import rice.persistence.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StorApplication implements IStorApplication {
    private static final Logger logger = Logger.getLogger(StorApplication.class.getName());

    //10M disk storage
    private static final long MAX_DISK_STORAGE_CAPACITY = 10 * 1024 * 1024;
    //default disk storage location
    private static final String DEFAULT_DISK_STORAGE_DIR = "Stor_Age/";
    //5M memory cache
    private static final int MAX_MEMORY_CACHE_CAPACITY = 5 * 1024 * 1024;
    private PastryNode node;
    private Past pastApp;
    private PastryIdFactory messageIdFactory;


    public StorApplication(int bindPort, InetSocketAddress bootAddress, final Environment environment) throws IOException, InterruptedException {
        logger.info("StorApplication - begin initialization");

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
        pastApp = new PastImpl(node, storageManager, 0, "");

        //boot node
        node.boot(bootAddress);

        // the node may require sending several messages to fully boot into the ring
        synchronized (node) {
            while (!node.isReady()) {
                // abort if can't join
                if (node.joinFailed()) {
                    throw new IOException("Could not join the FreePastry ring.  Reason:" + node.joinFailedReason());
                }

                // delay so we don't busy-wait
                node.wait(500);

            }
        }

        logger.info("StorApplication - initialization complete");
    }

    public String put(String filePath) {
        logger.info("StorApplication - put");

        byte[] fileContent = FileUtils.getFileData(filePath);
        if (fileContent == null) {
            //failed to fetch file content to store on the network
            logger.warning("Failed to put fileContent.");
            return null;
        } else {
            Id id = messageIdFactory.buildId(fileContent);

            //save message
            StorMessage message = new StorMessage(id, fileContent);

            //trigger past insert
            pastApp.insert(message, new Continuation<Boolean[], Exception>() {
                @Override
                public void receiveResult(Boolean[] results) {
                    logger.log(Level.INFO, "insert.receiveResult returned {0} results", results.length);
                }

                @Override
                public void receiveException(Exception e) {
                    logger.log(Level.WARNING, "insert.receiveException", e);
                }
            });

            logger.log(Level.INFO, "Attempting to put fileContent - {0}", id.toStringFull());
            return id.toStringFull();
        }
    }

    public String get(String fileId) {
        logger.info("StorApplication - get");

        //generate a pastry content id from the given file id
        Id contentId = messageIdFactory.buildIdFromToString(fileId);

        /**
         * Continuation to use for handling lookup response. We will be using an ExternalContinuation type because it
         * permits us to sleep until content is ready to process
         */
        Continuation.ExternalContinuation<PastContent, Exception> getContinuation = new Continuation.ExternalContinuation<PastContent, Exception>();

        //trigger past lookup
        pastApp.lookup(contentId, getContinuation);

        //wait for the content to be ready.
        getContinuation.sleep();

        /**
         * getContinuation sleep ended. check if the content is available and report back
         *
         * first we verify that an exception was not thrown while trying to retrieve content
         * then we obtain the result, check it matches what we need and process it
         */
        if (getContinuation.exceptionThrown()) {
            //an error occurred during lookup. for now we just log and respond with failure
            logger.log(Level.SEVERE, "Get Failed", getContinuation.getException());
            return null;
        } else {
            Object result = getContinuation.getResult();
            if (result == null) {
                //lookup failed
                logger.severe("Lookup failed to locate content");
                return null;
            } else if (result instanceof StorMessage) {
                //a valid, non empty message was received. try to save it
                StorMessage message = (StorMessage) result;
                Path newFilePath = FileUtils.putFileData(message.getFileContent());
                if (newFilePath == null) {
                    logger.severe("Failed to save file");
                    return null;
                } else {
                    logger.info("Located content store in: " + newFilePath.toString());
                    return newFilePath.toString();
                }
            } else {
                //unexpected result type
                logger.severe("Unknown result - " + result.getClass().getName());
                return null;
            }
        }
    }
}
