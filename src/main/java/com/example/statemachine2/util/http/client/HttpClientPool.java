package com.example.statemachine2.util.http.client;

import java.util.concurrent.TimeUnit;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientPool {

    private static Logger LOGGER = LoggerFactory.getLogger(HttpClientPool.class);

    private PoolingHttpClientConnectionManager cm;
    private CloseableHttpClient httpClient;

    public   HttpClientPool(Builder builder) {
        this.cm= new PoolingHttpClientConnectionManager(3600, TimeUnit.SECONDS);
        this.cm.setMaxTotal(builder.getMaxConnections());
        this.cm.setDefaultMaxPerRoute(builder.getDefaultMaxPerRoute());
        this.httpClient=HttpClients.custom().setConnectionManager(this.cm).build();
    }

    public CloseableHttpClient getHttpClient() {
        IdleConnectionMonitorThread monitor= new IdleConnectionMonitorThread(this.cm,this.httpClient);
        monitor.setDaemon(true);
        monitor.start();
        LOGGER.debug("Starting cnx manager [{}] and pool [{}]",this.cm,this.httpClient);
        return  this.httpClient;
    }

    /**
     * User Friendly builder
     * <p>There are 3 main parameters to set. maxConnections with default 20, macConnPerRoute default 10, defaultMaxPerRoute default 5</p>
     *
     */
    public static class Builder {
        private int maxConnections=20;
        private int maxConnPerRoute=10;
        private int defaultMaxPerRoute=5;

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder setMaxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
            return this;
        }

        public Builder setMaxConnPerRoute(int maxConnPerRoute) {
            this.maxConnPerRoute = maxConnPerRoute;
            return this;
        }

        public Builder setDefaultMaxPerRoute(int defaultMaxPerRoute) {
            this.defaultMaxPerRoute = defaultMaxPerRoute;
            return this;
        }


        public int getMaxConnections() {
            return maxConnections;
        }

        public int getMaxConnPerRoute() {
            return maxConnPerRoute;
        }

        public int getDefaultMaxPerRoute() {
            return defaultMaxPerRoute;
        }

        public HttpClientPool build() {
            return new HttpClientPool(this);
        }
    }


}
