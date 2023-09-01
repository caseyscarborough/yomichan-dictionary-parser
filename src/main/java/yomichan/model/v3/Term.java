package yomichan.model.v3;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import yomichan.model.v3.term.Content;

import java.util.ArrayList;
import java.util.List;

/**
 * The object representation of a term from the term_bank.json files.
 *
 * @see <a href="https://github.com/FooSoft/yomichan/blob/master/ext/data/schemas/dictionary-term-bank-v3-schema.json">Yomichan Term Bank v3 Schema</a></a>
 */
@Getter
@Setter
@ToString
public class Term {

    /**
     * The text for the term.
     */
    private String term;

    /**
     * Reading of the term, or an empty string if the reading
     * is the same as the term.
     */
    private String reading;

    /**
     * String of space-separated tags for the definition.
     * An empty string is treated as no tags.
     */
    private List<String> definitionTags = new ArrayList<>();

    /**
     * String of space-separated rule identifiers for the definition
     * which is used to validate de-inflection. Valid rule identifiers are:
     * <ul>
     *   <li>v1: ichidan verb</li>
     *   <li>v5: godan verb</li>
     *   <li>vs: suru verb</li>
     *   <li>vk: kuru verb</li>
     *   <li>adj-i: i-adjective</li>
     * </ul>
     * An empty string corresponds to words which aren't inflected, such as nouns.
     */
    private List<String> rules = new ArrayList<>();

    /**
     * Score used to determine popularity. Negative values are more rare and positive
     * values are more frequent. This score is also used to sort search results.
     */
    private Integer score;

    private List<Content> contents = new ArrayList<>();

    /**
     * Sequence number for the term. Terms with the same sequence number can be
     * shown together when the "resultOutputMode" option is set to "merge".
     */
    private Integer sequenceNumber;

    /**
     * String of space-separated tags for the term. An empty string is treated as no tags.
     */
    private List<String> termTags = new ArrayList<>();
}
