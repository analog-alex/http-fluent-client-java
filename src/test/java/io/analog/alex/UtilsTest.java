package io.analog.alex;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import io.analog.alex.models.Person;
import io.analog.alex.utils.GsonUtils;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UtilsTest {
    @Test
    public void gsonUtilsTest() {
        Person person = new Person();
        person.setName("name");

        assertEquals("{\"name\":\"name\"}", GsonUtils.json(person));
        assertEquals("{\"obj\":{\"name\":\"name\"}}", GsonUtils.jsonWrap("obj", person));
        assertEquals("{}", GsonUtils.emptyObj());

        Person parsed = GsonUtils.parse("{\"name\":\"name\"}", Person.class);
        assertEquals("name", parsed.getName());

        JsonObject jObj = new JsonObject();
        jObj.addProperty("name", "name");

        parsed = GsonUtils.parse(jObj, Person.class);

        Optional<String> property = GsonUtils.getMember(jObj, "name");

        assertTrue(property.isPresent());
        assertEquals("name", property.get());

        property = GsonUtils.getMember(jObj, "notName");
        assertFalse(property.isPresent());

    }

    @Test
    public void gsonAlternativeTest() {
        // -----
        Optional<String> property = GsonUtils.getMember(null, "name");
        assertFalse(property.isPresent());

        // -----
        property = GsonUtils.getMember(new JsonObject(), "name");
        assertFalse(property.isPresent());

        // -----
        JsonObject jObj = new JsonObject();
        jObj.add("name", null);
        property = GsonUtils.getMember(jObj, "name");
        assertFalse(property.isPresent());

        // -----
        jObj = new JsonObject();
        jObj.add("name", JsonNull.INSTANCE);
        property = GsonUtils.getMember(jObj, "name");
        assertFalse(property.isPresent());
    }
}
