package yomichan;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import yomichan.model.Index;
import yomichan.model.YomichanDictionary;
import yomichan.model.tag.v3.Tag;
import yomichan.model.term.v3.Appearance;
import yomichan.model.term.v3.Content;
import yomichan.model.term.v3.ContentData;
import yomichan.model.term.v3.ContentStyle;
import yomichan.model.term.v3.ContentType;
import yomichan.model.term.v3.FontStyle;
import yomichan.model.term.v3.FontWeight;
import yomichan.model.term.v3.HtmlTag;
import yomichan.model.term.v3.ImageRendering;
import yomichan.model.term.v3.SizeUnits;
import yomichan.model.term.v3.StructuredContent;
import yomichan.model.term.v3.Term;
import yomichan.model.term.v3.TextAlign;
import yomichan.model.term.v3.TextDecorationLine;
import yomichan.model.term.v3.VerticalAlign;
import yomichan.utils.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static yomichan.utils.JsonUtils.getBoolean;
import static yomichan.utils.JsonUtils.getDouble;
import static yomichan.utils.JsonUtils.getInt;
import static yomichan.utils.JsonUtils.getText;

@Slf4j
@RequiredArgsConstructor
public class YomichanParser {

    private final ObjectMapper mapper;

    /**
     * Parse the Yomichan dictionary .zip file.
     *
     * @param path The path to the file.
     * @return the parsed Yomichan dictionary.
     */
    public YomichanDictionary parseDictionary(final String path) {
        log.info("Parsing Yomichan dictionary at path: {}", path);
        final long start = System.nanoTime();
        final String tmp = FileUtils.getTempFolder();
        final File file = getFile(path);
        try (final ZipFile zip = new ZipFile(file)) {
            log.debug("Extracting Yomichan dictionary to {}", tmp);
            zip.extractAll(tmp);

            // Parse the index, term_bank, and tag_bank JSON files.
            final YomichanDictionary dictionary = new YomichanDictionary();
            for (File index : getFiles(tmp, (dir, name) -> name.startsWith("index"))) {
                dictionary.setIndex(parseIndex(index));
                // Only parse the first index file.
                break;
            }
            for (File term : getFiles(tmp, (dir, name) -> name.startsWith("term_bank"))) {
                dictionary.getTerms().addAll(parseTerms(term));
            }
            for (File tag : getFiles(tmp, (dir, name) -> name.startsWith("tag_bank"))) {
                dictionary.getTags().addAll(parseTags(tag));
            }

            log.debug("Successfully parsed Yomichan dictionary in {}ms", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
            return dictionary;
        } catch (Exception e) {
            log.error("Couldn't parse Yomichan dictionary at path {}", path, e);
            throw new IllegalStateException("Failed to parse Yomichan dictionary at path " + path, e);
        } finally {
            getFiles(tmp, (dir, name) -> name.endsWith(".json"))
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
    public Index parseIndex(File file) {
        try {
            log.info("Parsing Yomichan index file at path {}", file.getAbsolutePath());
            long start = System.nanoTime();
            final JsonNode node = mapper.readTree(file);
            final Index index = new Index();
            index.setAttribution(getText(node, "attribution"));
            index.setAuthor(getText(node, "author"));
            index.setDescription(getText(node, "description"));
            index.setFormat(getInt(node, "format"));
            index.setFrequencyMode(Index.FrequencyMode.from(getText(node, "frequencyMode")));
            index.setRevision(getText(node, "revision"));
            index.setSequenced(getBoolean(node, "sequenced", false));
            index.setTitle(getText(node, "title"));
            index.setUrl(getText(node, "url"));
            index.setVersion(getInt(node, "version"));
            log.debug("Successfully parsed Yomichan index file in {}ms", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
            return index;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to parse Yomichan index at path " + file.getAbsolutePath(), e);
        }
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
     * Parse the tags from the tag_bank.json file.
     *
     * @param file The tag_bank.json file.
     * @return the list of Tags.
     * @see <a href="https://github.com/FooSoft/yomichan/blob/master/ext/data/schemas/dictionary-tag-bank-v3-schema.json">Yomichan Tag Bank v3 Schema</a>
     * @see Tag
     */
    public List<Tag> parseTags(File file) {
        try {
            final JsonNode node = mapper.readTree(file);
            if (!node.isArray()) {
                throw new IllegalStateException("Yomichan tag bank should be an array.");
            }

            log.info("Parsing Yomichan tag bank at path {}", file.getAbsolutePath());
            final long start = System.nanoTime();
            final List<Tag> tags = new ArrayList<>();
            node.forEach(n -> tags.add(parseTag(n)));
            log.debug("Successfully parsed {} tags in {}ms", tags.size(), TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
            return tags;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to parse Yomichan tag bank at path " + file.getAbsolutePath(), e);
        }
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
    public List<Term> parseTerms(File file) {
        try {
            final JsonNode node = mapper.readTree(file);
            if (!node.isArray()) {
                throw new IllegalStateException("Yomichan term bank should be an array.");
            }

            log.info("Parsing Yomichan term bank at path {}", file.getAbsolutePath());
            final long start = System.nanoTime();
            final List<Term> terms = new ArrayList<>();
            node.forEach(n -> terms.add(parseTerm(n)));
            log.debug("Successfully parsed {} terms in {}ms", terms.size(), TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
            return terms;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to parse Yomichan term bank at path " + file.getAbsolutePath(), e);
        }
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

    private Term parseTerm(JsonNode node) {
        if (!node.isArray()) {
            throw new IllegalStateException("Yomichan term bank array items should start with an array.");
        }
        final Term term = new Term();
        for (int i = 0; i < node.size(); i++) {
            JsonNode item = node.get(i);
            switch (i) {
                case 0 -> term.setTerm(getText(item));
                case 1 -> term.setReading(getText(item));
                case 2 -> term.setDefinitionTags(parseTags(item));
                case 3 -> term.setInflections(parseTags(item));
                case 4 -> term.setScore(getInt(item));
                case 5 -> term.setDefinitions(parseContents(item));
                case 6 -> term.setSequenceNumber(getInt(item));
                case 7 -> term.setTermTags(parseTags(item));
                default ->
                    throw new IllegalStateException("Couldn't parse term due to invalid length. Yomichan term array should be 8 items long: " + node);
            }
        }
        return term;
    }

    private Tag parseTag(JsonNode node) {
        if (!node.isArray()) {
            throw new IllegalStateException("Yomichan tag bank array items should start with an array.");
        }
        Tag tag = new Tag();
        for (int i = 0; i < node.size(); i++) {
            JsonNode n = node.get(i);
            switch (i) {
                case 0 -> tag.setName(getText(n));
                case 1 -> tag.setCategory(getText(n));
                case 2 -> tag.setOrder(getInt(n));
                case 3 -> tag.setNotes(getText(n));
                case 4 -> tag.setScore(getInt(n));
                default ->
                    throw new IllegalStateException("Couldn't parse tag due to invalid length. Yomichan tag array should be 5 items long: " + node);
            }
        }
        return tag;
    }

    private File getFile(final String path) {
        final File file = new File(path);
        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist at path " + path);
        }
        return file;
    }

    private List<String> parseTags(JsonNode node) {
        return Arrays.stream(node.asText().split(" "))
            .filter(s -> !s.isBlank())
            .toList();
    }

    private List<Content> parseContents(JsonNode node) {
        final List<Content> output = new ArrayList<>();
        node.forEach(n -> output.add(parseContent(n)));
        return output;
    }

    private Content parseContent(JsonNode node) {
        final Content content = new Content();
        switch (node.getNodeType()) {
            case STRING -> parseContentString(content, node);
            case OBJECT -> parseContentObject(content, node);
            default -> throw new IllegalStateException("Content node should only be a string or object: " + node);
        }
        log.trace("Done parsing {} content", content.getType().getValue());
        return content;
    }

    private void parseContentString(Content content, JsonNode node) {
        content.setText(node.asText());
        content.setType(ContentType.TEXT);
    }

    private void parseContentObject(Content content, JsonNode node) {
        final JsonNode type = node.get("type");
        final ContentType contentType = ContentType.from(type.asText());
        content.setType(contentType);
        switch (contentType) {
            case TEXT -> content.setText(getText(node, "text"));
            case IMAGE -> {
                parseImageFields(content, node);
                content.setDescription(getText(node, "description"));
            }
            case STRUCTURED_CONTENT -> parseStructuredContent(content, node.get("content"));
            default -> throw new IllegalStateException("Unimplemented Yomichan content type: " + contentType);
        }
    }

    private void parseStructuredContent(Content root, JsonNode node) {
        log.trace("Parsing structured content: {}", node.toString());
        switch (node.getNodeType()) {
            case STRING -> root.setText(node.asText());
            case ARRAY -> node.forEach(n -> parseStructuredContent(root, n));
            case OBJECT -> root.getContent().add(parseStructuredContentObject(node));
            default -> throw new IllegalStateException("Unexpected node type in Term Structured Content: " + node);
        }
    }

    private StructuredContent parseStructuredContentObject(JsonNode node) {
        final StructuredContent content = new StructuredContent();
        content.setTag(HtmlTag.from(getText(node, "tag")));
        switch (content.getTag()) {
            case BR -> content.setData(parseData(node.get("data")));
            case RUBY, RT, RP, TABLE, THEAD, TBODY, TFOOT, TR -> {
                parseStructuredContent(content, node.get("content"));
                content.setData(parseData(node.get("data")));
                content.setLang(getText(node, "lang"));
            }
            case TD, TH -> {
                parseStructuredContent(content, node.get("content"));
                content.setColSpan(getInt(node, "colSpan"));
                content.setData(parseData(node.get("data")));
                content.setLang(getText(node, "lang"));
                content.setRowSpan(getInt(node, "rowSpan"));
                content.setStyle(parseStyle(node.get("style")));
            }
            case SPAN, DIV, OL, UL, LI -> {
                parseStructuredContent(content, node.get("content"));
                content.setData(parseData(node.get("data")));
                content.setLang(getText(node, "lang"));
                content.setStyle(parseStyle(node.get("style")));
            }
            case IMG -> {
                parseImageFields(content, node);
                content.setData(parseData(node.get("data")));
                content.setSizeUnits(SizeUnits.from(getText(node, "sizeUnits")));
                content.setVerticalAlign(VerticalAlign.from(getText(node, "verticalAlign")));
            }
            case A -> {
                parseStructuredContent(content, node.get("content"));
                content.setHref(getText(node, "href"));
                content.setLang(getText(node, "lang"));
            }
            default -> throw new IllegalStateException("Unimplemented tag in Term Structured Content: " + node);
        }
        return content;
    }

    private void parseTextDecorationLine(ContentStyle style, JsonNode tdl) {
        if (tdl == null) return;
        switch (tdl.getNodeType()) {
            case STRING -> style.getTextDecorationLine().add(TextDecorationLine.from(tdl.asText()));
            case ARRAY -> tdl.forEach(t -> parseTextDecorationLine(style, t));
            default ->
                throw new IllegalStateException("Only expecting string or array for textDecorationLine in Yomichan structured content style: " + tdl);
        }
    }

    private ContentStyle parseStyle(JsonNode node) {
        if (node == null) return null;
        final ContentStyle style = new ContentStyle();
        parseTextDecorationLine(style, node.get("textDecorationLine"));
        style.setFontSize(getText(node, "fontSize", "medium"));
        style.setFontStyle(FontStyle.from(getText(node, "fontStyle")));
        style.setFontWeight(FontWeight.from(getText(node, "fontWeight")));
        style.setTextAlign(TextAlign.from(getText(node, "textAlign")));
        style.setVerticalAlign(VerticalAlign.from(getText(node, "verticalAlign")));
        style.setMarginTop(getDouble(node, "marginTop", 0D));
        style.setMarginLeft(getDouble(node, "marginLeft", 0D));
        style.setMarginRight(getDouble(node, "marginRight", 0D));
        style.setMarginBottom(getDouble(node, "marginBottom", 0D));
        style.setListStyleType(getText(node, "listStyleType", "disc"));
        return style;
    }

    private ContentData parseData(JsonNode node) {
        if (node == null) return null;
        final ContentData data = new ContentData();
        node.fields().forEachRemaining(f -> data.setData(f.getKey(), f.getValue().asText()));
        return data;
    }

    private void parseImageFields(Content content, JsonNode node) {
        content.setAppearance(Appearance.from(getText(node, "appearance")));
        content.setBackground(getBoolean(node, "background", false));
        content.setCollapsed(getBoolean(node, "collapsed", false));
        content.setCollapsible(getBoolean(node, "collapsible", true));
        content.setHeight(getDouble(node, "height"));
        content.setImageRendering(ImageRendering.from(getText(node, "imageRendering")));
        content.setPath(getText(node, "path"));
        content.setPixelated(getBoolean(node, "pixelated", false));
        content.setTitle(getText(node, "title"));
        content.setWidth(getDouble(node, "width"));
    }

    private List<File> getFiles(final String path, final FilenameFilter filter) {
        File file = new File(path);
        final File[] files = file.listFiles(filter);
        if (files == null) {
            return new ArrayList<>();
        }
        return Arrays.stream(files).sorted(Comparator.comparing(File::getName)).toList();
    }
}
