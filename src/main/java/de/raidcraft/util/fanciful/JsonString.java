package de.raidcraft.util.fanciful;

import com.google.gson.stream.JsonWriter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a JSON string value.
 * Writes by this object will not write displayName values nor begin/end objects in the JSON stream.
 * All writes merely write the represented string value.
 */
final class JsonString implements JsonRepresentedObject, ConfigurationSerializable {

    private String _value;

    public JsonString(CharSequence value) {
        _value = value == null ? null : value.toString();
    }

    @Override
    public void writeJson(JsonWriter writer) throws IOException {
        writer.value(getValue());
    }

    public String getValue() {
        return _value;
    }

    public Map<String, Object> serialize() {
        HashMap<String, Object> theSingleValue = new HashMap<String, Object>();
        theSingleValue.put("stringValue", _value);
        return theSingleValue;
    }

    public static JsonString deserialize(Map<String, Object> map) {
        return new JsonString(map.get("stringValue").toString());
    }

    @Override
    public String toString() {
        return _value;
    }

}
