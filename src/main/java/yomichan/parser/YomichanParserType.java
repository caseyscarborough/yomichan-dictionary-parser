package yomichan.parser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum YomichanParserType {
    INDEX("index\\.json"),
    KANJI("kanji_bank_[0-9]+\\.json"),
    TAG("tag_bank_[0-9]+\\.json"),
    TERM("term_bank_[0-9]+\\.json"),
    TERM_METADATA("term_meta_bank_[0-9]+\\.json"),
    KANJI_METADATA("kanji_meta_bank_[0-9]+\\.json"),
    DICTIONARY(".*\\.zip");

    private final String pattern;
}
