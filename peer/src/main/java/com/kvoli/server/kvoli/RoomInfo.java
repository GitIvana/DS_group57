package comp90015a1.peer.src.main.java.com.kvoli.server.kvoli;

import com.google.gson.annotations.Expose;

import java.util.Vector;

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

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    //get room list now
    public static Vector<RoomInfo> NewRoominfoList() {
        Vector<RoomInfo> roominfoArray = new Vector<RoomInfo>();
        for(int i = 0; i < RoomContent.roomContentArray.size(); i ++) {
            RoomInfo newRoom = new RoomInfo();
            newRoom.setRoomid(RoomContent.roomContentArray.get(i).getRoomid());
            if (RoomContent.roomContentArray.get(i).getIdentities() == null) {
                newRoom.setCount(0);
            } else
            newRoom.setCount(RoomContent.roomContentArray.get(i).getIdentities().size());
            roominfoArray.add(newRoom);
        }
        return roominfoArray;
    }

}

//roomlist json
class RoomInfoList{
    @Expose
    String type;
    @Expose
    public Vector<RoomInfo> rooms;
    RoomInfoList(String newType) {
        type = newType;
        rooms = RoomInfo.NewRoominfoList();
    }
    public void deleteRoom(String RoomID) {
        if(rooms != null) {
            for(int i = 0; i < rooms.size(); i ++) {
                if (rooms.get(i).getRoomid().equals(RoomID)) {
                    rooms.remove(i);
                    break;
                }
            }
        }

    }
    public void addRoom(String RoomID) {
        RoomInfo tmp = new RoomInfo(RoomID, 0);
        rooms.add(tmp);

    }
}

//room content json
class RoomContent{
    @Expose
    String type;
    @Expose
    String roomid;
    @Expose
    Vector<String> identities;
    @Expose
    String owner;

    RoomContent() { identities = new Vector<String>();
    }
    RoomContent(String newType, String newRoomid, Vector<String> newIdentities, String newOwner) {
        type = newType;
        roomid = newRoomid;
        owner = newOwner;
        identities = new Vector<String>();
        if (newIdentities == null) {
        }
        else
        for(int i  = 0; i < newIdentities.size(); i ++) {
            identities.add(newIdentities.get(i));
        }
    }
    public String getRoomid() {
        return roomid;
    }

    public String getOwner() {
        return owner;
    }

    public String getType() {
        return type;
    }

    public Vector<String> getIdentities() {
        return identities;
    }

    public void setIdentities(Vector<String> identities) {
        this.identities = identities;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

    public void setType(String type) {
        this.type = type;
    }
    // room online info
    public static Vector<RoomContent> roomContentArray = new Vector<RoomContent>();

    public static boolean isOwner(String Name) {
        boolean flag = false;
        for(int i = 0; i < roomContentArray.size(); i ++) {
            if (roomContentArray.get(i).getOwner().equals(Name)) {
                flag = true;
                break;
            }
        }
        return flag;
    }
    //inti roomContentArray
//    public static void Init () {
//        RoomContent MainRoom = new RoomContent();
//        MainRoom.setRoomid("MainHall");
//        MainRoom.setOwner("");
//        roomContentArray.add(MainRoom);
//    }
    // get guests from a room
    public static Vector<String> getRoomIdentity(String roomid) {
        Vector<String> result = new Vector<String>();
        for(int i = 0; i < roomContentArray.size(); i ++) {
            if (roomContentArray.get(i).getRoomid().equals(roomid)) {
                result = roomContentArray.get(i).getIdentities();
                break;
            }
        }
        return result;
    }
    //deep copy room content
    public static RoomContent getRoomContent(RoomContent nowRoomContent) {
        RoomContent result = new RoomContent();
        for(int i = 0; i < roomContentArray.size(); i ++) {
            if (roomContentArray.get(i).getRoomid().equals(nowRoomContent.getRoomid())) {
                result = roomContentArray.get(i);
                break;
            }
        }
        RoomContent deepCopyRoomcontent = new RoomContent(result.getType(), result.getRoomid(),
                result.getIdentities(), result.getOwner());
        return deepCopyRoomcontent;
    }
    public static void AddRoom(String NewRoomID, String NewOwner) {
        roomContentArray.add(new RoomContent("", NewRoomID, new Vector<String>(), NewOwner));
    }
    // check whether create room name is valid
    public static boolean CheckNewRoonId(String newRoomId) {
        boolean flag = true;
        for(int i = 0; i < roomContentArray.size(); i ++) {
            if (roomContentArray.get(i).getRoomid().equals(newRoomId)) {
                flag = false;
                break;
            }
        }
        boolean is_matcher = newRoomId != null && newRoomId.matches("[a-zA-Z0-9]{3,32}");
        return (flag & is_matcher);
    }
    // check whether change room name is valid
    public static boolean CheckChange(String newRoomId) {
        boolean flag = false;
        for(int i = 0; i < roomContentArray.size(); i ++) {
            if (roomContentArray.get(i).getRoomid().equals(newRoomId)) {
                flag = true;
                break;
            }
        }
        return flag;
    }
    // join method
    public static RoomChange SwapRoom (String GuestName, String toRoom) {
        GuestInfo guest = GuestInfo.GetUserinfo(GuestName);
        RoomChange rc = new RoomChange("roomchange", GuestName, guest.getRoomID(), toRoom);
        if(guest.getRoomID().equals(toRoom)){
            return rc;
        }
        guest.setFormerRoomID(guest.getRoomID());
        guest.setRoomID(toRoom);
        for(int i = 0; i < roomContentArray.size(); i ++) {
            if (roomContentArray.get(i).getRoomid().equals(toRoom)) {
                roomContentArray.get(i).getIdentities().add(GuestName);
                continue;
            }
            if (roomContentArray.get(i).getIdentities() == null)
                continue;
            for(int j = 0; j < roomContentArray.get(i).getIdentities().size(); j ++) {
                if (roomContentArray.get(i).getIdentities().get(j).equals(GuestName)) {
                    roomContentArray.get(i).getIdentities().remove(j);
                    break;
                }
            }
        }
        return rc;
    }
    //add new guest in room
    public static void AddGuestToRoom(String GuestName, String Roomid) {
        for(int i = 0; i < roomContentArray.size(); i ++) {
            if(roomContentArray.get(i).getRoomid().equals(Roomid))
            roomContentArray.get(i).getIdentities().add(GuestName);
        }
    }
    // change guest name in room array
    public static void ChangeGuestName(String origin, String now) {

        for(int i = 0; i < roomContentArray.size(); i ++) {
            if (roomContentArray.get(i).getOwner().equals(origin)) {
                roomContentArray.get(i).setOwner(now);
            }
            if (roomContentArray.get(i).getIdentities() == null)
                continue;
            for(int j = 0; j < roomContentArray.get(i).getIdentities().size(); j ++) {
                if (roomContentArray.get(i).getIdentities().get(j).equals(origin)) {
                    roomContentArray.get(i).getIdentities().set(j, now);
                    break;
                }
            }
        }
    }
    // check whether the delete name is valid
    public  static boolean checkDeleteName(String Owner, String DeleteRoomID) {
        boolean flag = false;
        for(int i = 0; i < roomContentArray.size(); i ++) {
            if (roomContentArray.get(i).getRoomid().equals(DeleteRoomID)
                    && roomContentArray.get(i).getOwner().equals(Owner)) {
                flag = true;
                break;
            }
        }
        return flag;
    }
    // delete room
    public static void deleteRoomName(String RoomID) {
        for(int i = 0; i < roomContentArray.size(); i ++) {
            if (roomContentArray.get(i).getRoomid().equals(RoomID)) {
                roomContentArray.remove(i);
                break;
            }
        }
    }
    public static void quitGuest(String Name) {
        for(int i = 0; i < roomContentArray.size(); i ++) {
            if (roomContentArray.get(i).getOwner().equals(Name)) {
                roomContentArray.get(i).setOwner("");
            }
            if (roomContentArray.get(i).getIdentities() != null) {
                for(int j = 0; j < roomContentArray.get(i).getIdentities().size(); j ++) {
                    if(roomContentArray.get(i).getIdentities().get(j).equals(Name)){
                        roomContentArray.get(i).getIdentities().remove(j);
                        break;
                    }
                }
            }
        }
    }
    //delete empty room
    public static void RemoveEmptyRoom() {
        for(int i = 0; i < roomContentArray.size(); i ++) {
            if (roomContentArray.get(i).getRoomid().equals("MainHall")){
                continue;
            }
            if (roomContentArray.get(i).getOwner().equals("")) {
                if (roomContentArray.get(i).getIdentities() == null || roomContentArray.get(i).getIdentities().size() == 0){
                    roomContentArray.remove(i);
                    i --;
                }
            }
        }
    }

}

class RoomChange {
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

    RoomChange(String newType, String newIdentity, String newFormer, String newRoomid) {
        this.roomid = newRoomid;
        this.type = newType;
        this.identity = newIdentity;
        this.former = newFormer;
    }

}
