package yomichan.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public class Index {
    private String author;
    private Integer format;
    private Integer version;
    private String attribution;
    private String description;
    private String title;
    private boolean sequenced;
    private String url;
    private String revision;
    private FrequencyMode frequencyMode;

    public Integer getFormat() {
        return format != null ? format : version;
    }

    public Integer getVersion() {
        return version != null ? version : format;
    }

    @Getter
    @RequiredArgsConstructor
    public enum FrequencyMode {

        OCCURRENCE("occurrence-based"),
        RANK("rank-based");

        private final String value;

        public static FrequencyMode from(String value) {
            if (value == null) return null;
            return Arrays.stream(values())
                .filter(mode -> mode.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No frequency mode found for value: " + value));
        }
    }
}
