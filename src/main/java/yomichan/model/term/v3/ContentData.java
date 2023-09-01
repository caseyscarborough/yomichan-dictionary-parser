package yomichan.model.term.v3;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Generic data attributes that should be added to the element.
 */
@Getter
public class ContentData {

    private Map<String, String> data = new LinkedHashMap<>();

    public Map<String, String> getData() {
        return this.data;
    }

    public void setData(String name, String value) {
        this.data.put(name, value);
    }
}
