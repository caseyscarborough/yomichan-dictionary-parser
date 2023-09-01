package yomichan.model.v3;

import lombok.Getter;
import lombok.Setter;

/**
 * Information about a single tag.
 *
 * @see <a href="https://github.com/FooSoft/yomichan/blob/master/ext/data/schemas/dictionary-tag-bank-v3-schema.json">Yomichan Tag Bank v3 Schema</a>
 */
@Getter
@Setter
public class Tag {

    /**
     * Tag name.
     */
    private String name;

    /**
     * Category for the tag.
     */
    private String category;

    /**
     * Sorting order for the tag.
     */
    private Integer order;

    /**
     * Notes for the tag.
     */
    private String notes;

    /**
     * Score used to determine popularity. Negative values are more rare and positive values are more frequent. This score is also used to sort search results.
     */
    private Integer score;
}
