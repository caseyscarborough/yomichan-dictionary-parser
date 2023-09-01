package yomichan.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import yomichan.exception.YomichanException;
import yomichan.model.v3.Tag;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static yomichan.utils.JsonUtils.getInt;
import static yomichan.utils.JsonUtils.getText;

@Slf4j
@RequiredArgsConstructor
class YomichanTagParser implements IYomichanParser<List<Tag>> {

    private final ObjectMapper mapper;

    /**
     * Parse the tags from the tag_bank.json file.
     *
     * @param file The tag_bank.json file.
     * @return the list of Tags.
     * @see <a href="https://github.com/FooSoft/yomichan/blob/master/ext/data/schemas/dictionary-tag-bank-v3-schema.json">Yomichan Tag Bank v3 Schema</a>
     * @see Tag
     */
    @Override
    public List<Tag> parse(File file) {
        try {
            final JsonNode node = mapper.readTree(file);
            if (!node.isArray()) {
                throw new YomichanException("Yomichan tag bank should be an array.");
            }

            log.info("Parsing Yomichan tag bank at path {}", file.getAbsolutePath());
            final long start = System.nanoTime();
            final List<Tag> tags = new ArrayList<>();
            node.forEach(n -> tags.add(parseTag(n)));
            log.debug("Successfully parsed {} tags in {}ms", tags.size(), TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
            return tags;
        } catch (IOException e) {
            throw new YomichanException("Failed to parse Yomichan tag bank at path " + file.getAbsolutePath(), e);
        }
    }

    @Override
    public YomichanParserType getType() {
        return YomichanParserType.TAG;
    }

    private Tag parseTag(JsonNode node) {
        if (!node.isArray()) {
            throw new YomichanException("Yomichan tag bank array items should start with an array.");
        }
        Tag tag = new Tag();
        for (int i = 0; i < node.size(); i++) {
            JsonNode n = node.get(i);
            switch (i) {
                case 0 -> tag.setName(getText(n));
                case 1 -> tag.setCategory(getText(n));
                case 2 -> tag.setOrder(getInt(n));
                case 3 -> tag.setNotes(getText(n));
                case 4 -> tag.setScore(getInt(n));
                default ->
                    throw new YomichanException("Couldn't parse tag due to invalid length. Yomichan tag array should be 5 items long: " + node);
            }
        }
        return tag;
    }
}
