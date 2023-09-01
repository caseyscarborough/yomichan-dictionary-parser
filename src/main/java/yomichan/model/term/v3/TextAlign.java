package yomichan.model.term.v3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum TextAlign {

    START("start"),
    END("end"),
    LEFT("left"),
    RIGHT("right"),
    CENTER("center"),
    JUSTIFY("justify"),
    JUSTIFY_ALL("justify-all"),
    MATCH_PARENT("match-parent");
    private final String value;

    public static TextAlign from(String value) {
        return Arrays.stream(values())
            .filter(ta -> ta.getValue().equals(value))
            .findFirst()
            .orElse(getDefault());
    }

    public static TextAlign getDefault() {
        return START;
    }
}
