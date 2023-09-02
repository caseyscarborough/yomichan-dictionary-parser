package yomichan.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import yomichan.exception.YomichanException;
import yomichan.model.v3.KanjiMetadata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static yomichan.utils.JsonUtils.getInt;
import static yomichan.utils.JsonUtils.getText;

@Slf4j
@RequiredArgsConstructor
class YomichanKanjiMetadataParser implements IYomichanParser<List<KanjiMetadata>> {

    private final ObjectMapper mapper;

    @Override
    public List<KanjiMetadata> parse(File file) {
        try {
            JsonNode node = mapper.readTree(file);
            if (!node.isArray()) {
                throw new YomichanException("Yomichan kanji meta bank should be an array.");
            }

            log.info("Parsing Yomichan kanji meta bank at path {}", file.getAbsolutePath());
            final long start = System.nanoTime();
            List<KanjiMetadata> metadata = new ArrayList<>();
            node.forEach(n -> metadata.add(parseKanjiMetadata(n)));
            log.debug("Successfully parsed {} kanji metas in {}ms", metadata.size(), TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
            return metadata;
        } catch (IOException e) {
            throw new YomichanException("Failed to parse Yomichan kanji meta bank at path " + file.getAbsolutePath(), e);
        }
    }

    private KanjiMetadata parseKanjiMetadata(JsonNode node) {
        if (!node.isArray()) {
            throw new YomichanException("Yomichan kanji metadata items should be an array.");
        }
        KanjiMetadata meta = new KanjiMetadata();
        for (int i = 0; i < node.size(); i++) {
            JsonNode item = node.get(i);
            switch (i) {
                case 0 -> meta.setText(getText(item));
                case 1 -> {/* do nothing, all Kanji metadata types are 'freq' */}
                case 2 -> parseFrequency(meta, item);
                default -> throw new YomichanException("Yomichan kanji metadata item should only have 3 items.");
            }
        }
        return meta;
    }

    private void parseFrequency(KanjiMetadata metadata, JsonNode node) {
        switch (node.getNodeType()) {
            case NUMBER -> metadata.setFrequency(getInt(node));
            case OBJECT -> {
                metadata.setFrequency(getInt(node, "value"));
                metadata.setDisplay(getText(node, "displayValue"));
            }
            default -> throw new YomichanException("Yomichan frequency metadata should be a number or an object.");
        }
    }
}
