package yomichan.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import yomichan.model.v3.Term;
import yomichan.model.v3.term.Content;
import yomichan.model.v3.term.ContentType;
import yomichan.model.v3.term.HtmlTag;
import yomichan.model.v3.term.StructuredContent;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class YomichanTermParserTest {

    YomichanTermParser parser = new YomichanTermParser(new ObjectMapper());

    @Test
    void testParse() {
        final List<Term> terms = parser.parse("src/test/resources/yomichan/term_bank_1.json");
        assertFalse(terms.isEmpty());
        assertEquals(158, terms.size());

        // Test parsing a basic term
        Term term = terms.get(0);
        assertEquals("引き合わせる", term.getTerm());
        assertEquals("ひきあわせる", term.getReading());
        assertNotNull(term.getDefinitionTags());
        assertEquals(1, term.getDefinitionTags().size());
        assertEquals("forms", term.getDefinitionTags().get(0));
        assertNotNull(term.getRules());
        assertEquals(1, term.getRules().size());
        assertEquals("v1", term.getRules().get(0));
        assertEquals(4, term.getContents().size());
        Content content = term.getContents().get(0);
        assertEquals(ContentType.TEXT, content.getType());
        assertEquals("引き合わせる", content.getText());
        assertEquals(-203, term.getScore());
        assertEquals(1601510, term.getSequenceNumber());
        assertNotNull(term.getTermTags());
        assertTrue(term.getTermTags().isEmpty());

        // Test parsing a term with structured content
        term = terms.get(13);
        assertEquals("引き受ける", term.getTerm());
        assertEquals("ひきうける", term.getReading());
        final List<String> tags = term.getDefinitionTags();
        assertNotNull(tags);
        assertEquals(3, tags.size());
        assertEquals("1", tags.get(0));
        assertEquals("v1", tags.get(1));
        assertEquals("vt", tags.get(2));
        assertEquals(1, term.getRules().size());
        assertEquals("v1", term.getRules().get(0));
        assertEquals(1999800, term.getScore());
        List<Content> definitions = term.getContents();
        assertNotNull(definitions);
        assertEquals(1, definitions.size());
        content = definitions.get(0);
        assertNotNull(content);
        assertEquals(ContentType.STRUCTURED_CONTENT, content.getType());
        assertNotNull(content.getContents());
        assertEquals(2, content.getContents().size());
        List<StructuredContent> contents = content.getContents();
        final StructuredContent a = contents.get(0);
        assertNotNull(a.getContents());
        assertEquals(5, a.getContents().size());
        assertEquals("to take on", a.getContents().get(0).getText());
        assertEquals(HtmlTag.LI, a.getContents().get(0).getTag());
        assertEquals("to assume responsibility for", a.getContents().get(4).getText());
        assertEquals(HtmlTag.LI, a.getContents().get(4).getTag());
        assertEquals("glossary", a.getData().get("content"));
        assertEquals("en", a.getLang());
        assertEquals("circle", a.getStyle().getListStyleType());
        assertEquals(HtmlTag.UL, a.getTag());
        assertEquals(1601520, term.getSequenceNumber());
        assertEquals(3, term.getTermTags().size());
        assertEquals("⭐", term.getTermTags().get(0));
        assertEquals("ichi", term.getTermTags().get(1));
        assertEquals("news19k", term.getTermTags().get(2));
    }
}
