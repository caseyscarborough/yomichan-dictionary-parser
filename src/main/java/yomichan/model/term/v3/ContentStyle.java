package yomichan.model.term.v3;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ContentStyle {

    private FontStyle fontStyle = FontStyle.getDefault();
    private FontWeight fontWeight = FontWeight.getDefault();
    private String fontSize = "medium";
    private List<TextDecorationLine> textDecorationLine;
    private VerticalAlign verticalAlign = VerticalAlign.getDefault();
    private TextAlign textAlign = TextAlign.getDefault();
    private Double marginTop = 0.0D;
    private Double marginLeft = 0.0D;
    private Double marginRight = 0.0D;
    private Double marginBottom = 0.0D;
    private String listStyleType = "disc";

}
