package yomichan.model.term.v3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum FontStyle {

    NORMAL("normal"),
    ITALIC("italic");

    private final String value;

    public static FontStyle from(String value) {
        return Arrays.stream(values())
            .filter(fs -> fs.getValue().equals(value))
            .findFirst()
            .orElse(getDefault());
    }

    public static FontStyle getDefault() {
        return NORMAL;
    }
}
