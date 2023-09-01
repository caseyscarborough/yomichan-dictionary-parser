package yomichan.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import yomichan.exception.YomichanException;
import yomichan.model.v3.TermMetadata;
import yomichan.model.v3.term.meta.Frequency;
import yomichan.model.v3.term.meta.Pitch;
import yomichan.model.v3.term.meta.Pitches;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static yomichan.utils.JsonUtils.getInt;
import static yomichan.utils.JsonUtils.getText;
import static yomichan.utils.JsonUtils.parseIntegerArray;
import static yomichan.utils.JsonUtils.parseStringArray;

@Slf4j
@RequiredArgsConstructor
class YomichanTermMetadataParser implements IYomichanParser<List<TermMetadata>> {

    private final ObjectMapper mapper;

    /**
     * Parse the term_meta_bank.json file.
     *
     * @param file The term_meta_bank.json file.
     * @return The parsed term "meta" entries.
     * @see <a href="https://github.com/FooSoft/yomichan/blob/master/ext/data/schemas/dictionary-term-meta-bank-v3-schema.json">Yomichan Term Meta Bank v3 Schema</a>
     * @see TermMetadata
     */
    @Override
    public List<TermMetadata> parse(File file) {
        try {
            JsonNode root = mapper.readTree(file);
            if (!root.isArray()) {
                throw new YomichanException("Yomichan term meta bank should be an array.");
            }

            log.info("Parsing Yomichan term meta bank at path {}", file.getAbsolutePath());
            final long start = System.nanoTime();
            final List<TermMetadata> metas = new ArrayList<>();
            root.forEach(n -> metas.add(parseTermMeta(n)));
            log.debug("Successfully parsed {} term metas in {}ms", metas.size(), TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
            return metas;
        } catch (IOException e) {
            throw new YomichanException("Failed to parse Yomichan term meta bank at path " + file.getAbsolutePath(), e);
        }
    }

    @Override
    public YomichanParserType getType() {
        return YomichanParserType.TERM_META;
    }

    private TermMetadata parseTermMeta(JsonNode node) {
        if (!node.isArray()) {
            throw new YomichanException("Yomichan term meta bank array items should be an array.");
        }
        TermMetadata meta = new TermMetadata();
        for (int i = 0; i < node.size(); i++) {
            JsonNode item = node.get(i);
            switch (i) {
                case 0 -> meta.setText(getText(item));
                case 1 -> meta.setType(TermMetadata.Type.from(getText(item)));
                case 2 -> parsePitchesOrFrequency(meta, item);
                default ->
                    throw new YomichanException("Couldn't parse term meta due to invalid length. Yomichan term meta array should be 3 items long: " + node);
            }
        }
        return meta;
    }

    private void parsePitchesOrFrequency(TermMetadata meta, JsonNode node) {
        switch (meta.getType()) {
            case PITCH -> meta.setPitches(parsePitches(node));
            case FREQUENCY -> meta.setFrequency(parseFrequency(node));
        }
    }

    private Frequency parseFrequency(JsonNode node) {
        if (!node.isObject()) {
            throw new YomichanException("Yomichan frequency metadata should be an object.");
        }
        Frequency frequency = new Frequency();
        frequency.setValue(getInt(node, "value"));
        frequency.setDisplay(getText(node, "displayValue"));
        return frequency;
    }

    private Pitches parsePitches(JsonNode node) {
        if (!node.isObject()) {
            throw new YomichanException("Yomichan pitch metadata should be an object.");
        }
        Pitches pitches = new Pitches();
        pitches.setReading(getText(node, "reading"));
        pitches.setPitches(parsePitchArray(node.get("pitches")));
        return pitches;
    }

    private List<Pitch> parsePitchArray(JsonNode node) {
        if (!node.isArray()) {
            throw new YomichanException("Yomichan pitches should be an array.");
        }
        List<Pitch> pitches = new ArrayList<>();
        node.forEach(n -> pitches.add(parsePitch(n)));
        return pitches;
    }

    private Pitch parsePitch(JsonNode node) {
        if (!node.isObject()) {
            throw new YomichanException("Yomichan pitch should be an object.");
        }
        Pitch pitch = new Pitch();
        pitch.setDownstep(getInt(node.get("position")));
        pitch.setNasals(parseIntegerArray(node.get("nasal")));
        pitch.setDevoicings(parseIntegerArray(node.get("devoice")));
        pitch.setTags(parseStringArray(node.get("tags")));
        return pitch;
    }
}
