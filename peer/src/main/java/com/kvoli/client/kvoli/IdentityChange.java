package comp90015a1.peer.src.main.java.com.kvoli.client.kvoli;

import com.google.gson.annotations.Expose;

public class IdentityChange{
    @Expose
    String type;
    @Expose
    String identity;
    IdentityChange() {

    }
    IdentityChange(String Type, String identityTmp) {
        type    = Type;
        identity = identityTmp;
    }

    public String getIdentity(){
        return identity;
    }


}
