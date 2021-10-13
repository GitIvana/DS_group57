package comp90015a1.peer.src.main.java.com.kvoli.client.kvoli;

import com.google.gson.annotations.Expose;


public class ServerMsg {
    @Expose
    String type;
    @Expose
    String identity;
    @Expose
    String content;
    public String getidentity(){
        return this.identity;
    }
    public String getcontent(){
        return this.content;
    }
    ServerMsg() {

    }
    ServerMsg(String newType, String identity,String content) {
        this.identity = identity;
        this.type = newType;
        this.content = content;
    }
}