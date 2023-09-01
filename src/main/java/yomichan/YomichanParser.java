package yomichan;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import yomichan.exception.YomichanException;
import yomichan.model.Index;
import yomichan.model.YomichanDictionary;
import yomichan.model.v3.Kanji;
import yomichan.model.v3.Tag;
import yomichan.model.v3.Term;
import yomichan.model.v3.TermMetadata;
import yomichan.parser.IYomichanParser;
import yomichan.parser.YomichanParserFactory;
import yomichan.parser.YomichanParserType;
import yomichan.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static yomichan.parser.YomichanParserType.INDEX;
import static yomichan.parser.YomichanParserType.KANJI;
import static yomichan.parser.YomichanParserType.TAG;
import static yomichan.parser.YomichanParserType.TERM;
import static yomichan.parser.YomichanParserType.TERM_META;
import static yomichan.parser.YomichanParserType.values;
import static yomichan.utils.FileUtils.getFile;
import static yomichan.utils.FileUtils.getFiles;

/**
 * Class for parsing Yomichan dictionary files.
 *
 * <p>Examples:</p>
 *
 * <h4>Parse a Dictionary File</h5>
 *
 * <pre>{@code
 *     YomichanParser parser = new YomichanParser();
 *     YomichanDictionary dictionary = parser.parseDictionary("/path/to/yomichan/dictionary.zip");
 * }</pre>
 *
 * <h4>Parse Individually Extracted Files</h5>
 *
 * <pre>{@code
 *     // Parse an extracted index.json file
 *     Index index = parser.parseIndex("/path/to/yomichan/index.json");
 *
 *     // Parse an extracted kanji_bank.json file
 *     List<Kanji> kanji = parser.parseKanjis("/path/to/yomichan/kanji_bank.json");
 *
 *     // Parse an extracted tag_bank.json file
 *     List<Tag> tags = parser.parseTags("/path/to/yomichan/tag_bank.json");
 *
 *     // Parse an extracted term_bank.json file
 *     List<Term> terms = parser.parseTerms("/path/to/yomichan/term_bank.json");
 *
 *     // Parse an extracted term_meta_bank.json file
 *     List<TermMetadata> termMeta = parser.parseTermMetadata("/path/to/yomichan/term_meta_bank.json");
 * }</pre>
 *
 * @see <a href="https://github.com/caseyscarborough/yomichan-dictionary-parser">yomichan-dictionary-parser on GitHub</a>
 * @see <a href="https://github.com/FooSoft/yomichan/tree/master/ext/data/schemas">Yomichan JSON Schema Definitions</a>
 */
@Slf4j
public class YomichanParser {

    @SuppressWarnings("rawtypes")
    private final Map<YomichanParserType, IYomichanParser> parsers = new EnumMap<>(YomichanParserType.class);

    public YomichanParser() {
        this(new ObjectMapper());
    }

    public YomichanParser(ObjectMapper mapper) {
        YomichanParserFactory factory = new YomichanParserFactory(mapper);
        Arrays.stream(values()).forEach(type -> parsers.put(type, factory.getInstance(type)));
    }

    /**
     * Parse the Yomichan dictionary .zip file.
     *
     * @param path The path to the Yomichan dictionary file.
     * @return the parsed Yomichan dictionary.
     */
    public YomichanDictionary parseDictionary(final String path) {
        return this.parseDictionary(getFile(path));
    }

    /**
     * Parse the Yomichan dictionary .zip file.
     *
     * @param file The Yomichan dictionary file.
     * @return the parsed Yomichan dictionary.
     */
    public YomichanDictionary parseDictionary(final File file) {
        log.info("Parsing Yomichan dictionary at path: {}", file.getAbsolutePath());
        final long start = System.nanoTime();
        final String dir = FileUtils.getTempFolder();
        try (final ZipFile zip = new ZipFile(file)) {
            log.debug("Extracting Yomichan dictionary {} to {}", zip.getFile().getName(), dir);
            zip.extractAll(dir);

            // Parse the index, term_bank, and tag_bank JSON files.
            final YomichanDictionary dictionary = new YomichanDictionary();
            this.<Index>parse(dir, INDEX).stream().findFirst().ifPresent(dictionary::setIndex);
            this.<List<Kanji>>parse(dir, KANJI).forEach(kanji -> dictionary.getKanjis().addAll(kanji));
            this.<List<Term>>parse(dir, TERM).forEach(term -> dictionary.getTerms().addAll(term));
            this.<List<Tag>>parse(dir, TAG).forEach(tag -> dictionary.getTags().addAll(tag));
            this.<List<TermMetadata>>parse(dir, TERM_META).forEach(termMeta -> dictionary.getTermMetadata().addAll(termMeta));

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

    /**
     * Parses the Yomichan index.json file.
     *
     * @param file The index.json file.
     * @return the Index object.
     * @see <a href="https://github.com/FooSoft/yomichan/blob/master/ext/data/schemas/dictionary-index-schema.json">Yomichan Index Schema</a>
     * @see Index
     */
    @SuppressWarnings("unchecked")
    public Index parseIndex(File file) {
        IYomichanParser<Index> parser = parsers.get(INDEX);
        return parser.parse(file);
    }

    /**
     * Parses the Yomichan index.json file.
     *
     * @param path The path to the index.json file.
     * @return the Index object.
     * @see <a href="https://github.com/FooSoft/yomichan/blob/master/ext/data/schemas/dictionary-index-schema.json">Yomichan Index Schema</a>
     * @see Index
     */
    public Index parseIndex(String path) {
        final File file = getFile(path);
        return parseIndex(file);
    }

    /**
     * Parses the Yomichan kanji_bank.json file.
     *
     * @param file The kanji_bank.json file.
     * @return The list of Kanji.
     * @see <a href="https://github.com/FooSoft/yomichan/blob/master/ext/data/schemas/dictionary-kanji-bank-v3-schema.json">Yomichan Kanji Bank v3 Schema</a>
     * @see Kanji
     */
    @SuppressWarnings("unchecked")
    public List<Kanji> parseKanjis(File file) {
        IYomichanParser<List<Kanji>> parser = parsers.get(KANJI);
        return parser.parse(file);
    }

    /**
     * Parses the Yomichan kanji_bank.json file.
     *
     * @param path The path to the kanji_bank.json file.
     * @return The list of Kanji.
     * @see <a href="https://github.com/FooSoft/yomichan/blob/master/ext/data/schemas/dictionary-kanji-bank-v3-schema.json">Yomichan Kanji Bank v3 Schema</a>
     * @see Kanji
     */
    public List<Kanji> parseKanjis(String path) {
        final File file = getFile(path);
        return parseKanjis(file);
    }

    /**
     * Parse the tags from the tag_bank.json file.
     *
     * @param file The tag_bank.json file.
     * @return the list of Tags.
     * @see <a href="https://github.com/FooSoft/yomichan/blob/master/ext/data/schemas/dictionary-tag-bank-v3-schema.json">Yomichan Tag Bank v3 Schema</a>
     * @see Tag
     */
    @SuppressWarnings("unchecked")
    public List<Tag> parseTags(File file) {
        final IYomichanParser<List<Tag>> parser = parsers.get(TAG);
        return parser.parse(file);
    }

    /**
     * Parse the tags from the tag_bank.json file.
     *
     * @param path The path to the tag_bank.json file.
     * @return the list of Tags.
     * @see <a href="https://github.com/FooSoft/yomichan/blob/master/ext/data/schemas/dictionary-tag-bank-v3-schema.json">Yomichan Tag Bank v3 Schema</a>
     * @see Tag
     */
    public List<Tag> parseTags(String path) {
        final File file = getFile(path);
        return parseTags(file);
    }

    /**
     * Parses the term_bank.json file in the Yomichan dictionary.
     *
     * @param file The term_bank.json file.
     * @return the parsed terms from the file.
     * @see <a href="https://github.com/FooSoft/yomichan/blob/master/ext/data/schemas/dictionary-term-bank-v3-schema.json">Yomichan Term Bank v3 Schema</a>
     * @see Term
     */
    @SuppressWarnings("unchecked")
    public List<Term> parseTerms(File file) {
        final IYomichanParser<List<Term>> parser = parsers.get(TERM);
        return parser.parse(file);
    }

    /**
     * Parses the term_bank.json file in the Yomichan dictionary.
     *
     * @param path The path to the term_bank.json file.
     * @return the parsed terms from the file.
     * @see <a href="https://github.com/FooSoft/yomichan/blob/master/ext/data/schemas/dictionary-term-bank-v3-schema.json">Yomichan Term Bank v3 Schema</a>
     * @see Term
     */
    public List<Term> parseTerms(String path) {
        final File file = getFile(path);
        return parseTerms(file);
    }

    /**
     * Parse the term_meta_bank.json file.
     *
     * @param file The term_meta_bank.json file.
     * @return The parsed term "meta" entries.
     * @see <a href="https://github.com/FooSoft/yomichan/blob/master/ext/data/schemas/dictionary-term-meta-bank-v3-schema.json">Yomichan Term Meta Bank v3 Schema</a>
     * @see TermMetadata
     */
    @SuppressWarnings("unchecked")
    public List<TermMetadata> parseTermMetadata(File file) {
        final IYomichanParser<List<TermMetadata>> parser = parsers.get(TERM_META);
        return parser.parse(file);
    }

    /**
     * Parse the term_meta_bank.json file.
     *
     * @param path The path to the term_meta_bank.json file.
     * @return The parsed term "meta" entries.
     * @see <a href="https://github.com/FooSoft/yomichan/blob/master/ext/data/schemas/dictionary-term-meta-bank-v3-schema.json">Yomichan Term Meta Bank v3 Schema</a>
     * @see TermMetadata
     */
    public List<TermMetadata> parseTermMetadata(String path) {
        final File file = getFile(path);
        return parseTermMetadata(file);
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> parse(String path, YomichanParserType type) {
        final IYomichanParser<T> parser = parsers.get(type);
        final List<File> files = getFiles(path, (dir, name) -> name.matches(type.getPattern()));
        List<T> output = new ArrayList<>();
        for (File file : files) {
            final T parsed = parser.parse(file);
            output.add(parsed);
        }
        return output;
    }
}
