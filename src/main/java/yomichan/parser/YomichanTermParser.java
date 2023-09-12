package yomichan.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import yomichan.exception.YomichanException;
import yomichan.model.v3.Term;
import yomichan.model.v3.term.Appearance;
import yomichan.model.v3.term.Content;
import yomichan.model.v3.term.ContentData;
import yomichan.model.v3.term.ContentStyle;
import yomichan.model.v3.term.ContentType;
import yomichan.model.v3.term.FontStyle;
import yomichan.model.v3.term.FontWeight;
import yomichan.model.v3.term.HtmlTag;
import yomichan.model.v3.term.ImageRendering;
import yomichan.model.v3.term.SizeUnits;
import yomichan.model.v3.term.StructuredContent;
import yomichan.model.v3.term.TextAlign;
import yomichan.model.v3.term.TextDecorationLine;
import yomichan.model.v3.term.VerticalAlign;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static yomichan.utils.JsonUtils.getBoolean;
import static yomichan.utils.JsonUtils.getDouble;
import static yomichan.utils.JsonUtils.getInt;
import static yomichan.utils.JsonUtils.getText;
import static yomichan.utils.JsonUtils.parseSpaceSeparatedText;

@Slf4j
@RequiredArgsConstructor
class YomichanTermParser implements IYomichanParser<List<Term>> {

    private final ObjectMapper mapper;

    @Override
    public List<Term> parse(File file) {
        try {
            final JsonNode node = mapper.readTree(file);
            if (!node.isArray()) {
                throw new YomichanException("Yomichan term bank should be an array.");
            }

            log.info("Parsing Yomichan term bank at path {}", file.getAbsolutePath());
            final long start = System.nanoTime();
            final List<Term> terms = new ArrayList<>();
            node.forEach(n -> terms.add(parseTerm(n)));
            log.debug("Successfully parsed {} terms in {}ms", terms.size(), TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
            return terms;
        } catch (IOException e) {
            throw new YomichanException("Failed to parse Yomichan term bank at path " + file.getAbsolutePath(), e);
        }
    }

    private Term parseTerm(JsonNode node) {
        if (!node.isArray()) {
            throw new YomichanException("Yomichan term bank array items should start with an array.");
        }
        final Term term = new Term();
        for (int i = 0; i < node.size(); i++) {
            JsonNode item = node.get(i);
            switch (i) {
                case 0 -> term.setTerm(getText(item));
                case 1 -> term.setReading(getText(item));
                case 2 -> term.setDefinitionTags(parseSpaceSeparatedText(item));
                case 3 -> term.setRules(parseSpaceSeparatedText(item));
                case 4 -> term.setScore(getInt(item));
                case 5 -> term.setContents(parseContents(item));
                case 6 -> term.setSequenceNumber(getInt(item));
                case 7 -> term.setTermTags(parseSpaceSeparatedText(item));
                default ->
                    throw new YomichanException("Couldn't parse term due to invalid length. Yomichan term array should be 8 items long: " + node);
            }
        }
        return term;
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
            default -> throw new YomichanException("Content node should only be a string or object: " + node);
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
            default -> throw new YomichanException("Unimplemented Yomichan content type: " + contentType);
        }
    }

    private void parseStructuredContent(Content root, JsonNode node) {
        log.trace("Parsing structured content: {}", node.toString());
        switch (node.getNodeType()) {
            case STRING -> root.getContents().add(new StructuredContent((node.asText())));
            case ARRAY -> node.forEach(n -> parseStructuredContent(root, n));
            case OBJECT -> root.getContents().add(parseStructuredContentObject(node));
            default -> throw new YomichanException("Unexpected node type in Term Structured Content: " + node);
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
            default -> throw new YomichanException("Unimplemented tag in Term Structured Content: " + node);
        }
        return content;
    }

    private void parseTextDecorationLine(ContentStyle style, JsonNode tdl) {
        if (tdl == null) return;
        switch (tdl.getNodeType()) {
            case STRING -> style.getTextDecorationLine().add(TextDecorationLine.from(tdl.asText()));
            case ARRAY -> tdl.forEach(t -> parseTextDecorationLine(style, t));
            default ->
                throw new YomichanException("Only expecting string or array for textDecorationLine in Yomichan structured content style: " + tdl);
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
}
