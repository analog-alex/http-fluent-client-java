package io.analog.alex.http.model;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

/**
 * The Response object represents a HttpResponse, holding a status code, the
 * content (in a String format), and an array of response headers. This class offers several
 * built-in functions to manipulate the response content, by parsing it into a POJO or
 * a GSON JsonObject
 *
 * @author Miguel Alexandre
 */
public abstract class Response {
    private static final Logger LOGGER = LogManager.getLogger();

    private final String content;
    private final Integer statusCode;
    private final Header[] headers;

    /**
     * Constructs an object copying an {@link org.apache.http.HttpResponse}
     *
     * @param httpResponse an http response from Apache Client
     * @throws IOException if it cannot parse the response to a String
     */
    public Response(HttpResponse httpResponse) throws IOException {
        this.headers = httpResponse.getAllHeaders();
        this.statusCode = httpResponse.getStatusLine().getStatusCode();

        if (httpResponse.getEntity() != null) {
            this.content = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
        } else {
            this.content = StringUtils.EMPTY;
        }

        LOGGER.debug(this::toString);
    }

    /**
     * Get the response body as a java.lang.String
     *
     * @return the content in String format
     */
    public String getContent() {
        return content;
    }

    /**
     * Get an header by key
     *
     * @param key the header name
     * @return a String representation of the value of the header identified by the given key.
     * If no such header exists, it is returned a "No Header with key ${key}"
     */
    public String getHeader(String key) {
        for (Header header : headers) {
            if (header.getName().equals(key)) {
                return header.getValue();
            }
        }

        return String.format("No Header with key %s", key);
    }

    /**
     * Parse to a com.google.gson.JsonElement
     *
     * @return a {@link com.google.gson.JsonElement} object for further JSON manipulation
     */
    public JsonElement parseAsGson() {
        return new JsonParser().parse(this.content);
    }

    /**
     * Parse response into a generic Java class. The parsing is done via
     * the Gson library which maps JSON members into a POJO attribute with
     * the same name.
     *
     * @param <T>     a type parameter
     * @param classOf the class wished to be parsed to
     * @return an instance of the given T class, if parsing is successfull
     * @throws com.google.gson.JsonSyntaxException - if parsing fails
     */
    public <T> T parseAs(Class<T> classOf) {
        JsonElement element = new JsonParser().parse(this.content);
        return new GsonBuilder().create().fromJson(element, classOf);
    }

    /**
     * Parse response into a Java collection of a generic class. The parsing is done via
     * the Gson library which maps JSON members into a POJO attribute with
     * the same name.
     *
     * @param <T>     a type parameter
     * @param classOf the type that will parametrized the collection that the JSON will be parsed to
     * @return a collection of generic T class
     * @throws com.google.gson.JsonSyntaxException - if parsing fails
     */
    public <T> Collection<T> parseAsCollectionOf(Class<T> classOf) {
        JsonArray elements = new JsonParser().parse(this.content).getAsJsonArray();
        Type listType = TypeToken.getParameterized(ArrayList.class, classOf).getType();
        return new GsonBuilder().create().fromJson(elements, listType);
    }

    /**
     * Get the status code of the response as a java.lang.Integer
     *
     * @return the status code as an Integer
     */
    public Integer getStatusCode() {
        return statusCode;
    }

    /* *
     *  Fluent Methods to Log the Response
     */

    /**
     * Log the response by calling a LOGGER and {@link #toString()}
     *
     * @return the reference to this class instance
     */
    public Response logReponse() {
        LOGGER.info(this::toString);
        return this;
    }

    /**
     * log the response by calling a LOGGER to the format provided by a
     * function that maps a Response instance to a String instance
     *
     * @param mutate a function that takes a {@link io.analog.alex.http.model.Response} and outputs a String
     * @return the reference to this class instance
     */
    public Response logReponse(Function<Response, String> mutate) {
        LOGGER.info(() -> mutate.apply(this));
        return this;
    }

    /* *
     * UTILITIES
     * some ease-of-life features
     */

    /**
     * Was the return code in the 100-199 range?
     *
     * @return a boolean answering the question
     */
    public Boolean isInformational() {
        return this.statusCode > 99 && this.statusCode < 199;
    }

    /**
     * Was the return code in the 200-299 range?
     * (Semantically understood as a successful response)
     *
     * @return a boolean answering the question
     */
    public Boolean isSuccessful() {
        return this.statusCode > 199 && this.statusCode < 299;
    }

    /**
     * Was the return code in the 300-399 range?
     * (Semantically understood as a redirection)
     *
     * @return a boolean answering the question
     */
    public Boolean isRedirection() {
        return this.statusCode > 299 && this.statusCode < 399;
    }

    /**
     * Was the return code in the 400-499 range?
     * (Semantically understood as a malformed or wrongly sent client request)
     *
     * @return a boolean answering the question
     */
    public Boolean isClientError() {
        return this.statusCode > 399 && this.statusCode < 499;
    }

    /**
     * Was the return code in the 500-599 range?
     * (Semantically understood as a server error)
     *
     * @return a boolean answering the question
     */
    public Boolean isServerError() {
        return this.statusCode > 499 && this.statusCode < 599;
    }

    /**
     * a String representation of the object instance
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Response [status=" + statusCode + ", content=" + content + "]";
    }
}
