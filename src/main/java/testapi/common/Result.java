package testapi.common;

import java.util.HashMap;
import java.util.Map;

public class Result {
    int code;
    String body;
    public Map<String, String> head;
    public void Result(){
  //  this.setCode(0);
 //  this.setBody(null);
   //this.setHead(null);

}
    public String getBody() {
        if(body==null){
            this.body = "";
        }
        return this.body.trim();
    }

    public  Map getHead() {
        return this.head;
    }

    public  void setHead(Map head) {

        this.head = head;
    }

    public void setBody(String body) {
    if(body==null){
        this.body = "";
    }
        this.body = body;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
