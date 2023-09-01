package yomichan.model.v3.term.meta;

import lombok.Data;

import java.util.List;

/**
 * Pitch accent information for the term and reading combination.
 */
@Data
public class Pitch {
    /**
     * Mora position of the pitch accent downstep. A value of 0 indicates that the word does not have a downstep (heiban).
     */
    private Integer downstep;
    /**
     * Positions of morae with nasal sounds.
     */
    private List<Integer> nasals;
    /**
     * Positions of morae with devoiced sounds.
     */
    private List<Integer> devoicings;
    /**
     * List of tags for this pitch accent. These typically correspond to a certain type of part of speech.
     */
    private List<String> tags;
}
