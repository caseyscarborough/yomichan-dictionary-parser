package yomichan.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import yomichan.model.Index;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class YomichanIndexParserTest extends BaseYomichanParserTest<Index> {

    public YomichanIndexParserTest() {
        super(YomichanParserType.INDEX, new YomichanIndexParser(new ObjectMapper()));
    }

    @Test
    void testParse() {
        final Index index = parser.parse("src/test/resources/yomichan/index.json");
        assertNotNull(index);
        assertEquals(3, index.getFormat());
        assertEquals(3, index.getVersion());
        assertEquals("yomichan-import", index.getAuthor());
        assertEquals("JMdict Extra", index.getTitle());
        assertEquals("https://github.com/FooSoft/yomichan-import", index.getUrl());
        assertEquals("This publication has included material from the JMdict (EDICT, etc.) dictionary files in accordance with the licence provisions of the Electronic Dictionaries Research Group. See http://www.edrdg.org/", index.getAttribution());
        assertEquals("JMdict Extra Description", index.getDescription());
        assertEquals("JMdict.2023-01-29", index.getRevision());
        assertTrue(index.isSequenced());
    }
}
