package comp90015a1.peer.src.main.java.com.kvoli.client.kvoli;

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

}
