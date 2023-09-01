package yomichan.model.v3.term.meta;

import lombok.Data;

import java.util.List;

@Data
public class Pitches {

    /**
     * Reading for the term.
     */
    private String reading;

    /**
     * List of different pitch accent information for the term and reading combination.
     */
    private List<Pitch> pitches;
}
