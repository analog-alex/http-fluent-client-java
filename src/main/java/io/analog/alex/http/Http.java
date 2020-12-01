package io.analog.alex.http;

import io.analog.alex.functional.monads.Either;
import io.analog.alex.http.methods.Method;
import io.analog.alex.http.methods.impl.Delete;
import io.analog.alex.http.methods.impl.Get;
import io.analog.alex.http.methods.impl.Patch;
import io.analog.alex.http.methods.impl.Post;
import io.analog.alex.http.methods.impl.Put;
import io.analog.alex.http.model.Response;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Main point of entry for API, the Http class allows the creation of a class that extends {@link io.analog.alex.http.methods.Method}
 * representing a Http request with that method i.e a GET, POST, PUT, PATCH or a DELETE. The creation is made via
 * a static function sharing the same name of the returned method -- 'Get' function return a {@link io.analog.alex.http.methods.impl.Get}
 */
public class Http {
    /* == hide constructor == */
    private Http() {
    }

    // ================================


    /**
     * A GET request to the provided {@link java.net.URI} resource
     * A GET request is normally used to retrieve data from a server without altering the state or triggering
     * any other kind of side-effects. This is described as a nullipotent operation.
     *
     * @param uri an universal resource identifier representing the address of a remote resource
     * @return a callable representation of the HTTP method
     */
    public static Get Get(URI uri) {
        return new Get(uri, client);
    }

    /**
     * A POST request to the provided {@link java.net.URI} resource
     * A POST response is normally used to provide data to the server to alter the state or to trigger some persistent effect.
     *
     * @param uri an universal resource identifier representing the address of a remote resource
     * @return a callable representation of the HTTP method
     */
    public static Post Post(URI uri) {
        return new Post(uri, client);
    }

    /**
     * A PATCH request to the provided {@link java.net.URI} resource
     * A PATCH request is normally used to alter some state or trigger some persistent effect in an idempotent fashion e.g. updating
     * a resource. It is distinguished from a PUT by representing a partial modification of the resource.
     *
     * @param uri an universal resource identifier representing the address of a remote resource
     * @return a callable representation of the HTTP method
     */
    public static Patch Patch(URI uri) {
        return new Patch(uri, client);
    }

    /**
     * A PUT request to the provided {@link java.net.URI} resource
     * A PUT request is normally used to alter some state or trigger some persistent effect in an idempotent fashion e.g. updating or upserting
     * a resource. It is distinguished from a PATCH by representing the full modification of the resource.
     *
     * @param uri an universal resource identifier representing the address of a remote resource
     * @return a callable representation of the HTTP method
     */
    public static Put Put(URI uri) {
        return new Put(uri, client);
    }

    /**
     * A DELETE request to the provided {@link java.net.URI} resource
     * A DELETE request is normally used to remove a resource.
     *
     * @param uri an universal resource identifier representing the address of a remote resource
     * @return a callable representation of the HTTP method
     */
    public static Delete Delete(URI uri) {
        return new Delete(uri, client);
    }

    // ---------

    /**
     * A GET request to the provided URL resource
     * A GET request is normally used to retrieve data from a server without altering the state or triggering
     * any other kind of side-effects. This is described as a nullipotent operation.
     *
     * @param uri an universal resource locator representing the address of a remote resource
     * @return a callable representation of the HTTP method
     */
    public static Get Get(String uri) {
        return new Get(uri, client);
    }

    /**
     * A POST request to the provided URL resource
     * A POST response is normally used to provide data to the server to alter the state or to trigger some persistent effect.
     *
     * @param uri an universal resource locator representing the address of a remote resource
     * @return a callable representation of the HTTP method
     */
    public static Post Post(String uri) {
        return new Post(uri, client);
    }

    /**
     * A PATCH request to the provided URL resource
     * A PATCH request is normally used to alter some state or trigger some persistent effect in an idempotent fashion e.g. updating
     * a resource. It is distinguished from a PUT by representing a partial modification of the resource.
     *
     * @param uri an universal resource identifier representing the address of a remote resource
     * @return a callable representation of the HTTP method
     */
    public static Patch Patch(String uri) {
        return new Patch(uri, client);
    }

    /**
     * A PUT request to the provided URL resource
     * A PUT request is normally used to alter some state or trigger some persistent effect in an idempotent fashion e.g. updating or upserting
     * a resource. It is distinguished from a PATCH by representing the full modification of the resource.
     *
     * @param uri an universal resource locator representing the address of a remote resource
     * @return a callable representation of the HTTP method
     */
    public static Put Put(String uri) {
        return new Put(uri, client);
    }

    /**
     * A DELETE request to the provided URL resource
     * A DELETE request is normally used to remove a resource.
     *
     * @param uri an universal resource locator representing the address of a remote resource
     * @return a callable representation of the HTTP method
     */
    public static Delete Delete(String uri) {
        return new Delete(uri, client);
    }

    /* ==========================
     *  extras
     */

    private static CloseableHttpClient client = closableHttpClient();

    /**
     * Calls all the provided request asynchronously and returns an {@link io.reactivex.Observable} that
     * can be subscribed to.
     *
     * @param https a collection of HTTP requests with any combination of methods
     * @return n {@link io.reactivex.Observable} for subscription
     */
    public static Observable<Either<IOException, Response>> all(Method... https) {

        /* === create a list of Observables from each async call == */
        List<Observable<Either<IOException, Response>>> observables = new ArrayList<>();

        for (Method http : https) {
            observables.add(Observable.fromFuture(http.executeAsync()));
        }

        /* === concatenate said collection of Observables === */
        return Observable.concat(observables).subscribeOn(Schedulers.io());
    }

    // INJECTION
    public static CloseableHttpClient closableHttpClient() {
        return HttpClients.createDefault();
    }

    public static void setClient(CloseableHttpClient newClient) {
        client = newClient;
    }
}
