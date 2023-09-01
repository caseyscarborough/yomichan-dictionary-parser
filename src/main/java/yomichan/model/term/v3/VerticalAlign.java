package yomichan.model.term.v3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * The vertical alignment of the image.
 */
@Getter
@RequiredArgsConstructor
public enum VerticalAlign {

    BASELINE("baseline"),
    SUB("sub"),
    SUPER("super"),
    TEXT_TOP("text-top"),
    TEXT_BOTTOM("text-bottom"),
    MIDDLE("middle"),
    TOP("top"),
    BOTTOM("bottom");

    private final String value;

    public static VerticalAlign from(String value) {
        return Arrays.stream(values())
            .filter(va -> va.getValue().equals(value))
            .findFirst()
            .orElse(getDefault());
    }

    public static VerticalAlign getDefault() {
        return BASELINE;
    }
}
