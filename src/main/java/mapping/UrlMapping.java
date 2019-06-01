package mapping;

/**
 * @auther: wxy
 * @date: 2019/5/28 10:13
 */
public class UrlMapping {
    private String serverletName;
    private String urlPattern;

    public String getServerletName() {
        return serverletName;
    }

    public void setServerletName(String serverletName) {
        this.serverletName = serverletName;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }
}
