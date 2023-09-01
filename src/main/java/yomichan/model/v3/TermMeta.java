package yomichan.model.v3;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import yomichan.model.v3.term.meta.Frequency;
import yomichan.model.v3.term.meta.Pitches;

import java.util.Arrays;

@Data
public class TermMeta {

    /**
     * The frequency information for this metadata entry.
     */
    private Frequency frequency;

    /**
     * List of different pitch accent information for the term and reading combination.
     */
    private Pitches pitches;
    /**
     * The text for the term.
     */
    private String text;
    /**
     * The type of the meta entry.
     */
    private Type type;

    @Getter
    @RequiredArgsConstructor
    public enum Type {
        FREQUENCY("freq"),
        PITCH("pitch");
        private final String value;

        public static Type from(String value) {
            return Arrays.stream(values())
                .filter(type -> type.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Could not find type for Term Meta entry: " + value));
        }
    }
}
