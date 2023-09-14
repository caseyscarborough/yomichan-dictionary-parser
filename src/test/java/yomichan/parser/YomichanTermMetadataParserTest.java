package yomichan.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import yomichan.model.v3.TermMetadata;
import yomichan.model.v3.term.meta.Frequency;
import yomichan.model.v3.term.meta.Pitches;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class YomichanTermMetadataParserTest extends BaseYomichanParserTest<List<TermMetadata>> {

    public YomichanTermMetadataParserTest() {
        super(YomichanParserType.TERM_METADATA, new YomichanTermMetadataParser(new ObjectMapper()));
    }

    @Test
    void testParseForPitch() {
        final List<TermMetadata> metas = parser.parse("src/test/resources/yomichan/term_meta_bank_2.json");
        assertFalse(metas.isEmpty());
        assertEquals(510, metas.size());
        final TermMetadata meta = metas.get(1);
        assertEquals(TermMetadata.Type.PITCH, meta.getType());
        assertEquals("積雪地帯", meta.getText());
        final Pitches pitches = meta.getPitches();
        assertEquals("せきせつちたい", pitches.getReading());
        assertEquals(1, pitches.getPitches().size());
        assertEquals(5, pitches.getPitches().get(0).getDownstep());
    }

    @Test
    void testParseForFrequency() {
        final List<TermMetadata> metas = parser.parse("src/test/resources/yomichan/term_meta_bank_1.json");
        assertFalse(metas.isEmpty());
        assertEquals(156, metas.size());
        final TermMetadata meta = metas.get(0);
        assertEquals(TermMetadata.Type.FREQUENCY, meta.getType());
        assertEquals("の", meta.getText());
        final Frequency frequency = meta.getFrequency();
        assertEquals(1, frequency.getValue());
        assertEquals("1㋕", frequency.getDisplay());
    }

    @Test
    void testParseForFrequencyWithReading() {
        final List<TermMetadata> metas = parser.parse("src/test/resources/yomichan/term_meta_bank_3.json");
        assertFalse(metas.isEmpty());
        assertEquals(1, metas.size());

        for (TermMetadata meta : metas) {
            assertNotNull(meta.getFrequency());
            assertNotNull(meta.getFrequency().getReading());
            assertNotNull(meta.getFrequency().getValue());
            assertNotNull(meta.getFrequency().getDisplay());
        }
    }
}
