package yomichan;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import yomichan.exception.YomichanException;
import yomichan.model.Index;
import yomichan.model.YomichanDictionary;
import yomichan.model.v3.Kanji;
import yomichan.model.v3.Tag;
import yomichan.model.v3.Term;
import yomichan.model.v3.TermMeta;
import yomichan.model.v3.term.Content;
import yomichan.model.v3.term.ContentType;
import yomichan.model.v3.term.HtmlTag;
import yomichan.model.v3.term.StructuredContent;
import yomichan.model.v3.term.meta.Frequency;
import yomichan.model.v3.term.meta.Pitches;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class YomichanParserTest {

    private static final String FILES_ROOT = "src/test/resources/yomichan";
    private YomichanParser parser;

    @BeforeEach
    void setUp() {
        ObjectMapper mapper = new ObjectMapper();
        parser = new YomichanParser(mapper);
    }

    @Test
    void testParseIndex() {
        for (File file : getFiles((dir, name) -> name.startsWith("index"))) {
            final Index index = parser.parseIndex(file.getAbsolutePath());
            assertNotNull(index);
            assertTrue(index.getFormat() != null && index.getVersion() != null);
            assertTrue(List.of(1, 2, 3).contains(index.getFormat()));
            assertNotNull(index.getAuthor());
            assertNotNull(index.getDescription());
        }
    }

    @Test
    void testParseTerms() {
        for (File file : getFiles((dir, name) -> name.startsWith("term_bank"))) {
            final List<Term> terms = parser.parseTerms(file.getAbsolutePath());
            assertFalse(terms.isEmpty());
        }
    }

    @Test
    void testParseTags() {
        for (File file : getFiles((dir, name) -> name.startsWith("tag_bank"))) {
            final List<Tag> tags = parser.parseTags(file.getAbsolutePath());
            assertFalse(tags.isEmpty());
        }
    }

    @Test
    void testParseKanji() {
        for (File file : getFiles((dir, name) -> name.startsWith("kanji_bank"))) {
            final List<Kanji> kanji = parser.parseKanjis(file.getAbsolutePath());
            assertFalse(kanji.isEmpty());
        }
    }

    @Test
    void testParseDictionary() {
        for (File file : getFiles((dir, name) -> name.endsWith(".zip"))) {
            final YomichanDictionary dictionary = parser.parseDictionary(file.getAbsolutePath());
            assertNotNull(dictionary);
            assertNotNull(dictionary.getIndex());
            switch (dictionary.getType()) {
                case TERM -> assertFalse(dictionary.getTerms().isEmpty());
                case KANJI -> assertFalse(dictionary.getKanjis().isEmpty());
                case PITCH -> {
                    assertFalse(dictionary.getTermMetas().isEmpty());
                    dictionary.getTermMetas().forEach(meta -> assertEquals(TermMeta.Type.PITCH, meta.getType()));
                }
                case FREQUENCY -> {
                    assertFalse(dictionary.getTermMetas().isEmpty());
                    dictionary.getTermMetas().forEach(meta -> assertEquals(TermMeta.Type.FREQUENCY, meta.getType()));
                }
            }
        }
    }

    @Test
    void testParseSingleIndex() {
        File file = new File(FILES_ROOT + "/index.json");
        final Index index = parser.parseIndex(file);
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

    @Test
    void testParseSingleTag() {
        File file = new File(FILES_ROOT + "/tag_bank_1.json");
        final List<Tag> tags = parser.parseTags(file);
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

    @Test
    void testParseSingleTerm() {
        File file = new File(FILES_ROOT + "/term_bank_1.json");
        final List<Term> terms = parser.parseTerms(file);
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

    @Test
    void parseSingleKanji() {
        File file = new File(FILES_ROOT + "/kanji_bank_1.json");
        final List<Kanji> kanjis = parser.parseKanjis(file);
        assertFalse(kanjis.isEmpty());
        assertEquals(351, kanjis.size());
        Kanji kanji = kanjis.get(0);
        assertEquals("亜", kanji.getCharacter());
        assertEquals("ア", kanji.getOnyomi().get(0));
        assertEquals("つ.ぐ", kanji.getKunyomi().get(0));
        assertEquals("jouyou", kanji.getTags().get(0));
        assertEquals(4, kanji.getMeanings().size());
        assertEquals("Asia", kanji.getMeanings().get(0));
        assertEquals("3273", kanji.getStats().get("deroo"));
        assertEquals("1010.6", kanji.getStats().get("four_corner"));
        assertEquals("1509", kanji.getStats().get("freq"));
    }

    @Test
    void testParsingSingleTermMetaForPitches() {
        File file = new File(FILES_ROOT + "/term_meta_bank_2.json");
        final List<TermMeta> metas = parser.parseTermMetas(file);
        assertFalse(metas.isEmpty());
        assertEquals(510, metas.size());
        final TermMeta meta = metas.get(1);
        assertEquals(TermMeta.Type.PITCH, meta.getType());
        assertEquals("積雪地帯", meta.getText());
        final Pitches pitches = meta.getPitches();
        assertEquals("せきせつちたい", pitches.getReading());
        assertEquals(1, pitches.getPitches().size());
        assertEquals(5, pitches.getPitches().get(0).getDownstep());
    }

    @Test
    void testParsingSingleTermMetaForFrequency() {
        File file = new File(FILES_ROOT + "/term_meta_bank_1.json");
        final List<TermMeta> metas = parser.parseTermMetas(file);
        assertFalse(metas.isEmpty());
        assertEquals(156, metas.size());
        final TermMeta meta = metas.get(0);
        assertEquals(TermMeta.Type.FREQUENCY, meta.getType());
        assertEquals("の", meta.getText());
        final Frequency frequency = meta.getFrequency();
        assertEquals(1, frequency.getValue());
        assertEquals("1㋕", frequency.getDisplay());
    }

    @Test
    void testParseTermWhenFileNotFound() {
        assertThrows(YomichanException.class, () -> parser.parseTerms("does_not_exist.json"));
    }

    private List<File> getFiles(FilenameFilter filter) {
        File file = new File(FILES_ROOT);
        final File[] files = file.listFiles(filter);
        if (files == null) {
            return new ArrayList<>();
        }
        return Arrays.stream(files).sorted(Comparator.comparing(File::getName)).toList();
    }
}
