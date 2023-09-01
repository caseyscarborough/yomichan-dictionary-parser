package yomichan.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import yomichan.exception.YomichanException;
import yomichan.model.Index;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static yomichan.utils.JsonUtils.getBoolean;
import static yomichan.utils.JsonUtils.getInt;
import static yomichan.utils.JsonUtils.getText;

@Slf4j
@RequiredArgsConstructor
class YomichanIndexParser implements IYomichanParser<Index> {

    private final ObjectMapper mapper;

    @Override
    public Index parse(File file) {
        try {
            log.info("Parsing Yomichan index file at path {}", file.getAbsolutePath());
            long start = System.nanoTime();
            final JsonNode node = mapper.readTree(file);
            final Index index = new Index();
            index.setAttribution(getText(node, "attribution"));
            index.setAuthor(getText(node, "author"));
            index.setDescription(getText(node, "description"));
            index.setFormat(getInt(node, "format"));
            index.setFrequencyMode(Index.FrequencyMode.from(getText(node, "frequencyMode")));
            index.setRevision(getText(node, "revision"));
            index.setSequenced(getBoolean(node, "sequenced", false));
            index.setTitle(getText(node, "title"));
            index.setUrl(getText(node, "url"));
            index.setVersion(getInt(node, "version"));
            log.debug("Successfully parsed Yomichan index file in {}ms", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
            return index;
        } catch (IOException e) {
            throw new YomichanException("Failed to parse Yomichan index at path " + file.getAbsolutePath(), e);
        }
    }
}
