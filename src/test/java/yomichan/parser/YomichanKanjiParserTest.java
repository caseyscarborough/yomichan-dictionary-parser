package yomichan.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import yomichan.model.v3.Kanji;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class YomichanKanjiParserTest {

    YomichanKanjiParser parser = new YomichanKanjiParser(new ObjectMapper());

    @Test
    void testParse() {
        final List<Kanji> kanjis = parser.parse("src/test/resources/yomichan/kanji_bank_1.json");
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
}
