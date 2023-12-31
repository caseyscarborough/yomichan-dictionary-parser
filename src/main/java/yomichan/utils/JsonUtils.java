package yomichan.utils;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.NoArgsConstructor;
import yomichan.exception.YomichanException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class JsonUtils {

    public static String getText(JsonNode node) {
        return node != null ? node.asText() : null;
    }

    public static String getText(JsonNode node, String fieldName) {
        return getText(node, fieldName, null);
    }

    public static String getText(JsonNode node, String fieldName, String defaultValue) {
        if (node == null || !node.has(fieldName)) {
            return defaultValue;
        }
        final String value = node.get(fieldName).asText();
        return value != null ? value : defaultValue;
    }

    public static boolean getBoolean(JsonNode node, String fieldName) {
        return getBoolean(node, fieldName, false);
    }

    public static boolean getBoolean(JsonNode node, String fieldName, boolean defaultValue) {
        if (node == null || !node.has(fieldName)) {
            return defaultValue;
        }
        final JsonNode field = node.get(fieldName);
        return field.asText() != null ? field.asBoolean() : defaultValue;
    }

    public static Double getDouble(JsonNode node, String fieldName) {
        return getDouble(node, fieldName, null);
    }

    public static Double getDouble(JsonNode node, String fieldName, Double defaultValue) {
        if (node == null || !node.has(fieldName)) {
            return defaultValue;
        }

        final JsonNode field = node.get(fieldName);
        if (field.asText() == null) {
            // Can't use ternary operator because it attempts to
            // unbox the default value throwing an NPE when it's null.
            return defaultValue;
        }
        return field.asDouble();
    }

    public static Integer getInt(JsonNode node) {
        return node != null && node.asText() != null ? node.asInt() : null;
    }

    public static Integer getInt(JsonNode node, String fieldName) {
        return getInt(node, fieldName, null);
    }

    public static Integer getInt(JsonNode node, String fieldName, Integer defaultValue) {
        if (node == null || !node.has(fieldName)) {
            return defaultValue;
        }
        final JsonNode field = node.get(fieldName);
        // Can't use ternary operator because it attempts to
        // unbox the default value throwing an NPE when it's null.
        if (field.asText() == null) {
            return defaultValue;
        }
        return field.asInt();
    }

    public static Map<String, String> toMap(JsonNode node) {
        if (!node.isObject()) {
            throw new IllegalStateException("Can only convert JSON object to map!");
        }

        Map<String, String> output = new HashMap<>();
        final Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            final Map.Entry<String, JsonNode> field = fields.next();
            output.put(field.getKey(), field.getValue().asText());
        }
        return output;
    }

    public static List<String> parseStringArray(JsonNode node) {
        if (node == null) {
            return null;
        }
        if (!node.isArray()) {
            throw new YomichanException("Cannot parse string array from non-array node: " + node);
        }
        final List<String> output = new ArrayList<>();
        node.forEach(n -> output.add(getText(n)));
        return output;
    }

    public static List<Integer> parseIntegerArray(JsonNode node) {
        if (node == null) {
            return null;
        }
        if (!node.isArray()) {
            throw new YomichanException("Cannot parse integer array from non-array node: " + node);
        }
        final List<Integer> output = new ArrayList<>();
        node.forEach(n -> output.add(getInt(n)));
        return output;
    }

    public static List<String> parseSpaceSeparatedText(JsonNode node) {
        if (node == null) {
            return null;
        }
        if (!node.isTextual()) {
            throw new YomichanException("Cannot parse space separated text from non-text node: " + node);
        }
        return Arrays.stream(node.asText().split(" "))
            .filter(s -> !s.isBlank())
            .toList();
    }
}
