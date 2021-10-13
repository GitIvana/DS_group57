package comp90015a1.peer.src.main.java.com.kvoli.client.kvoli;

import com.google.gson.annotations.Expose;

public class Message{
    @Expose
    String type;
    @Expose
    String content;
    Message() {

    }
    Message(String Type, String contenttmp) {
        type    = Type;
        content = contenttmp;
    }

    public String getContent(){
        return content;
    }


}