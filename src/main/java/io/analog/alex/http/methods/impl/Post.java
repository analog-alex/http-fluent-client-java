package io.analog.alex.http.methods.impl;

import io.analog.alex.http.methods.MethodWithPayload;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;

import java.net.URI;

/**
 * Represents an http request with POST as a method
 *
 * @author Miguel Alexandre
 */
public class Post extends MethodWithPayload {

    private Post() {
        super(null, null);
    }

    @Override
    public String getMethod() {
        return "POST";
    }

    /**
     * Create a new Post request object with a URI object and a Apache Http Client
     *
     * @param uri    an universal resource identifier representing the address of a remote resource
     * @param client a pre-defined CloseableHttpClient to override the default one
     */
    public Post(URI uri, CloseableHttpClient client) {
        super(client, new HttpPost(uri));
    }

    /**
     * Create a new Post request object with a URI object and a Apache Http Client
     *
     * @param uri    an universal resource identifier representing the address of a remote resource
     * @param client a pre-defined CloseableHttpClient to override the default one
     */
    public Post(String uri, CloseableHttpClient client) {
        super(client, new HttpPost(uri));
    }
}
