package io.analog.alex.http.methods.impl;

import io.analog.alex.http.methods.Method;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import java.net.URI;

/**
 * Represents an http request with GET as a method
 *
 * @author Miguel Alexandre
 */
public class Get extends Method {
    private Get() {
        super(null);
    }

    @Override
    public String getMethod() {
        return "GET";
    }

    /**
     * Create a new Get request object with a URI object and a Apache Http Client
     *
     * @param uri an universal resource identifier representing the address of a remote resource
     * @param client a pre-defined CloseableHttpClient to override the default one
     */
    public Get(URI uri, CloseableHttpClient client) {
        super(client);
        this.request = new HttpGet(uri);
    }

    /**
     * Create a new Get request object with a string URI and a Apache Http Client
     *
     * @param uri an universal resource identifier representing the address of a remote resource
     * @param client a pre-defined CloseableHttpClient to override the default one
     */
    public Get(String uri, CloseableHttpClient client) {
        super(client);
        this.request = new HttpGet(uri);
    }
}
