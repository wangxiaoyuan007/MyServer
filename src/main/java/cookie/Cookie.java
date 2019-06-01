package cookie;

import lombok.Getter;
import lombok.Setter;

/**
 * @auther: wxy
 * @date: 2019/6/1 16:54
 */

public class Cookie {
    private  String key;
    private  String value;
    public Cookie(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
