package io.analog.alex.http.methods;

import io.analog.alex.http.model.Form;
import io.analog.alex.http.model.UrlEncodedForm;
import io.analog.alex.utils.GsonUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.File;
import java.io.InputStream;
import java.lang.invoke.WrongMethodTypeException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public abstract class MethodWithPayload extends Method {
    protected HttpEntityEnclosingRequestBase requestExtended;

    protected MethodWithPayload(
            CloseableHttpClient client,
            HttpEntityEnclosingRequestBase req) {
        super(client);
        this.requestExtended = req;
        super.request = this.requestExtended;
    }


    /* ===========================
     * add payload with a string value of a content type
     */

    /**
     * Add a body with a given type
     *
     * @param payload     a String payload
     * @param contentType a content type written raw
     * @return the abstract MethodWithPayload
     */
    public MethodWithPayload addBody(String payload, String contentType) {
        StringEntity requestEntity = new StringEntity(payload, StandardCharsets.UTF_8);
        requestEntity.setContentType(contentType);
        this.requestExtended.setEntity(requestEntity);
        return this;
    }

    /**
     * Add a body with a given type
     *
     * @param payload     a byte array payload
     * @param contentType a content type written raw
     * @return the abstract MethodWithPayload
     */
    public MethodWithPayload addBody(byte[] payload, String contentType) {
        ByteArrayEntity requestEntity = new ByteArrayEntity(payload);
        requestEntity.setContentType(contentType);
        this.requestExtended.setEntity(requestEntity);
        return this;
    }

    /**
     * Add a body with a given type
     *
     * @param payload     an input stream payload
     * @param contentType a content type written raw
     * @return the abstract MethodWithPayload
     */
    public MethodWithPayload addBody(InputStream payload, String contentType) {
        InputStreamEntity requestEntity = new InputStreamEntity(payload);
        requestEntity.setContentType(contentType);
        this.requestExtended.setEntity(requestEntity);
        return this;
    }

    /**
     * Add a body with a given type
     *
     * @param payload     a file payload
     * @param contentType a content type written raw
     * @return the abstract MethodWithPayload
     */
    public MethodWithPayload addBody(File payload, String contentType) {
        FileEntity requestEntity = new FileEntity(payload);
        requestEntity.setContentType(contentType);
        this.requestExtended.setEntity(requestEntity);
        return this;
    }

    /* ===========================
     * add payload with content type as ContentType (pass as String to above methods)
     */

    /**
     * Add a body with a given type
     *
     * @param payload     an input stream payload
     * @param contentType a content type
     * @return the abstract MethodWithPayload
     */
    public MethodWithPayload addBody(InputStream payload, ContentType contentType) {
        addBody(payload, contentType.getMimeType());
        return this;
    }

    /**
     * Add a body with a given type
     *
     * @param payload     a byte array payload
     * @param contentType a content type
     * @return the abstract MethodWithPayload
     */
    public MethodWithPayload addBody(byte[] payload, ContentType contentType) {
        addBody(payload, contentType.getMimeType());
        return this;
    }

    /**
     * Add a body with a given type
     *
     * @param payload     a String payload
     * @param contentType a content type
     * @return the abstract MethodWithPayload
     */
    public MethodWithPayload addBody(String payload, ContentType contentType) {
        addBody(payload, contentType.getMimeType());
        return this;
    }

    /**
     * Add a body with a given type
     *
     * @param payload     a file payload
     * @param contentType a content type
     * @return the abstract MethodWithPayload
     */
    public MethodWithPayload addBody(File payload, ContentType contentType) {
        addBody(payload, contentType.getMimeType());
        return this;
    }

    /* ===========================
     * ease-of-life methods
     */

    /**
     * Add a String based body as a 'application/json'
     *
     * @param payload a String payload understood as representing a JSON
     * @return the abstract MethodWithPayload
     */
    public MethodWithPayload addBody(String payload) {
        addBody(payload, ContentType.APPLICATION_JSON);
        return this;
    }

    /**
     * Add a Object based body as a 'application/json'
     *
     * @param payload an Object understood as representing a JSON
     * @return the abstract MethodWithPayload
     */
    public MethodWithPayload addBodyAsJson(Object payload) {
        addBody(GsonUtils.json(payload));
        return this;
    }

    /**
     * Add a HttpEntity request body
     *
     * @param payload an {@link org.apache.http.HttpEntity} for interoperability with Apache Http
     * @return the abstract MethodWithPayload
     */
    public MethodWithPayload addBody(HttpEntity payload) {
        this.requestExtended.setEntity(payload);
        return this;
    }


    /**
     * Add a Form request body
     *
     * @param payload a {@link io.analog.alex.http.model.Form} payload
     * @return the abstract MethodWithPayload
     */
    public MethodWithPayload addBody(Form payload) {
        this.requestExtended.setEntity(payload.set());
        return this;
    }

    /**
     * Add a UrlEncodedForm request body
     *
     * @param payload a {@link io.analog.alex.http.model.UrlEncodedForm} payload
     * @return the abstract MethodWithPayload
     */
    public MethodWithPayload addBody(UrlEncodedForm payload) {
        this.requestExtended.setEntity(payload.set());
        return this;
    }

    /* ===========================
     * override super methods to assure coherence in fluent usage
     */

    /**
     * Add a URL parameter to the URI
     *
     * @param key   the parameter name
     * @param value the parameter value
     * @return the modified abstract http MethodWithPayload
     */
    @Override
    public MethodWithPayload addParameter(String key, String value) {
        try {
            URI uri = new URIBuilder(this.request.getURI()).addParameter(key, value).build();
            this.request.setURI(uri);

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
     * @return the modified abstract http MethodWithPayload
     */
    @Override
    public MethodWithPayload addHeader(String key, String value) {
        this.request.addHeader(key, value);
        return this;
    }
}
