package yomichan.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import yomichan.model.v3.TermMetadata;

/**
 * The Yomichan dictionary type.
 */
@Getter
@RequiredArgsConstructor
public enum YomichanDictionaryType {

    TERM("Term"),
    KANJI("Kanji"),
    FREQUENCY("Frequency"),
    KANJI_FREQUENCY("Kanji Frequency"),
    PITCH("Pitch Accent");

    private final String name;

    public static YomichanDictionaryType findByMetadataType(TermMetadata.Type type) {
        return switch (type) {
            case PITCH -> PITCH;
            case FREQUENCY -> FREQUENCY;
        };
    }
}
