package yomichan.model;

import lombok.Getter;
import lombok.Setter;
import yomichan.model.v3.Kanji;
import yomichan.model.v3.Tag;
import yomichan.model.v3.Term;
import yomichan.model.v3.TermMeta;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class YomichanDictionary {
    private Index index;
    private List<Tag> tags = new ArrayList<>();
    private List<Term> terms = new ArrayList<>();
    private List<Kanji> kanjis = new ArrayList<>();
    private List<TermMeta> termMetas = new ArrayList<>();
    private YomichanDictionaryType type;
}
