package comp90015a1.peer.src.main.java.com.kvoli.client.kvoli;

import com.google.gson.annotations.Expose;

import java.util.Vector;

public class RoomContent {
    @Expose
    String type;
    @Expose
    String roomid;
    @Expose
    Vector<String> identities;
    @Expose
    String owner;
    RoomContent() {

    }
    public Vector<String> getIdentities(){
        return this.identities;
    }


    RoomContent(String newType,String roomid,Vector<String> identities, String owner) {
        this.identities = identities;
        this.type = newType;
        this.owner = owner;
        this.roomid = roomid;
    }
}
