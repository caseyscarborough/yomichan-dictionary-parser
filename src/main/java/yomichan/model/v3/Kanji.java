package yomichan.model.v3;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Kanji {

    /**
     * Kanji character.
     */
    private String character;

    /**
     * On'yomi readings for the kanji character.
     */
    private List<String> onyomi;

    /**
     * Kun'yomi readings for the kanji character.
     */
    private List<String> kunyomi;

    /**
     * Tags for the kanji character.
     */
    private List<String> tags;

    /**
     * Meanings for the kanji character.
     */
    private List<String> meanings;

    /**
     * Various stats for the kanji character.
     */
    private Map<String, String> stats = new HashMap<>();
}
