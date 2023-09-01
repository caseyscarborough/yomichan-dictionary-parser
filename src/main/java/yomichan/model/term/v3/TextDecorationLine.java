package yomichan.model.term.v3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum TextDecorationLine {

    NONE("none"),
    UNDERLINE("underline"),
    OVERLINE("overline"),
    LINE_THROUGH("line-through");

    private final String value;

    public static TextDecorationLine from(String value) {
        return Arrays.stream(values())
            .filter(tdl -> tdl.getValue().equals(value))
            .findFirst()
            .orElse(getDefault());
    }

    public static TextDecorationLine getDefault() {
        return NONE;
    }
}
