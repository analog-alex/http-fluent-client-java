package io.analog.alex.http.model;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * The UrlEncodedForm class abstracts an url-encoded request entity, allowing the client to set
 * a series of key - value mappings and send a request body in the same format as URL parameters
 * are usually passed i.e. key=value
 *
 * @author Miguel Alexandre
 */
public class UrlEncodedForm {
    private List<NameValuePair> params;

    /**
     * Begin building a UrlEncoded request with this form.
     */
    public UrlEncodedForm() {
        this.params = new ArrayList<>();
    }

    /**
     * Adds an argument value in String format, identified by a key.
     * Should look like <code>key=value</code> in the raw Http request.
     *
     * @param key   the name of the part
     * @param value the value proper
     * @return the reference to this class instance
     */
    public UrlEncodedForm addPart(String key, String value) {
        this.params.add(new BasicNameValuePair(key, value));
        return this;
    }

    /**
     * Build the Form into a HttpEntity, allowing interoperability with the regular Apache Client
     *
     * @return an {@link org.apache.http.HttpEntity}
     */
    public HttpEntity set() {
        return new UrlEncodedFormEntity(this.params, StandardCharsets.UTF_8);
    }
}
