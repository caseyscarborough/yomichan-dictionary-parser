package yomichan.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import yomichan.model.v3.KanjiMetadata;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class YomichanKanjiMetadataParserTest {

    YomichanKanjiMetadataParser parser = new YomichanKanjiMetadataParser(new ObjectMapper());

    @Test
    void testParse() {
        final List<KanjiMetadata> metadata = parser.parse("src/test/resources/yomichan/kanji_meta_bank_1.json");
        assertNotNull(metadata);
        assertFalse(metadata.isEmpty());
        assertEquals(1865, metadata.size());
        final KanjiMetadata meta = metadata.get(0);
        assertFrequency(metadata, "人", 1, null);
        assertFrequency(metadata, "纘", 6000, "Never used");
        assertFrequency(metadata, "蔣", 5000, "Very rare");
    }

    private void assertFrequency(List<KanjiMetadata> metadatas, String kanji, Integer frequency, String display) {
        final KanjiMetadata meta = metadatas.stream().filter(m -> m.getText().equals(kanji)).findFirst().orElseThrow();
        assertEquals(frequency, meta.getFrequency());
        assertEquals(display, meta.getDisplay());
    }
}
