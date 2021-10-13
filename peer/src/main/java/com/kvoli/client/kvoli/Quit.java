package comp90015a1.peer.src.main.java.com.kvoli.client.kvoli;

import com.google.gson.annotations.Expose;

public class Quit{
    @Expose
    String type;

    Quit() {

    }
    Quit(String Type) {
        type = Type;
    }

}
