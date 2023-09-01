package yomichan.model.term.v3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * Controls how the image is rendered. The value of this field supersedes the pixelated field.
 */
@Getter
@RequiredArgsConstructor
public enum ImageRendering {

    AUTO("auto"),
    PIXELATED("pixelated"),
    CRISP_EDGES("crisp-edges");

    private final String value;

    public static ImageRendering from(String value) {
        return Arrays.stream(values())
            .filter(ir -> ir.getValue().equals(value))
            .findFirst()
            .orElse(getDefault());
    }

    public static ImageRendering getDefault() {
        return AUTO;
    }
}
