package yomichan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import yomichan.exception.YomichanException;
import yomichan.model.Index;
import yomichan.model.YomichanDictionary;
import yomichan.model.v3.Kanji;
import yomichan.model.v3.Tag;
import yomichan.model.v3.Term;
import yomichan.model.v3.TermMetadata;
import yomichan.parser.YomichanParserType;
import yomichan.utils.FileUtils;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static yomichan.parser.YomichanParserType.DICTIONARY;
import static yomichan.parser.YomichanParserType.INDEX;
import static yomichan.parser.YomichanParserType.KANJI;
import static yomichan.parser.YomichanParserType.TAG;
import static yomichan.parser.YomichanParserType.TERM;

class YomichanParserTest {

    private static final String FILES_ROOT = "src/test/resources/yomichan";
    private YomichanParser parser;

    @BeforeEach
    void setUp() {
        parser = new YomichanParser();
    }

    @Test
    void testParseIndex() {
        for (File file : getFiles(INDEX)) {
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
        for (File file : getFiles(TERM)) {
            final List<Term> terms = parser.parseTerms(file.getAbsolutePath());
            assertFalse(terms.isEmpty());
        }
    }

    @Test
    void testParseTags() {
        for (File file : getFiles(TAG)) {
            final List<Tag> tags = parser.parseTags(file.getAbsolutePath());
            assertFalse(tags.isEmpty());
        }
    }

    @Test
    void testParseKanji() {
        for (File file : getFiles(KANJI)) {
            final List<Kanji> kanji = parser.parseKanjis(file.getAbsolutePath());
            assertFalse(kanji.isEmpty());
        }
    }

    @Test
    void testParseDictionary() {
        for (File file : getFiles(DICTIONARY)) {
            final YomichanDictionary dictionary = parser.parseDictionary(file.getAbsolutePath());
            assertNotNull(dictionary);
            assertNotNull(dictionary.getIndex());
            switch (dictionary.getType()) {
                case TERM -> assertFalse(dictionary.getTerms().isEmpty());
                case KANJI -> assertFalse(dictionary.getKanjis().isEmpty());
                case PITCH -> {
                    assertFalse(dictionary.getTermMetadata().isEmpty());
                    dictionary.getTermMetadata().forEach(meta -> assertEquals(TermMetadata.Type.PITCH, meta.getType()));
                }
                case FREQUENCY -> {
                    assertFalse(dictionary.getTermMetadata().isEmpty());
                    dictionary.getTermMetadata().forEach(meta -> assertEquals(TermMetadata.Type.FREQUENCY, meta.getType()));
                }
            }
        }
    }

    @Test
    void testParseTermWhenFileNotFound() {
        assertThrows(YomichanException.class, () -> parser.parseTerms("does_not_exist.json"));
    }

    private List<File> getFiles(YomichanParserType type) {
        return FileUtils.getFiles(FILES_ROOT, (dir, name) -> name.matches(type.getPattern()));
    }
}
