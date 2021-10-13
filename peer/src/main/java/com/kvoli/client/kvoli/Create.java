package comp90015a1.peer.src.main.java.com.kvoli.client.kvoli;

import com.google.gson.annotations.Expose;

public class Create{
    @Expose
    String type;
    @Expose
    String roomid;
    Create() {

    }
    Create(String Type, String roomidtmp) {
        type    = Type;
        roomid = roomidtmp;
    }

    public String getRoomID(){
        return roomid;
    }
    public void setRoomID(String newRoomID){
        roomid = newRoomID;
    }


}