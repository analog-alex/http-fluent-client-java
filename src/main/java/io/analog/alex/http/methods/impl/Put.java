package io.analog.alex.http.methods.impl;

import io.analog.alex.http.methods.MethodWithPayload;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;

import java.net.URI;

/**
 * Represents an http request with PUT as a method
 *
 * @author Miguel Alexandre
 */
public class Put extends MethodWithPayload {

    private Put() {
        super(null, null);
    }

    @Override
    public String getMethod() {
        return "PUT";
    }

    /**
     * Create a new Put request object with a URI object and a Apache Http Client
     *
     * @param uri an universal resource identifier representing the address of a remote resource
     * @param client a pre-defined CloseableHttpClient to override the default one
     */
    public Put(URI uri, CloseableHttpClient client) {
        super(client, new HttpPut(uri));
    }

    /**
     * Create a new Put request object with a string URI and a Apache Http Client
     *
     * @param uri an universal resource identifier representing the address of a remote resource
     * @param client a pre-defined CloseableHttpClient to override the default one
     */
    public Put(String uri, CloseableHttpClient client) {
        super(client, new HttpPut(uri));
    }
}
