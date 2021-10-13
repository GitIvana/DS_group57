package comp90015a1.peer.src.main.java.com.kvoli.client.kvoli;

import com.google.gson.annotations.Expose;

public class RoomChange {
    @Expose
    String type;
    @Expose
    String identity;
    @Expose
    String former;
    @Expose
    String roomid;
    RoomChange() {

    }
    public String getFormer(){
        return this.former;
    }
    public String getRoomid(){
        return this.roomid;
    }
    public String getidentity(){return this.identity;}

    RoomChange(String newType, String newIdentity, String newFormer, String newRoomid) {
        this.roomid = newRoomid;
        this.type = newType;
        this.identity = newIdentity;
        this.former = newFormer;
    }
}
