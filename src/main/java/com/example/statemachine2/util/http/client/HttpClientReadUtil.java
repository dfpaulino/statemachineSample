package com.example.statemachine2.util.http.client;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * HttpClient Utility to execute a http call
 * This Utility Class intends to abstract the user from the pool resource management
 */
public class HttpClientReadUtil {

    /**
     * read. read the http response for a given pool and HttpUriRequest
     *  <p>
     *     This method completely relieves the user from having to worry about connection management.
     *      When using a ResponseHandler, HttpClient will automatically take care of ensuring release of the connection back to the connection manager
     *      regardless whether the request execution succeeds or causes an exception
     *  </p>
     *
     * @param hc {@link CloseableHttpClient}
     * @param rb {@link HttpUriRequest}
     * @return {@link HttpClientResponse}
     * @throws IOException
     */
    public static HttpClientResponse read(CloseableHttpClient hc, HttpUriRequest rb) throws IOException {

        HttpClientResponse httpClientResponse=new HttpClientResponse();
            CloseableHttpResponse response = hc.execute(rb);
            try {

                httpClientResponse.setStatusLine(response.getStatusLine());
                HttpEntity entity = response.getEntity();
                httpClientResponse.setBytes(EntityUtils.toByteArray(entity));
                //closes the inputStream
                EntityUtils.consume(entity);
            } catch (Exception e) {

            } finally {
                //Always close the response entity
                response.close();
            }
        return httpClientResponse;
    }
}
