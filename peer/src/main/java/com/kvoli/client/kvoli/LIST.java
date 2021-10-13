package comp90015a1.peer.src.main.java.com.kvoli.client.kvoli;

import com.google.gson.annotations.Expose;

public class LIST{
    @Expose
    String type;

    LIST() {

    }
    LIST(String Type) {
        type    = Type;
    }

}