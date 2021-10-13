package comp90015a1.peer.src.main.java.com.kvoli.server.kvoli;

import com.google.gson.annotations.Expose;

public class NewIdentity{
    @Expose
    String type;
    @Expose
    String former;
    @Expose
    String identity;
    NewIdentity() {

    }
    NewIdentity(String identityTmp, String NewType) {
        identity = identityTmp;
        former = "";
        type    = NewType;
    }

    public String getIdentity(){
        return identity;
    }

    public String getFormer(){
        return former;
    }

    public void setType(String newType) {
        type = newType;
    }

    public void setFormer(String newFormer) {
        former = newFormer;
    }

    public void setIdentity(String newIdentity) {
        identity = newIdentity;
    }

}

class MessageGuest {
    @Expose
    String type;
    @Expose
    String identity;
    @Expose
    String content;

    public String getIdentity() {
        return identity;
    }

    public String getContent() {
        return content;
    }

    public String getType() {
        return type;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setType(String type) {
        this.type = type;
    }
}
