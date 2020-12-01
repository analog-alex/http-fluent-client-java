package io.analog.alex.http.model;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.File;
import java.io.InputStream;

/**
 * The Form class abstracts a Multi-Part request entity, allowing the client to set
 * a series of key - value mappings, where the value can be Text, Binary or a File.
 *
 * @author Miguel Alexandre
 */
public class Form {
    private final MultipartEntityBuilder entity;

    /**
     * The entry point to begin creating a Multi-Part request
     */
    public Form() {
        entity = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
    }

    /**
     * Add a String part with TEXT_PLAIN Content-Type
     *
     * @param key the identifier
     * @param value the content proper
     * @return the reference to this class instance
     */
    public Form addPart(String key, String value) {
        entity.addPart(key, new StringBody((String) value, ContentType.TEXT_PLAIN));
        return this;
    }

    /**
     * Add a String part with an explicit Content-Type
     *
     * @param key the identifier
     * @param value the content proper
     * @param type a content type represented by the String (e.g. JSON)
     * @return the reference to this class instance
     */
    public Form addPart(String key, String value, ContentType type) {
        entity.addPart(key, new StringBody(value, type));
        return this;
    }

    /**
     * Add an InputStream part with a required explicit Content-Type
     *
     * @param key the identifier
     * @param value the content proper
     * @param type a content type represented by the Input Stream (e.g. JSON)
     * @return the reference to this class instance
     */
    public Form addPart(String key, InputStream value, ContentType type) {
        entity.addPart(key, new InputStreamBody(value, type));
        return this;
    }

    /**
     * Add an File part with a required explicit Content-Type
     *
     * @param key the identifier
     * @param value the content proper
     * @param type a content type represented by the File (e.g. JSON)
     * @return the reference to this class instance
     */
    public Form addPart(String key, File value, ContentType type) {
        entity.addPart(key, new FileBody(value, type, value.getName()));
        return this;
    }

    /**
     * Add an byte array part with a required explicit Content-Type and filename
     *
     * @param key the identifier
     * @param value the content proper
     * @param type a content type represented by the byte array (e.g. JSON)
     * @param filename a file name to store the byte array
     * @return the reference to this class instance
     */
    public Form addPart(String key, byte[] value, ContentType type, String filename) {
        entity.addPart(key, new ByteArrayBody(value, type, filename));
        return this;
    }

    /**
     * Build the Form into a HttpEntity, allowing inter-operability with the regular Apache Client
     *
     * @return an {@link org.apache.http.HttpEntity}
     */
    public HttpEntity set() {
        return entity.build();
    }

}
