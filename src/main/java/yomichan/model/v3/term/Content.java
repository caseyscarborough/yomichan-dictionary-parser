package yomichan.model.v3.term;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Content {

    /**
     * (Required)
     */
    private ContentType type;
    /**
     * Single definition for the term. The presence of this field
     * indicates that this content is a leaf in the tree and the
     * content field for this class will be empty.
     */
    private String text;
    /**
     * Empty tags - "br"
     * Generic container tags - "ruby", "rt", "rp", "table", "thead", "tbody", "tfoot", "tr"
     * Table tags - "td", "th"
     * Container tags supporting configurable styles - "span", "div", "ol", "ul", "li"
     * Image tag - "img"
     * Link tag - "a"
     * (Required)
     */
    private List<StructuredContent> contents = new ArrayList<>();
    /**
     * Path to the image file in the archive.
     * (Required)
     */
    private String path;
    /**
     * Preferred width of the image.
     */
    private Double width;
    /**
     * Preferred width of the image.
     */
    private Double height;
    /**
     * Hover text for the image.
     */
    private String title;
    /**
     * Description of the image.
     */
    private String description;
    /**
     * Whether or not the image should appear pixelated at sizes larger than the image's native resolution.
     */
    private Boolean pixelated;
    /**
     * Controls how the image is rendered. The value of this field supersedes the pixelated field.
     */
    private ImageRendering imageRendering;
    /**
     * Controls the appearance of the image. The "monochrome" value will mask the opaque parts of the image using the current text color.
     */
    private Appearance appearance;
    /**
     * Whether or not a background color is displayed behind the image.
     */
    private Boolean background;
    /**
     * Whether or not the image is collapsed by default.
     */
    private Boolean collapsed;
    /**
     * Whether or not the image can be collapsed.
     */
    private Boolean collapsible;

    public Content(String text) {
        this.text = text;
        this.type = ContentType.TEXT;
    }

    public String getText() {
        // Handle the case where content is a single text node
        if (contents.size() == 1 && contents.get(0).getType() == ContentType.TEXT) {
            return contents.get(0).getText();
        }
        return text;
    }
}
