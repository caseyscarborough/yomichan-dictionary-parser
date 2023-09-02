package yomichan.model.v3.term.meta;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Frequency {

    /**
     * The frequency value.
     */
    private Integer value;

    /**
     * The display value.
     */
    private String display;

    public Frequency(Integer value) {
        this.value = value;
    }

    public Frequency(Integer value, String display) {
        this.value = value;
        this.display = display;
    }
}
