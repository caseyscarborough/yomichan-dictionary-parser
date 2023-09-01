package yomichan.model;

import lombok.Getter;
import lombok.Setter;
import yomichan.model.tag.v3.Tag;
import yomichan.model.term.v3.Term;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class YomichanDictionary {
    private Index index;
    private List<Tag> tags = new ArrayList<>();
    private List<Term> terms = new ArrayList<>();
}
