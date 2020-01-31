package com.example.statemachine2.util.http.client;

import org.apache.http.StatusLine;

/**
 * Simple HttpResponse when using the {@link HttpClientReadUtil}
 *
 */
public class HttpClientResponse {
    private byte[] bytes;
    private StatusLine statusLine;

    public HttpClientResponse() {
    }

    /**
     * response body
     * @return byte[]
     */
    public byte[] getBytes() {
        return bytes;
    }

    public HttpClientResponse setBytes(byte[] bytes) {
        this.bytes = bytes;
        return this;
    }

    /**
     * Http response code
     * @return {@link StatusLine}
     */
    public StatusLine getStatusLine() {
        return statusLine;
    }

    public HttpClientResponse setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
        return this;
    }
}
