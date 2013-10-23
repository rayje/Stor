package com.stor.p2p;

import rice.environment.Environment;
import rice.p2p.commonapi.Id;
import rice.p2p.past.Past;
import rice.p2p.past.PastImpl;
import rice.pastry.PastryNode;
import rice.pastry.PastryNodeFactory;
import rice.pastry.commonapi.PastryIdFactory;
import rice.pastry.socket.SocketPastryNodeFactory;
import rice.pastry.standard.RandomNodeIdFactory;
import rice.persistence.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class StorApplication implements IStorApplication
{
    private static final Logger logger = Logger.getLogger(StorApplication.class.getName());

    //stor port
    private static final int STOR_PORT = 17373;
    //10M disk storage
    private static final long MAX_DISK_STORAGE_CAPACITY = 10 * 1024 * 1024;
    //default disk storage location
    private static final String DEFAULT_DISK_STORAGE_DIR = "Stor_Age/";
    //5M memory cache
    private static final int MAX_MEMORY_CACHE_CAPACITY = 5 * 1024 * 1024;
    private PastryNode node;
    private Past pastApp;

    public StorApplication(int bindPort, final Environment environment) throws IOException, InterruptedException {
        logger.info("StorApplication - begin initialization");

        //attach the node to the specified application boot port
        InetAddress address = InetAddress.getLocalHost();
        InetSocketAddress thisHostSocket = new InetSocketAddress(address, STOR_PORT);

        //generate a random node id
        PastryNodeFactory pastryNodeFactory = new SocketPastryNodeFactory(new RandomNodeIdFactory(environment), STOR_PORT, environment);
        node = pastryNodeFactory.newNode();

        //this id generator is used for creating ids for messages
        PastryIdFactory messageIdFactory = new PastryIdFactory(node.getEnvironment());

        //storage
        Storage diskStorage = new PersistentStorage(messageIdFactory, DEFAULT_DISK_STORAGE_DIR, MAX_DISK_STORAGE_CAPACITY, node.getEnvironment());
        Cache inMemoryCache = new LRUCache(new MemoryStorage(messageIdFactory), MAX_MEMORY_CACHE_CAPACITY, node.getEnvironment());
        StorageManager storageManager = new StorageManagerImpl(messageIdFactory, diskStorage, inMemoryCache);

        //attach the Past application to the current node
        pastApp = new PastImpl(node, storageManager, 0, "");

        //boot node
        node.boot(thisHostSocket);

        // the node may require sending several messages to fully boot into the ring
        synchronized (node) {
            while (!node.isReady() && !node.joinFailed()) {
                // delay so we don't busy-wait
                node.wait(500);

                // abort if can't join
                if (node.joinFailed()) {
                    throw new IOException("Could not join the FreePastry ring.  Reason:" + node.joinFailedReason());
                }
            }
        }

        logger.info("StorApplication - initialization complete");
    }

    public Id put(String filePath) {
        logger.info("put");
        return null;
    }

    public String get(Id fileId) {
        logger.info("get");
        return null;
    }
}
