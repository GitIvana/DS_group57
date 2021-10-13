package comp90015a1.peer.src.main.java.com.kvoli.client.kvoli;

import com.google.gson.annotations.Expose;

public class Join{
    @Expose
    String type;
    @Expose
    String roomid;
    Join() {

    }
    Join(String Type, String roomidtmp) {
        type    = Type;
        roomid = roomidtmp;
    }

    public String getRoomID(){
        return roomid;
    }


}