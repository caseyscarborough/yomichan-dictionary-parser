package yomichan.model.v3.term;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum HtmlTag {

    BR("br"),
    RUBY("ruby"),
    RT("rt"),
    RP("rp"),
    TABLE("table"),
    THEAD("thead"),
    TBODY("tbody"),
    TFOOT("tfoot"),
    TR("tr"),
    TD("td"),
    TH("th"),
    SPAN("span"),
    DIV("div"),
    OL("ol"),
    UL("ul"),
    LI("li"),
    IMG("img"),
    A("a");

    private final String value;

    public static HtmlTag from(String value) {
        return Arrays.stream(values())
            .filter(mode -> mode.getValue().equals(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Could not find HTML tag with value: " + value));
    }
}
