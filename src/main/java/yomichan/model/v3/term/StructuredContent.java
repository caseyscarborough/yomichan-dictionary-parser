package yomichan.model.v3.term;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 * Empty tags - "br"
 * Generic container tags - "ruby", "rt", "rp", "table", "thead", "tbody", "tfoot", "tr"
 * Table tags - "td", "th"
 * Container tags supporting configurable styles - "span", "div", "ol", "ul", "li"
 * Image tag - "img"
 * Link tag - "a"
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class StructuredContent extends Content {

    /**
     * (Required)
     */
    private HtmlTag tag;
    /**
     * Generic data attributes that should be added to the element.
     */
    private ContentData data;
    /**
     * The style of the content.
     */
    private ContentStyle style;
    /**
     * Defines the language of an element in the format defined by RFC 5646.
     */
    private String lang;
    /**
     * Column span used with td and th
     */
    private Integer colSpan;
    /**
     * Row span used with td and th
     */
    private Integer rowSpan;
    /**
     * The vertical alignment of the image.
     */
    private VerticalAlign verticalAlign;
    /**
     * The units for the width and height.
     */
    private SizeUnits sizeUnits;
    /**
     * The URL for the link. URLs starting with a ? are treated as internal links to other dictionary content.
     */
    private String href;

    public StructuredContent(String text) {
        super(text);
    }
}
