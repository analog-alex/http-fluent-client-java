package io.analog.alex;

import io.analog.alex.http.Http;
import io.analog.alex.http.methods.impl.Delete;
import io.analog.alex.http.methods.impl.Get;
import io.analog.alex.http.methods.impl.Patch;
import io.analog.alex.http.methods.impl.Post;
import io.analog.alex.http.methods.impl.Put;
import io.analog.alex.http.model.Response;
import io.analog.alex.server.WireMockServerBuilder;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(Lifecycle.PER_CLASS)
public class SimpleRequestsTest {
    /* == constants == */
    private static final Integer port = 8090;
    private static final String endpoint = "http://localhost:" + port;
    private static final String resource = "/person";
    private static final Integer element = 1;
    private static final String JSONrequestBody = "  {" +
            "    \"userId\": 12," +
            "    \"id\": 102," +
            "    \"title\": \"at nam consequatur ea labore ea harum\"," +
            "    \"body\": \"cupiditate quo est a modi nesciunt soluta\\nipsa voluptas error itaque dicta in\\nautem qui minus magnam et distinctio eum\\naccusamus ratione error aut\"" +
            "  }";

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
    public void getRequestTest() {

        // simple gets

        Get getAll = Http.Get(endpoint + resource);
        Get getOne = Http.Get(endpoint + resource + "/" + element);

        Response getAllResponse = getAll.executeToOptional().orElseThrow(NoSuchElementException::new);
        Response getOneResponse = getOne.executeToOptional().orElseThrow(NoSuchElementException::new);

        assertEquals("GET", getAll.getMethod());
        assertEquals("GET", getOne.getMethod());

        assertEquals(HttpStatus.SC_OK, getAllResponse.getStatusCode().intValue());
        assertEquals(HttpStatus.SC_OK, getOneResponse.getStatusCode().intValue());

        assertTrue(getAllResponse.isSuccessful());
        assertTrue(getOneResponse.isSuccessful());

        // simple get with query params

        Get getWithParams = Http.Get(endpoint + resource + "-withParams");
        Response getWithParamsResponse = getWithParams
                .addParameter("name", "User")
                .addParameter("age", "99")
                .executeToOptional()
                .orElseThrow(NoSuchElementException::new);

        assertEquals(HttpStatus.SC_OK, getWithParamsResponse.getStatusCode().intValue());
    }

    @Test
    public void getRequestAlternativeTest() throws URISyntaxException {
        URI url = new URI(endpoint + resource);
        Get get = Http.Get(url);

        assertTrue(get.execute().isRight());
    }

    @Test
    public void postRequestTest() {
        Post post = Http.Post(endpoint + resource);
        Response postResponse = post.addBody(JSONrequestBody).executeToOptional().orElseThrow(NoSuchElementException::new);

        assertEquals("POST", post.getMethod());
        assertEquals(HttpStatus.SC_CREATED, postResponse.getStatusCode().intValue());
        assertTrue(postResponse.isSuccessful());
    }

    @Test
    public void postRequestAlternativeTest() throws URISyntaxException {
        URI url = new URI(endpoint + resource);
        Post post = Http.Post(url);
        Boolean isResponse = post.addBody(JSONrequestBody).execute().isRight();

        assertTrue(isResponse);
    }

    @Test
    public void putRequestTest() {
        Put put = Http.Put(endpoint + resource + "/" + element);
        Response putResponse = put.addBody(JSONrequestBody).executeToOptional().orElseThrow(NoSuchElementException::new);

        assertEquals("PUT", put.getMethod());
        assertEquals(HttpStatus.SC_CREATED, putResponse.getStatusCode().intValue());
        assertTrue(putResponse.isSuccessful());
    }

    @Test
    public void putRequestAlternativeTest() throws URISyntaxException {
        URI url = new URI(endpoint + resource);
        Put put = Http.Put(url);
        Boolean isResponse = put.addBody(JSONrequestBody).execute().isRight();

        assertTrue(isResponse);
    }

    @Test
    public void patchRequestTest() {
        Patch patch = Http.Patch(endpoint + resource + "/" + element);
        Response patchResponse = patch.addBody(JSONrequestBody).executeToOptional().orElseThrow(NoSuchElementException::new);

        assertEquals("PATCH", patch.getMethod());
        assertEquals(HttpStatus.SC_NO_CONTENT, patchResponse.getStatusCode().intValue());
        assertTrue(patchResponse.isSuccessful());
    }

    @Test
    public void patchRequestAlternativeTest() throws URISyntaxException {
        URI url = new URI(endpoint + resource);
        Patch patch = Http.Patch(url);
        Boolean isResponse = patch.addBody(JSONrequestBody).execute().isRight();

        assertTrue(isResponse);
    }

    @Test
    public void deleteRequestTest() {
        Delete delete = Http.Delete(endpoint + resource + "/" + element);
        Response deleteResponse = delete.executeToOptional().orElseThrow(NoSuchElementException::new);

        assertEquals("DELETE", delete.getMethod());
        assertEquals(HttpStatus.SC_NO_CONTENT, deleteResponse.getStatusCode().intValue());
        assertTrue(deleteResponse.isSuccessful());
    }

    @Test
    public void deleteRequestAlternativeTest() throws URISyntaxException {
        URI url = new URI(endpoint + resource);
        Delete delete = Http.Delete(url);

        assertTrue(delete.execute().isRight());
    }


    @Test
    public void headerAccessTest() {
        Get headerOut = Http.Get(endpoint + "/headerInOut");
        Response headerOutResponse = headerOut.addHeader("Authorization", "Bearer <jwt>").executeToOptional().orElseThrow(NoSuchElementException::new);

        assertTrue(headerOutResponse.getHeader("Content-Type").length() > 1);
        assertEquals("Bearer <jwt>", headerOutResponse.getHeader("Authorization"));
        assertEquals(HttpStatus.SC_OK, headerOutResponse.getStatusCode());
        assertEquals("No Header with key NONE", headerOutResponse.getHeader("NONE"));
    }

    @Test
    public void successRequestTest() {

        Get doesNotExist = Http.Get(endpoint + resource + "/99");
        Response doesNotExistResponse = doesNotExist.executeToOptional().orElseThrow(NoSuchElementException::new);

        assertEquals(HttpStatus.SC_NOT_FOUND, doesNotExistResponse.getStatusCode().intValue());
        assertTrue(doesNotExistResponse.isClientError());
        assertFalse(doesNotExistResponse.isServerError());
    }

    @Test
    public void withAnotherClientTest() {

        Get method = Http.Get(endpoint + resource + "/1");
        method.setClient(HttpClients.createMinimal());
        Response response = method.executeToOptional().orElseThrow(NoSuchElementException::new);

        assertEquals(HttpStatus.SC_OK, response.getStatusCode().intValue());

    }

    @Test
    public void responseCodesTest() {
        Response informational = Http.Get(endpoint + "/codes/informational").executeToOptional().orElseThrow(NoSuchElementException::new);
        assertFalse(informational.isInformational());

        Response successful = Http.Get(endpoint + "/codes/successful").executeToOptional().orElseThrow(NoSuchElementException::new);
        assertTrue(successful.isSuccessful());

        Response redirection = Http.Get(endpoint + "/codes/redirection").executeToOptional().orElseThrow(NoSuchElementException::new);
        assertTrue(redirection.isRedirection());

        Response clientError = Http.Get(endpoint + "/codes/clientError").executeToOptional().orElseThrow(NoSuchElementException::new);
        assertTrue(clientError.isClientError());

        Response serverError = Http.Get(endpoint + "/codes/serverError").executeToOptional().orElseThrow(NoSuchElementException::new);
        assertTrue(serverError.isServerError());

    }
}