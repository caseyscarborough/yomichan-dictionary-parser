package yomichan.model.v3.term;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Generic data attributes that should be added to the element.
 */
@Getter
public class ContentData {

    private final Map<String, String> data = new LinkedHashMap<>();

    public Map<String, String> getData() {
        return this.data;
    }

    public String get(String name) {
        return this.data.get(name);
    }

    public void setData(String name, String value) {
        this.data.put(name, value);
    }
}
