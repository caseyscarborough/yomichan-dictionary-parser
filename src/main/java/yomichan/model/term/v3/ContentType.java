package yomichan.model.term.v3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ContentType {

    TEXT("text"),
    IMAGE("image"),
    STRUCTURED_CONTENT("structured-content");

    private final String value;

    public static ContentType from(String value) {
        return Arrays.stream(values())
            .filter(ct -> ct.getValue().equals(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Could not find content type for value: " + value));
    }
}
