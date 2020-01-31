package com.example.statemachine2.util.http.client;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdleConnectionMonitorThread extends Thread {

    private static Logger LOGGER = LoggerFactory.getLogger(IdleConnectionMonitorThread.class);


    // The manager to watch.
    private final PoolingHttpClientConnectionManager cm;
    // Use a BlockingQueue to stop everything.
    private final BlockingQueue<Stop> stopSignal = new ArrayBlockingQueue<Stop>(1);

    private CloseableHttpClient httpClient;


    public IdleConnectionMonitorThread(PoolingHttpClientConnectionManager cm,CloseableHttpClient httpClient) {
        super();
        this.cm = cm;
        this.httpClient=httpClient;
    }

    // Pushed up the queue.
    private static class Stop {
        // The return queue.
        private final BlockingQueue<Stop> stop = new ArrayBlockingQueue<Stop>(1);

        // Called by the process that is being told to stop.
        public void stopped() {
            // Push me back up the queue to indicate we are now stopped.
            stop.add(this);
        }

        // Called by the process requesting the stop.
        public void waitForStopped() throws InterruptedException {
            // Wait until the callee acknowledges that it has stopped.
            stop.take();
        }
    }

    @Override
    public void run() {
        try {
            // Holds the stop request that stopped the process.
            Stop stopRequest;
            // Every 5 seconds.
            while ((stopRequest = stopSignal.poll(5, TimeUnit.SECONDS)) == null) {
                LOGGER.trace("looking for idle/stalled cnx for cm [{}]",this.cm);
                // Close expired connections
                cm.closeExpiredConnections();
                // Optionally, close connections that have been idle too long.
                cm.closeIdleConnections(60, TimeUnit.SECONDS);
                // Look at pool stats.
                LOGGER.trace("Stats: {}", cm.getTotalStats());
            }
            // Acknowledge the stop request.
            stopRequest.stopped();
        } catch (InterruptedException ex) {
            // terminate
        }
    }

    public void shutdown() throws InterruptedException, IOException {
        LOGGER.info("Shutting down client pool [{}]",this.httpClient);
        // Signal the stop to the thread.
        Stop stop = new Stop();
        stopSignal.add(stop);
        // Wait for the stop to complete.
        stop.waitForStopped();
        // Close the pool - Added
        httpClient.close();
        // Close the connection manager.
        cm.close();
        LOGGER.info("Client pool shut down [{}]",this.httpClient);
    }

}
