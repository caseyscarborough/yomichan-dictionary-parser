package yomichan.utils;

import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonUtilsTest {

    JsonNodeFactory factory = JsonNodeFactory.instance;

    @Test
    void testGetText() {
        assertEquals("test", JsonUtils.getText(new TextNode("test")));
        assertNull(JsonUtils.getText(null));
        assertNull(JsonUtils.getText(new TextNode(null)));
        final ObjectNode foo = new ObjectNode(factory, Map.of("foo", new TextNode("bar"), "baz", new TextNode(null)));
        assertEquals("bar", JsonUtils.getText(foo, "foo"));
        assertNull(JsonUtils.getText(foo, "baz"));
        assertEquals("qux", JsonUtils.getText(foo, "baz", "qux"));
        assertNull(JsonUtils.getText(foo, "qux"));
        assertEquals("foo", JsonUtils.getText(foo, "qux", "foo"));
    }

    @Test
    void testGetBoolean() {
        final ObjectNode foo = new ObjectNode(factory, Map.of("foo", new TextNode("true"), "bar", new TextNode("false"), "baz", new TextNode(null)));
        assertTrue(JsonUtils.getBoolean(foo, "foo"));
        assertFalse(JsonUtils.getBoolean(foo, "bar"));
        assertFalse(JsonUtils.getBoolean(foo, "baz"));
        assertTrue(JsonUtils.getBoolean(foo, "baz", true));
        assertFalse(JsonUtils.getBoolean(foo, "qux"));
        assertTrue(JsonUtils.getBoolean(foo, "qux", true));
    }

    @Test
    void testGetDouble() {
        final ObjectNode foo = new ObjectNode(factory, Map.of("foo", new DoubleNode(1.2D), "bar", new TextNode(null)));
        assertEquals(1.2D, JsonUtils.getDouble(foo, "foo"));
        assertNull(JsonUtils.getDouble(foo, "bar"));
        assertEquals(3.4D, JsonUtils.getDouble(foo, "bar", 3.4D));
        assertNull(JsonUtils.getDouble(foo, "baz"));
        assertEquals(5.6D, JsonUtils.getDouble(foo, "baz", 5.6D));
    }

    @Test
    void testGetInt() {
        assertEquals(1, JsonUtils.getInt(new IntNode(1)));
        assertNull(JsonUtils.getInt(null));
        assertNull(JsonUtils.getInt(new TextNode(null)));
        final ObjectNode foo = new ObjectNode(factory, Map.of("foo", new IntNode(3), "bar", new TextNode(null)));
        assertEquals(3, JsonUtils.getInt(foo, "foo"));
        assertNull(JsonUtils.getInt(foo, "bar"));
        assertEquals(4, JsonUtils.getInt(foo, "bar", 4));
        assertNull(JsonUtils.getInt(foo, "baz"));
        assertEquals(5, JsonUtils.getInt(foo, "baz", 5));
    }
}
