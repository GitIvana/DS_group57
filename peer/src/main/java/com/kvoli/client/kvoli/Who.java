package comp90015a1.peer.src.main.java.com.kvoli.client.kvoli;

import com.google.gson.annotations.Expose;

public class Who{
    @Expose
    String type;
    @Expose
    String roomid;
    Who() {

    }
    Who(String Type, String roomidtmp) {
        type    = Type;
        roomid = roomidtmp;
    }

    public String getRoomID(){
        return roomid;
    }


}