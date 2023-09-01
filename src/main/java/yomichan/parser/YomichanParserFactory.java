package yomichan.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class YomichanParserFactory {

    private final ObjectMapper mapper;

    public YomichanParserFactory() {
        this(new ObjectMapper());
    }

    @SuppressWarnings("unchecked")
    public <T> IYomichanParser<T> getInstance(YomichanParserType type) {
        return switch (type) {
            case KANJI -> (IYomichanParser<T>) new YomichanKanjiParser(mapper);
            case INDEX -> (IYomichanParser<T>) new YomichanIndexParser(mapper);
            case TAG -> (IYomichanParser<T>) new YomichanTagParser(mapper);
            case TERM -> (IYomichanParser<T>) new YomichanTermParser(mapper);
            case TERM_META -> (IYomichanParser<T>) new YomichanTermMetadataParser(mapper);
        };
    }
}
