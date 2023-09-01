package yomichan.model.v3.term;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * Controls the appearance of the image. The "monochrome" value will mask the opaque parts of the image using the current text color.
 */
@Getter
@RequiredArgsConstructor
public enum Appearance {

    AUTO("auto"),
    MONOCHROME("monochrome");

    private final String value;

    public static Appearance from(String value) {
        return Arrays.stream(values())
            .filter(a -> a.getValue().equals(value))
            .findFirst()
            .orElse(getDefault());
    }

    public static Appearance getDefault() {
        return AUTO;
    }
}
