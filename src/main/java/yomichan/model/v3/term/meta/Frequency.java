package yomichan.model.v3.term.meta;

import lombok.Data;

@Data
public class Frequency {

    /**
     * The frequency value.
     */
    private Integer value;

    /**
     * The display value.
     */
    private String display;
}
