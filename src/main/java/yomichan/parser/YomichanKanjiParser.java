package yomichan.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import yomichan.exception.YomichanException;
import yomichan.model.v3.Kanji;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static yomichan.utils.JsonUtils.getText;
import static yomichan.utils.JsonUtils.parseSpaceSeparatedText;
import static yomichan.utils.JsonUtils.parseStringArray;
import static yomichan.utils.JsonUtils.toMap;

@Slf4j
@RequiredArgsConstructor
class YomichanKanjiParser implements IYomichanParser<List<Kanji>> {

    private final ObjectMapper mapper;

    /**
     * Parses the Yomichan kanji_bank.json file.
     *
     * @param file The kanji_bank.json file.
     * @return The list of Kanji.
     * @see <a href="https://github.com/FooSoft/yomichan/blob/master/ext/data/schemas/dictionary-kanji-bank-v3-schema.json">Yomichan Kanji Bank v3 Schema</a>
     * @see Kanji
     */
    @Override
    public List<Kanji> parse(File file) {
        try {
            final JsonNode node = mapper.readTree(file);
            if (!node.isArray()) {
                throw new YomichanException("Yomichan kanji bank should be an array.");
            }

            log.info("Parsing Yomichan kanji bank at path {}", file.getAbsolutePath());
            final long start = System.nanoTime();
            List<Kanji> kanji = new ArrayList<>();
            node.forEach(n -> kanji.add(parseKanji(n)));
            log.debug("Successfully parsed {} kanji in {}ms", kanji.size(), TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
            return kanji;
        } catch (IOException e) {
            throw new YomichanException("Failed to parse Yomichan kanji bank at path " + file.getAbsolutePath(), e);
        }
    }

    @Override
    public YomichanParserType getType() {
        return YomichanParserType.KANJI;
    }

    private Kanji parseKanji(JsonNode node) {
        if (!node.isArray()) {
            throw new YomichanException("Yomichan kanji bank array items should be an array.");
        }

        final Kanji kanji = new Kanji();
        for (int i = 0; i < node.size(); i++) {
            JsonNode item = node.get(i);
            switch (i) {
                case 0 -> kanji.setCharacter(getText(item));
                case 1 -> kanji.setOnyomi(parseSpaceSeparatedText(item));
                case 2 -> kanji.setKunyomi(parseSpaceSeparatedText(item));
                case 3 -> kanji.setTags(parseSpaceSeparatedText(item));
                case 4 -> kanji.setMeanings(parseStringArray(item));
                case 5 -> kanji.setStats(toMap(item));
                default ->
                    throw new YomichanException("Couldn't parse kanji due to invalid length. Yomichan kanji array should be 6 items long: " + node);
            }
        }
        return kanji;
    }
}
