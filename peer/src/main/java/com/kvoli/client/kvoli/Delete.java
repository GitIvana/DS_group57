package comp90015a1.peer.src.main.java.com.kvoli.client.kvoli;

import com.google.gson.annotations.Expose;

public class Delete{
    @Expose
    String type;
    @Expose
    String roomid;
    Delete() {

    }
    Delete(String Type, String roomidtmp) {
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