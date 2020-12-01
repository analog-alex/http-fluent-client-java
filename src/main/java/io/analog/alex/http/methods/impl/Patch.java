package io.analog.alex.http.methods.impl;

import io.analog.alex.http.methods.MethodWithPayload;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.impl.client.CloseableHttpClient;

import java.net.URI;

/**
 * Represents an http request with PATCH as a method
 *
 * @author Miguel Alexandre
 */
public class Patch extends MethodWithPayload {

    private Patch() {
        super(null, null);
    }

    @Override
    public String getMethod() {
        return "PATCH";
    }

    /**
     * Create a new Patch request object with a URI object and a Apache Http Client
     *
     * @param uri    an universal resource identifier representing the address of a remote resource
     * @param client a pre-defined CloseableHttpClient to override the default one
     */
    public Patch(URI uri, CloseableHttpClient client) {
        super(client, new HttpPatch(uri));
    }

    /**
     * Create a new Patch request object with a string URI and a Apache Http Client
     *
     * @param uri    an universal resource identifier representing the address of a remote resource
     * @param client a pre-defined CloseableHttpClient to override the default one
     */
    public Patch(String uri, CloseableHttpClient client) {
        super(client, new HttpPatch(uri));
    }
}