package comp90015a1.peer.src.main.java.com.kvoli.server.kvoli;

import java.util.Vector;

public class GuestInfo{
    String former;
    String identity;
    String Address;
    String RoomID;
    String FormerRoomID;
    String HostName;
    //init
    GuestInfo(){
        this.RoomID = "";
        this.FormerRoomID = "";
        this.former = "";

    }
    GuestInfo(String NewAddress, String NewIdentity) {
        Address = NewAddress;
        identity = NewIdentity;
        this.RoomID = "";
        this.FormerRoomID = "";
        this.former = "";
    }
    public String getAddress(){
        return Address;
    }

    public void setFormerRoomID(String formerRoomID) {
        FormerRoomID = formerRoomID;
    }

    public String getFormerRoomID() {
        return FormerRoomID;
    }

    public String getIdentity(){
        return identity;
    }

    public String getFormer(){
        return former;
    }

    //public void setType(String newType) { type = newType; }

    public void setFormer(String newFormer) { former = newFormer; }

    public void setIdentity(String newIdentity) { identity = newIdentity; }

    public String getRoomID() {
        return RoomID;
    }

    public void setRoomID(String roomID) {
        RoomID = roomID;
    }

    public void printOne(){
        System.out.println("former: " + former);
        System.out.println("identity: " + identity);
    }
    public static int usernum = 0;
    // set new guest name
    public static String getUserName() {
        String name = "guest";
        if(userinfoArray == null) {
            name = name + "0";
        } else {
            int cnt = 0;
            // find the smallest number not use now
            while(true) {
                String tmpname = name + Integer.toString(cnt);
                cnt ++;
                boolean flag = false;
                for(int i = 0; i < userinfoArray.size(); i ++) {
                    if(userinfoArray.get(i).getIdentity().equals(tmpname)) {
                        flag = true;
                        break;
                    }
                }
                if(flag == false) {
                    name = tmpname;
                    break;
                }
            }
        }
        return name;
    }
    //guest info online array
    public static Vector<GuestInfo> userinfoArray = new Vector<GuestInfo>();
    public static void ChangeName(String origin, String now) {
        var guest = GetUserinfo(origin);
        RoomContent.ChangeGuestName(origin, now);
        guest.setFormer(origin);
        guest.setIdentity(now);
    }
    //from guest Indentity get GuestInfo
    public static GuestInfo GetUserinfo(String identity) {
        GuestInfo tmpGuest = new GuestInfo();
        for(int i = 0; i < userinfoArray.size(); i ++) {
            if (userinfoArray.get(i).getIdentity().equals(identity)) {
                tmpGuest =  userinfoArray.get(i);
            }
        }
        return tmpGuest;
    }
    //guest quit
    public static void quitGuest(String Name) {
        for(int i = 0; i < userinfoArray.size(); i ++) {
            if (userinfoArray.get(i).getIdentity().equals(Name)) {
                RoomContent.quitGuest(Name);
                userinfoArray.remove(i);
                break;
            }
        }
    }
    public static void AddUserinfo(GuestInfo New) {
        userinfoArray.add(New);
    }
    //test method
    public static void Print() {
        for(int i = 0; i < userinfoArray.size(); i ++) {
            userinfoArray.get(i).printOne();
        }
    }
    //check whether new Identity is valid
    public static boolean CheckNewIdentity(String newIdentity) {
        boolean flag = true;
        for(int i = 0; i < userinfoArray.size(); i ++) {
            if (userinfoArray.get(i).getIdentity().equals(newIdentity)) {
                flag = false;
                break;
            }
        }

        boolean is_matcher = newIdentity != null && newIdentity.matches("^[a-zA-Z][a-zA-Z1-9]{1,14}[a-zA-Z1-9]\\Z");

        return (flag & is_matcher);
    }



}