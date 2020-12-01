package io.analog.alex;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.analog.alex.http.Http;
import io.analog.alex.http.model.Form;
import io.analog.alex.http.model.UrlEncodedForm;
import io.analog.alex.models.Person;
import io.analog.alex.server.WireMockServerBuilder;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(Lifecycle.PER_CLASS)
public class SimplePayloadTest {
    /* == constants == */
    private static final Integer port = 8091;
    private static final String endpoint = "http://localhost:" + port;
    private static final String resource = "/person";
    private static final String body = "{ \"name\": \"User\" }";

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
    public void fetchOneObjectAndParseTest() {
        Person person = Http.Get(endpoint + resource + "/1")
                .executeToOptional()
                .orElseThrow(NoSuchElementException::new)
                .parseAs(Person.class);

        assertAll("person",
                () -> assertEquals(1, person.getId()),
                () -> assertEquals(27, person.getAge()),
                () -> assertEquals("Miguel Alexandre", person.getName()));
    }

    @Test
    public void fetchOneObjectAndParseAsGsonTest() {
        JsonElement gson = Http.Get(endpoint + resource + "/1")
                .executeToOptional()
                .orElseThrow(NoSuchElementException::new)
                .logReponse(str -> "EasyLog" + str)
                .parseAsGson();

        int age = gson.getAsJsonObject().get("age").getAsInt();
        assertEquals(27, age);
    }

    @Test
    public void fetchCollectionAndParseTest() {
        Collection<Person> listOfPersons = Http.Get(endpoint + resource)
                .executeToOptional()
                .orElseThrow(NoSuchElementException::new)
                .parseAsCollectionOf(Person.class);

        assertAll("list",
                () -> assertEquals(4, listOfPersons.size()),
                () -> assertEquals(10, listOfPersons.stream().map(Person::getId).reduce((x, y) -> x + y).get()));
    }

    @Test
    public void simpleJsonPayloadTest() {

        JsonObject json = new JsonObject();
        json.addProperty("userId", 1);
        json.addProperty("title", "TEST_TITLE");

        Integer responseCode = Http.Post(endpoint + "/headerJson")
                .addBody(json.toString())
                .executeToOptional()
                .orElseThrow(NoSuchElementException::new)
                .logReponse()
                .getStatusCode();

        assertEquals(201, responseCode.intValue());
    }

    @Test
    public void anotherSimpleJsonPayloadTest() {

        JsonObject json = new JsonObject();
        json.addProperty("userId", 1);
        json.addProperty("title", "TEST_TITLE");

        Integer responseCode = Http.Post(endpoint + "/payloadWithExtras")
                .addParameter("name", "User")
                .addHeader("Authorization", "Bearer <jwt>")
                .addBody(json.toString())
                .executeToOptional()
                .orElseThrow(NoSuchElementException::new)
                .logReponse()
                .getStatusCode();

        assertEquals(201, responseCode.intValue());
    }

    @Test
    public void jsonPayloadTest() {

        JsonObject json = new JsonObject();
        json.addProperty("name", "User");

        Integer responseCode = Http.Post(endpoint + "/headerJson")
                .addBody(json.toString())
                .executeToOptional()
                .orElseThrow(NoSuchElementException::new)
                .logReponse()
                .getStatusCode();

        assertEquals(201, responseCode.intValue());
    }

    @Test
    public void formDataPayloadTest() {
        Form data = new Form()
                .addPart("key", "value");

        Integer responseCode = Http.Post(endpoint + "/headerMultiForm")
                .addBody(data)
                .executeToOptional()
                .orElseThrow(NoSuchElementException::new)
                .logReponse()
                .getStatusCode();

        assertEquals(201, responseCode.intValue());

    }

    @Test
    public void formDataPayloadWithFileTest() throws FileNotFoundException, IOException {

        File initialFile = new File("src/test/resources/__files/other/text.pages");

        try (InputStream targetStream = new FileInputStream(initialFile)) {
            Form data = new Form()
                    .addPart("key", "value")
                    .addPart("text", "value2", ContentType.TEXT_PLAIN)
                    .addPart("byte", "bytes".getBytes(), ContentType.TEXT_PLAIN, "text")
                    .addPart("file", targetStream, ContentType.DEFAULT_BINARY);

            Integer responseCode = Http.Post(endpoint + "/headerMultiForm")
                    .addBody(data)
                    .executeToOptional()
                    .orElseThrow(NoSuchElementException::new)
                    .logReponse()
                    .getStatusCode();

            assertEquals(201, responseCode.intValue());
        }
    }

    @Test
    public void byteArrayBodyTest() {
        Integer responseCode = Http.Post(endpoint + "/headerJson")
                .addBody(body.getBytes(), ContentType.APPLICATION_JSON)
                .executeToOptional()
                .orElseThrow(NoSuchElementException::new)
                .logReponse()
                .getStatusCode();

        assertEquals(201, responseCode.intValue());
    }

    @Test
    public void inputStreamBodyTest() {
        Integer responseCode = Http.Post(endpoint + "/headerJson")
                .addBody(new ByteArrayInputStream(body.getBytes()), ContentType.APPLICATION_JSON)
                .executeToOptional()
                .orElseThrow(NoSuchElementException::new)
                .logReponse()
                .getStatusCode();

        assertEquals(201, responseCode.intValue());
    }

    @Test
    public void byteArrayBodyAltTest() {
        Integer responseCode = Http.Post(endpoint + "/headerJson")
                .addBody(body.getBytes(), "application/json")
                .executeToOptional()
                .orElseThrow(NoSuchElementException::new)
                .logReponse()
                .getStatusCode();

        assertEquals(201, responseCode.intValue());
    }

    @Test
    public void inputStreamBodyAltTest() {
        Integer responseCode = Http.Post(endpoint + "/headerJson")
                .addBody(new ByteArrayInputStream(body.getBytes()), "application/json")
                .executeToOptional()
                .orElseThrow(NoSuchElementException::new)
                .logReponse()
                .getStatusCode();

        assertEquals(201, responseCode.intValue());
    }

    @Test
    public void formUrlEncodedDataPayloadTest() {
        UrlEncodedForm data = new UrlEncodedForm()
                .addPart("requestData", "value");

        Integer responseCode = Http.Post(endpoint + "/headerUrlencoded")
                .addBody(data)
                .executeToOptional()
                .orElseThrow(NoSuchElementException::new)
                .logReponse()
                .getStatusCode();

        assertEquals(201, responseCode.intValue());

    }
}
