package yomichan.parser;

import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import yomichan.exception.YomichanException;
import yomichan.model.Index;
import yomichan.model.YomichanDictionary;
import yomichan.model.v3.Kanji;
import yomichan.model.v3.KanjiMetadata;
import yomichan.model.v3.Tag;
import yomichan.model.v3.Term;
import yomichan.model.v3.TermMetadata;
import yomichan.utils.FileUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static yomichan.parser.YomichanParserType.DICTIONARY;
import static yomichan.parser.YomichanParserType.INDEX;
import static yomichan.parser.YomichanParserType.KANJI;
import static yomichan.parser.YomichanParserType.KANJI_METADATA;
import static yomichan.parser.YomichanParserType.TAG;
import static yomichan.parser.YomichanParserType.TERM;
import static yomichan.parser.YomichanParserType.TERM_METADATA;
import static yomichan.parser.YomichanParserType.values;
import static yomichan.utils.FileUtils.getFiles;

@Slf4j
class YomichanDictionaryParser implements IYomichanParser<YomichanDictionary> {

    @SuppressWarnings("rawtypes")
    private final Map<YomichanParserType, IYomichanParser> parsers;

    public YomichanDictionaryParser(YomichanParserFactory factory) {
        this.parsers = Arrays.stream(values())
            .filter(type -> type != DICTIONARY)
            .collect(Collectors.toMap(type -> type, factory::getInstance));
    }

    @Override
    public YomichanDictionary parse(File file) {
        log.info("Parsing Yomichan dictionary at path: {}", file.getAbsolutePath());
        final long start = System.nanoTime();
        final String dir = FileUtils.getTempFolder();
        try (final ZipFile zip = new ZipFile(file)) {
            log.debug("Extracting Yomichan dictionary {} to {}", zip.getFile().getName(), dir);
            zip.extractAll(dir);

            // Parse the index, term_bank, and tag_bank JSON files.
            final YomichanDictionary dictionary = new YomichanDictionary();
            this.<Index>parse(dir, INDEX).stream().findFirst().ifPresent(dictionary::setIndex);
            this.<List<Kanji>>parse(dir, KANJI).forEach(dictionary.getKanjis()::addAll);
            this.<List<Term>>parse(dir, TERM).forEach(dictionary.getTerms()::addAll);
            this.<List<Tag>>parse(dir, TAG).forEach(dictionary.getTags()::addAll);
            this.<List<TermMetadata>>parse(dir, TERM_METADATA).forEach(dictionary.getTermMetadata()::addAll);
            this.<List<KanjiMetadata>>parse(dir, KANJI_METADATA).forEach(dictionary.getKanjiMetadata()::addAll);

            log.debug("Successfully parsed Yomichan {} dictionary {} in {}ms", dictionary.getType() != null ? dictionary.getType().getName() : "[Unknown]", zip.getFile().getName(), TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
            return dictionary;
        } catch (Exception e) {
            log.error("Couldn't parse Yomichan dictionary at path {}", file.getAbsolutePath(), e);
            throw new YomichanException("Failed to parse Yomichan dictionary at path " + file.getAbsolutePath(), e);
        } finally {
            getFiles(dir, (d, name) -> name.endsWith(".json"))
                .stream()
                .map(File::getAbsolutePath)
                .forEach(FileUtils::delete);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> parse(String path, YomichanParserType type) {
        final IYomichanParser<T> parser = parsers.get(type);
        return getFiles(path, (dir, name) -> name.matches(type.getPattern()))
            .stream()
            .map(parser::parse)
            .toList();
    }
}
