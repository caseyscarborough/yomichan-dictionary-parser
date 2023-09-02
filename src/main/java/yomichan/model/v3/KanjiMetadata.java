package yomichan.model.v3;

import lombok.Data;

@Data
public class KanjiMetadata {

    private String text;

    private Integer frequency;

    private String display;
}
