package yomichan.model;

import lombok.Data;
import yomichan.model.v3.Kanji;
import yomichan.model.v3.KanjiMetadata;
import yomichan.model.v3.Tag;
import yomichan.model.v3.Term;
import yomichan.model.v3.TermMetadata;

import java.util.ArrayList;
import java.util.List;

@Data
public class YomichanDictionary {

    private Index index;
    private List<Tag> tags = new ArrayList<>();
    private List<Term> terms = new ArrayList<>();
    private List<TermMetadata> termMetadata = new ArrayList<>();
    private List<Kanji> kanjis = new ArrayList<>();
    private List<KanjiMetadata> kanjiMetadata = new ArrayList<>();

    public YomichanDictionaryType getType() {
        if (!terms.isEmpty()) {
            return YomichanDictionaryType.TERM;
        }
        if (!kanjis.isEmpty()) {
            return YomichanDictionaryType.KANJI;
        }
        if (!termMetadata.isEmpty()) {
            return YomichanDictionaryType.findByMetadataType(termMetadata.get(0).getType());
        }
        if (!kanjiMetadata.isEmpty()) {
            return YomichanDictionaryType.KANJI_FREQUENCY;
        }
        throw new IllegalStateException("Could not determine dictionary type.");
    }
}
