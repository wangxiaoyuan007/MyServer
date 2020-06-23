package result;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
@JsonDeserialize
public class Result {
  private int code;
  private String msg;

  public int getCode(){
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }
}
