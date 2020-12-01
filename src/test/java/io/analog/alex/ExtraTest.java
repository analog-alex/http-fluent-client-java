package io.analog.alex;

import io.analog.alex.functional.monads.Either;
import io.analog.alex.http.Http;
import io.analog.alex.http.methods.impl.Get;
import io.analog.alex.http.model.Response;
import io.analog.alex.server.WireMockServerBuilder;
import io.reactivex.Observable;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(Lifecycle.PER_CLASS)
public class ExtraTest {
    /* == constants == */
    private static final Integer port = 8092;
    private static final String endpoint = "http://localhost:" + port;
    private static final String resource = "/person";


    /* == instantiate server for all methods == */
    @BeforeAll
    public void setUp() {

        WireMockServerBuilder.startOnPort(port);
    }

    @AfterAll
    public void tearDown() {

        WireMockServerBuilder.stopOnPort(port);
    }

    /* == tests == */
    @Test
    public void asyncRequestTest() throws IOException, InterruptedException, ExecutionException {

        Get all = Http.Get(endpoint + resource + "/1");

        CompletableFuture<Either<IOException, Response>> fut = all.executeAsync();
        assertFalse(fut.isDone());
        System.out.println("Waiting...");

        Response res = fut.get().attemptRightThrowIfLeft();
        assertEquals(HttpStatus.SC_OK, res.getStatusCode().intValue());
    }

    @Test
    public void allAsyncTest() throws InterruptedException {

        Get one = Http.Get(endpoint + resource + "/1");
        Get two = Http.Get(endpoint + resource + "/1");
        Get three = Http.Get(endpoint + resource + "/1");
        Get four = Http.Get(endpoint + resource + "/1");
        Get five = Http.Get(endpoint + resource + "/1");

        Observable<Either<IOException, Response>> obs = Http.all(one, two, three, four, five);
        obs.map(o -> o.getRight())
                // the test 'per-se'
                .test()
                .assertNoErrors();

        assertFalse(obs.isEmpty().blockingGet());
    }

    @Test
    public void reactiveCallTest() throws InterruptedException {
        Http.Get(endpoint + resource).executeToObservable().test().assertNoErrors();
        Http.Get(endpoint + resource).executeToObservable()
                .subscribe(x -> System.out.println(x), err -> err.printStackTrace(), () -> System.out.println("Test Done!"));

        assertFalse(Http.Get(endpoint + resource).executeToObservable().isEmpty().blockingGet());
    }

    @Test
    public void setClientTest() {
        Http.setClient(HttpClients.createMinimal());
        Get one = Http.Get(endpoint + resource + "/1");

        assertTrue(one.execute().isRight());
    }
}
