package response;

import lombok.Getter;
import lombok.Setter;

/**
 * @auther: wxy
 * @date: 2019/6/2 13:28
 * 封装response的头信息，如 Location：xxxxx
 */
@Setter
@Getter
public class Header {
    private String key;
    private String value;

    public Header(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Header() {
    }
}
