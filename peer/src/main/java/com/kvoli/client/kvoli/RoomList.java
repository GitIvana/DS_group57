package comp90015a1.peer.src.main.java.com.kvoli.client.kvoli;

import com.google.gson.annotations.Expose;

import java.util.Vector;

public class RoomList {
    @Expose
    String type;
    @Expose
    Vector<RoomInfo> rooms;
    RoomList() {

    }
    public Vector<RoomInfo> getrooms(){
        return this.rooms;
    }


    RoomList(String newType, Vector<RoomInfo> rooms) {
        this.rooms = rooms;
        this.type = newType;
    }
}
