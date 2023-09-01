package yomichan.model.term.v3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum FontWeight {

    NORMAL("normal"),
    BOLD("bold");
    private final String value;

    public static FontWeight from(String value) {
        return Arrays.stream(values())
            .filter(fw -> fw.getValue().equals(value))
            .findFirst()
            .orElse(getDefault());
    }

    public static FontWeight getDefault() {
        return NORMAL;
    }
}
