package yomichan.utils;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.NoArgsConstructor;

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
        return node.get(fieldName).asText();
    }

    public static boolean getBoolean(JsonNode node, String fieldName) {
        return getBoolean(node, fieldName, false);
    }

    public static boolean getBoolean(JsonNode node, String fieldName, boolean defaultValue) {
        if (node == null || !node.has(fieldName)) {
            return defaultValue;
        }
        return node.get(fieldName).asBoolean();
    }

    public static Double getDouble(JsonNode node, String fieldName) {
        return getDouble(node, fieldName, null);
    }

    public static Double getDouble(JsonNode node, String fieldName, Double defaultValue) {
        if (node == null || !node.has(fieldName)) {
            return defaultValue;
        }
        return node.get(fieldName).asDouble();
    }

    public static Integer getInt(JsonNode node) {
        return getInt(node, null);
    }

    public static Integer getInt(JsonNode node, String fieldName) {
        return getInt(node, fieldName, null);
    }

    public static Integer getInt(JsonNode node, String fieldName, Integer defaultValue) {
        if (node == null || !node.has(fieldName)) {
            return defaultValue;
        }
        return node.get(fieldName).asInt();
    }
}
