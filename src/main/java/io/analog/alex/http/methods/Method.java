package io.analog.alex.http.methods;

import io.analog.alex.functional.monads.Either;
import io.analog.alex.http.model.Response;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.lang.invoke.WrongMethodTypeException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class Method {
    protected HttpRequestBase request;
    protected CloseableHttpClient client;

    public abstract String getMethod();


    protected Method(CloseableHttpClient client) {
        this.client = client;
    }

    /**
     * Re-Set the HttpClient manually
     *
     * @param client a pre-configured Apache Http Client to override the default one
     * @return the abstract http Method
     */
    public Method setClient(CloseableHttpClient client) {
        this.client = client;
        return this;
    }

    /**
     * Add a URL parameter to the URI
     *
     * @param key   the parameter name
     * @param value the parameter value
     * @return the modified abstract http Method
     */
    public Method addParameter(String key, String value) {
        try {
            URI nUri = new URIBuilder(this.request.getURI()).addParameter(key, value).build();
            this.request.setURI(nUri);

        } catch (URISyntaxException e) {
            throw new WrongMethodTypeException("URI parsing exited via a critical exception");
        }

        return this;
    }

    /**
     * Add an HTTP Header on a  key | value basis
     *
     * @param key   the header key
     * @param value the header value
     * @return the modified abstract http Method
     */
    public Method addHeader(String key, String value) {
        this.request.addHeader(key, value);
        return this;
    }

    /* *
     * Execution Functions
     */

    /**
     * execute the HTTP call and return an Optional (failure state corresponds to a
     * empty optional)
     *
     * @return the response as an {@link java.util.Optional}
     */
    public Optional<Response> executeToOptional() {
        try (CloseableHttpResponse response = client.execute(request)) {
            return Optional.of(new InnerResponse(response));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    /**
     * execute the HTTP call and return an Either
     * (failure state corresponds to a 'left' Either)
     *
     * @return the response as an {@link io.analog.alex.functional.monads.Either}
     */
    public Either<IOException, Response> execute() {
        try (CloseableHttpResponse response = client.execute(request)) {
            return Either.right(new InnerResponse(response));

        } catch (IOException e) {
            return Either.left(e);
        }
    }

    /**
     * An asynchronous execution of the HTTP call, generation a CompleatableFuture object
     * that holds the promise
     *
     * @return the response as an {@link java.util.concurrent.CompletableFuture}
     */
    public CompletableFuture<Either<IOException, Response>> executeAsync() {
        return CompletableFuture.supplyAsync(this::execute);
    }

    /**
     * Using an {@link io.reactivex.Observable} parameterized with a {@link io.analog.alex.http.model.Response} object
     * this method allows the caller to handle the Http response in a reactive manner, not to dissimilar with the standart
     * Http call in a modern RxJs framework.
     *
     * @return the response as an {@link io.reactivex.Observable}
     */
    public Observable<Response> executeToObservable() {
        ObservableOnSubscribe<Response> emitEvent = emitter -> {

            try (CloseableHttpResponse response = client.execute(request)) {
                emitter.onNext(new InnerResponse(response));

            } catch (IOException e) {
                throw new HttpException(e.getMessage());
            }
        };

        return Observable.create(emitEvent).subscribeOn(Schedulers.io());
    }

    // --------

    class InnerResponse extends Response {

        public InnerResponse(HttpResponse httpResponse) throws IOException {
            super(httpResponse);
        }
    }
}

