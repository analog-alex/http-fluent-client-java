package io.analog.alex.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Collections;
import java.util.Optional;

/* ======
 *  Intended to be used as static methods
 */
public final class GsonUtils {
    // some ready-at-hand attributes
    private static final Gson GSON = new GsonBuilder().create();
    private static final JsonParser PARSER = new JsonParser();

    // hide constructor
    private GsonUtils() {
    }

    // JSON related function
    public static String json(Object src) {
        return GSON.toJson(src);
    }

    public static String jsonWrap(String memberName, Object src) {
        return GSON.toJson(Collections.singletonMap(memberName, src));
    }

    public static String emptyObj() {
        return json(new Object());
    }

    public static <T> T parse(String json, Class<T> classOf) {
        JsonElement ele = PARSER.parse(json);
        return GSON.fromJson(ele, classOf);
    }

    public static <T> T parse(JsonElement json, Class<T> classOf) {
        return GSON.fromJson(json, classOf);
    }

    public static Optional<String> getMember(JsonObject json, String name) {

        if (!isNull(json)
                && !json.isJsonNull()
                && !isNull(json.get(name))
                && !json.get(name).isJsonNull()) {
            String member = json.get(name).getAsString();
            if (!isNull(member)) {
                return Optional.of(member);
            }
        }

        return Optional.empty();
    }

    // null check function
    public static boolean isNull(Object reference) {
        return reference == null;
    }
}
