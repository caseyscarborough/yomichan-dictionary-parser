package yomichan.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import yomichan.model.v3.TermMeta;

/**
 * The Yomichan dictionary type.
 */
@Getter
@RequiredArgsConstructor
public enum YomichanDictionaryType {

    TERM("Term"),
    KANJI("Kanji"),
    FREQUENCY("Frequency"),
    PITCH("Pitch Accent");

    private final String name;

    public static YomichanDictionaryType findByMetaType(TermMeta.Type type) {
        return switch (type) {
            case PITCH -> PITCH;
            case FREQUENCY -> FREQUENCY;
        };
    }
}
