package io.analog.alex.http.methods.impl;

import io.analog.alex.http.methods.Method;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.CloseableHttpClient;

import java.net.URI;

/**
 * Represents an http request with DELETE as a method
 *
 * @author Miguel Alexandre
 */
public class Delete extends Method {

    private Delete() {
        super(null);
    }

    @Override
    public String getMethod() {
        return "DELETE";
    }

    /**
     * Create a new Delete request object with a URI object and a Apache Http Client
     *
     * @param uri an universal resource identifier representing the address of a remote resource
     * @param client a pre-defined CloseableHttpClient to override the default one
     */
    public Delete(URI uri, CloseableHttpClient client) {
        super(client);
        this.request = new HttpDelete(uri);
    }

    /**
     * Create a new Delete request object with a String URI and a Apache Http Client
     *
     * @param uri an universal resource identifier representing the address of a remote resource
     * @param client a pre-defined CloseableHttpClient to override the default one
     */
    public Delete(String uri, CloseableHttpClient client) {
        super(client);
        this.request = new HttpDelete(uri);
    }
}
