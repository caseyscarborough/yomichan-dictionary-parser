package yomichan.parser;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import yomichan.exception.YomichanException;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RequiredArgsConstructor
abstract class BaseYomichanParserTest<T> {

    final YomichanParserType type;
    final IYomichanParser<T> parser;

    @Test
    void testParsingObject() {
        if (type == YomichanParserType.INDEX) {
            assertTrue(true);
            return;
        }
        File file = new File("src/test/resources/yomichan/object.json");
        assertThrows(YomichanException.class, () -> parser.parse(file));
    }

    @Test
    void testParsingNotFound() {
        File file = new File("src/test/resources/yomichan/not_found.json");
        assertThrows(YomichanException.class, () -> parser.parse(file));
    }
}
