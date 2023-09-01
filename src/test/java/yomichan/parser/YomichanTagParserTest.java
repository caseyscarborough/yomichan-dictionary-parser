package yomichan.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import yomichan.model.v3.Tag;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class YomichanTagParserTest {

    YomichanTagParser parser = new YomichanTagParser(new ObjectMapper());

    @Test
    void testParse() {
        final List<Tag> tags = parser.parse("src/test/resources/yomichan/tag_bank_1.json");
        assertFalse(tags.isEmpty());
        assertEquals(312, tags.size());

        // ["arch","archaism",-4,"archaic",3]
        final Tag tag = tags.stream().filter(t -> t.getName().equals("arch")).findFirst().orElseThrow(() -> new IllegalStateException("Could not find expected tag in tag_bank_1.json"));
        assertEquals("arch", tag.getName());
        assertEquals("archaism", tag.getCategory());
        assertEquals(-4, tag.getOrder());
        assertEquals("archaic", tag.getNotes());
        assertEquals(3, tag.getScore());
    }
}
