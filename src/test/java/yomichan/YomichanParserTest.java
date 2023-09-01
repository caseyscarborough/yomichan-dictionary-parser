package yomichan;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import yomichan.model.Index;
import yomichan.model.YomichanDictionary;
import yomichan.model.tag.v3.Tag;
import yomichan.model.term.v3.Term;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class YomichanParserTest {

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
    void testParseTermBank() {
        for (File file : getFiles((dir, name) -> name.startsWith("term_bank"))) {
            final List<Term> terms = parser.parseTerms(file.getAbsolutePath());
            assertFalse(terms.isEmpty());
        }
    }

    @Test
    void testParseTagBank() {
        for (File file: getFiles((dir, name) -> name.startsWith("tag_bank"))) {
            final List<Tag> tags = parser.parseTags(file.getAbsolutePath());
            assertFalse(tags.isEmpty());
        }
    }

    @Test
    void testParseDictionary() {
        for (File file : getFiles((dir, name) -> name.endsWith(".zip"))) {
            final YomichanDictionary dictionary = parser.parseDictionary(file.getAbsolutePath());
            assertNotNull(dictionary);
            assertNotNull(dictionary.getIndex());
            assertFalse(dictionary.getTerms().isEmpty());
        }
    }

    private List<File> getFiles(FilenameFilter filter) {
        File file = new File("src/test/resources/yomichan");
        final File[] files = file.listFiles(filter);
        if (files == null) {
            return new ArrayList<>();
        }
        return Arrays.stream(files).sorted(Comparator.comparing(File::getName)).toList();
    }
}
