package enumer;

/**
 * @auther: wxy
 * @date: 2019/6/2 13:39
 */
public enum HttpStatusEnum {
    OK(200),NOT_FOUND(404),INTERNAL_SERVER_ERROR(500),BAD_REQUEST(400),MOVED_TEMPORARILY(302);
    private int code;
    HttpStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
