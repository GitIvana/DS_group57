package comp90015a1.peer.src.main.java.com.kvoli.client.kvoli;

import com.google.gson.annotations.Expose;

public class RoomInfo {
    @Expose
    String roomid;
    @Expose
    int count;
    RoomInfo() {

    }
    RoomInfo(String thisRoomid, int thisCount) {
        roomid = thisRoomid;
        count = thisCount;
    }
    public String getRoomid() {
        return roomid;
    }


    public int getCount() {
        return count;
    }
}
