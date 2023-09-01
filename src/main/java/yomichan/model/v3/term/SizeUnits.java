package yomichan.model.v3.term;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * The units for the width and height.
 */
@Getter
@RequiredArgsConstructor
public enum SizeUnits {

    PX("px"),
    EM("em");

    private final String value;

    public static SizeUnits from(String value) {
        return Arrays.stream(values())
            .filter(su -> su.getValue().equals(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Could not find size units found for value: " + value));
    }
}
