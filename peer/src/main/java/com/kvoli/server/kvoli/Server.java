package comp90015a1.peer.src.main.java.com.kvoli.server.kvoli;


import com.google.gson.*;
import comp90015a1.peer.src.main.java.com.kvoli.client.kvoli.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;


public class Server {
  private List<MyChannel> list=new ArrayList<MyChannel>();
  static int serverPort = 8888;
  static class Mythread extends Thread {
    @Override
    public void run() {
      try {
        new Server().start(serverPort);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  public static void createServer(int port) throws IOException {
//    RoomContent.Init();
    // default port
    serverPort = port;
    Mythread mt = new Mythread();
    mt.start();
  }
  public final static ReentrantLock guestInfolock = new ReentrantLock();
  private void start(int serverPort)throws IOException {
    // create socket
    ServerSocket ss=new ServerSocket(serverPort);
    // accept connect
    while (true) {
      Socket socket=ss.accept();
      Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
      //lock
      guestInfolock.lock();
//      System.out.println(socket.getPort());
//      System.out.println(socket.getRemoteSocketAddress());
//      if (socket.getRemoteSocketAddress().toString().split(":")[0].
//              equals(socket.getLocalAddress().toString())) {
//        System.out.println("hahah");
//
//      }
      try {
        // create new channel
        MyChannel myChannel=new MyChannel(socket);
        String newName = socket.getRemoteSocketAddress().toString().substring(1);

        //set name
        myChannel.GuestName = newName;
        Thread thisThread = new Thread(myChannel);
        thisThread.start();
        // set Abrupt Disconnections
        thisThread.setUncaughtExceptionHandler((t, e) -> {
          try {
            var gusetQuit = GuestInfo.GetUserinfo(myChannel.GuestName);
            //Check whether this guest has exited
            if (gusetQuit != null) {
              var GuestInSameRoom1Quit = RoomContent.getRoomIdentity(gusetQuit.getRoomID());
              //System.out.println("roomid: + "+gusetQuit.getRoomID());
              RoomChange RoomContentChangeSendMessage = new RoomChange("roomchange", myChannel.GuestName,
                      gusetQuit.getRoomID(), "");
              GuestInfo.quitGuest(myChannel.GuestName);
              myChannel.sendOthers(gson.toJson(RoomContentChangeSendMessage), GuestInSameRoom1Quit);
              //System.out.println(myChannel.GuestName +" disconnect");
            }
          }catch (IOException e1) {
            e1.printStackTrace();
          }
          e.printStackTrace();
          });
        list.add(myChannel);
        //add new guest
        GuestInfo newGuest = new GuestInfo("", newName);
        GuestInfo.AddUserinfo(newGuest);

        //add new guest in MainHall
//        RoomContent.AddGuestToRoom(newName, "MainHall");


        //send new guest's identity
        NewIdentity message = new NewIdentity(newName, "newidentity");
        String jsonStr = gson.toJson(message);
        myChannel.send(jsonStr);

        //send roomlist
//        RoomInfoList listOut = new RoomInfoList("roomlist");
//        myChannel.send(gson.toJson(listOut));
//
//        //send room change info to everyone in the MainHall
//        Vector<String> fromRoomPeople = RoomContent.getRoomIdentity("MainHall");
//        RoomChange RoomContentChangeSendMessage = new RoomChange("roomchange", myChannel.GuestName,
//                "", "MainHall");
//        myChannel.sendOthers(gson.toJson(RoomContentChangeSendMessage), fromRoomPeople);
//
//        // send mainhall room content
//        RoomContent RoomContentMessage = new RoomContent();
//        RoomContentMessage.setRoomid("MainHall");
//        RoomContent RoomContentSendMessage = RoomContent.getRoomContent(RoomContentMessage);
//        for(int i = 0; i < RoomContentSendMessage.getIdentities().size(); i ++) {
//          //find room owner
//          if (RoomContent.isOwner(RoomContentSendMessage.getIdentities().get(i))) {
//            RoomContentSendMessage.getIdentities().set(i, RoomContentSendMessage.getIdentities().get(i) + "*");
//          }
//        }
//        RoomContentSendMessage.setType("roomcontents");
//        myChannel.send(gson.toJson(RoomContentSendMessage));
      } finally {
        guestInfolock.unlock();
      }
    }
  }
  private class MyChannel implements Runnable{
    private Socket socketM;
    //Input stream of client socket
    private DataInputStream dis;
    //Output stream of client socket
    private DataOutputStream dos;
    // this thread running state
    private boolean isRunning=true;
    // this client guest name
    public String GuestName = "";
    public MyChannel(Socket socket) throws IOException {
      try {
        //Input / output stream of socket
        dis=new DataInputStream(socket.getInputStream());
        dos=new DataOutputStream(socket.getOutputStream());
        socketM = socket;
      } catch (IOException e) {
        isRunning=false;
        list.remove(this);
        dis.close();
        dos.close();
      }
    }
    //receive messages from the client
    private String receive() throws IOException {
      String msg="";
      try {
        msg=dis.readUTF();
        //System.out.println(dis.readUTF());
      } catch (IOException e) {
        isRunning=false;
        list.remove(this);
        dis.close();
        dos.close();
      }
      return msg;
    }
    // send a message to client in this thread
    private void send(String msg) throws IOException {
      if("".equals(msg)){
        return;
      }
      try {
        dos.writeUTF(msg);
        dos.flush();
      } catch (IOException e) {
        isRunning=false;
        list.remove(this);
        dis.close();
        dos.close();
      }
    }
    // send a message to all clients that connect to the server
    private void sendAll(String msg) throws IOException {

      for(MyChannel m:list){
        m.send(msg);
      }
    }
    // send a message to one special client
    private void send2Guest(String msg, String name) throws IOException {

      for (MyChannel m : list) {
        if (m.GuestName.equals(name)) {
          m.send(msg);
        }

      }
    }

    //Send a message to a group of guest
    private void sendOthers(String msg, Vector<String> sendPeople) throws IOException {

      for(MyChannel m:list){
        if(sendPeople.indexOf(m.GuestName) != -1) {
          m.send(msg);
        }
      }
    }


    @Override
    public void run() {
      try {
        while (isRunning) {
          try {
            //receive messages from the client
            String msg = receive();
            //Initialize gson
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            //judge whether socket is broken
            if (msg == "") {
              try {
                var gusetQuit = GuestInfo.GetUserinfo(GuestName);
                //Check whether this guest has exited
                if (gusetQuit != null) {
                  var GuestInSameRoom1Quit = RoomContent.getRoomIdentity(gusetQuit.getRoomID());
                  //System.out.println("roomid: + "+gusetQuit.getRoomID());
                  RoomChange RoomContentChangeSendMessage = new RoomChange("roomchange", GuestName,
                          gusetQuit.getRoomID(), "");
                  GuestInfo.quitGuest(GuestName);
                  sendOthers(gson.toJson(RoomContentChangeSendMessage), GuestInSameRoom1Quit);
//                  System.out.println(GuestName +" disconnect");
                }
              }catch (IOException e1) {
                e1.printStackTrace();
              }
              isRunning = false;
              break;
            }
            JsonElement je = new JsonParser().parse(msg);
            //get type of this message
            String Type = je.getAsJsonObject().get("type").toString();
            //lock


            guestInfolock.lock();
            RoomContent.RemoveEmptyRoom();
            try {
              switch (Type) {
                // change identity
//                case "\"identitychange\"":
//                  NewIdentity message = gson.fromJson(msg, NewIdentity.class);
//                  // get this guest origin info
//                  GuestInfo thisGuest = GuestInfo.GetUserinfo(GuestName);
//                /*GuestInfo.Print();
//                System.out.println("message.getIdentity(): " + message.getIdentity());*/
//
//                  //if new identity is valid
//                  if (GuestInfo.CheckNewIdentity(message.getIdentity())) {
//                    GuestInfo.ChangeName(GuestName, message.getIdentity());
//                    NewIdentity sendMessage = new NewIdentity();
//                    sendMessage.setFormer(thisGuest.getFormer());
//                    sendMessage.setIdentity(thisGuest.getIdentity());
//                    this.GuestName = message.getIdentity();
//                    sendMessage.setType("newidentity");
//                    this.sendAll(gson.toJson(sendMessage));
//
//                  }
//                  //if new identity is not valid
//                  else {
//                    NewIdentity sendMessage = new NewIdentity();
//                    //newidentity : former = Identity
//                    sendMessage.setFormer(thisGuest.getIdentity());
//                    sendMessage.setIdentity(thisGuest.getIdentity());
//                    sendMessage.setType("newidentity");
//                    System.out.println("thisGuest.getFormer()" + thisGuest.getFormer());
//                    System.out.println("thisGuest.getIdentity()" + thisGuest.getIdentity());
//                    this.send(gson.toJson(sendMessage));
//                  }
//                  break;
//                // create new room
                case "\"createroom\"":
                  RoomInfo RoomMessage = gson.fromJson(msg, RoomInfo.class);
                  //if room's name is valid

                  if (RoomContent.CheckNewRoonId(RoomMessage.getRoomid()) &&
                          ((socketM.getRemoteSocketAddress().toString().split(":")[0].
                          equals(socketM.getLocalAddress().toString())))) {
                    RoomContent.AddRoom(RoomMessage.getRoomid(), this.GuestName);
                    RoomInfoList listOut = new RoomInfoList("roomlist");
                    this.send(gson.toJson(listOut));
                  }
                  //if room's name is valid
                  else {
                    RoomInfoList listOut = new RoomInfoList("roomlist");
                    //delete this name from list
                    listOut.deleteRoom(RoomMessage.getRoomid());
                    this.send(gson.toJson(listOut));
                  }
                  break;
                // send room list
                case "\"list\"":
                  RoomInfoList listOut = new RoomInfoList("roomlist");
                  this.send(gson.toJson(listOut));
                  break;
                // send room content
                case "\"who\"":
                  RoomContent RoomContentMessage = gson.fromJson(msg, RoomContent.class);
                  RoomContent RoomContentSendMessage = RoomContent.getRoomContent(RoomContentMessage);
                  RoomContentSendMessage.setType("roomcontents");
                  this.send(gson.toJson(RoomContentSendMessage));
                  break;
                // room change
                case "\"join\"":
                  RoomInfo RoomJoinMessage = gson.fromJson(msg, RoomInfo.class);
                  //check if this room name is valid
                  if (RoomContent.CheckChange(RoomJoinMessage.getRoomid()) || RoomJoinMessage.getRoomid().equals("")) {
                    RoomChange RoomContentChangeSendMessage = RoomContent.SwapRoom(
                            this.GuestName, RoomJoinMessage.getRoomid());
                    // join the origin room
                    if (RoomContentChangeSendMessage.former.equals(RoomContentChangeSendMessage.roomid)) {
                      this.send(gson.toJson(RoomContentChangeSendMessage));
                    }
                    //send roomchange to all
                    //clients currently in the requesting client’s current room and the requesting
                    //client’s requested room.
                    else {
                      if(RoomJoinMessage.getRoomid().equals("")) {
                        this.send(gson.toJson(RoomContentChangeSendMessage));
                      }
                      Vector<String> fromRoomPeople = RoomContent.getRoomIdentity(RoomContentChangeSendMessage.getFormer());
                      this.sendOthers(gson.toJson(RoomContentChangeSendMessage), fromRoomPeople);
                      Vector<String> toRoomPeople = RoomContent.getRoomIdentity(RoomContentChangeSendMessage.getRoomid());
                      this.sendOthers(gson.toJson(RoomContentChangeSendMessage), toRoomPeople);
                    }
                    //If client is changing to the MainHall then the server will also send a
                    //RoomContents message to the client (for the MainHall) and a RoomList
                    //message after the RoomChange message.
//                    if (RoomContentChangeSendMessage.getRoomid().equals("MainHall")) {
//                      RoomContent RoomContentMessage2 = new RoomContent();
//                      RoomContentMessage2.setRoomid("");
//                      RoomContent RoomContentSendMessage2 = RoomContent.getRoomContent(RoomContentMessage2);
//                      for (int i = 0; i < RoomContentSendMessage2.getIdentities().size(); i++) {
//                        if (RoomContent.isOwner(RoomContentSendMessage2.getIdentities().get(i))) {
//                          RoomContentSendMessage2.getIdentities().set(i, RoomContentSendMessage2.getIdentities().get(i) + "*");
//                        }
//                      }
//                      RoomContentSendMessage2.setType("roomcontents");
//                      this.send(gson.toJson(RoomContentSendMessage2));
//
//                      RoomInfoList listOut2 = new RoomInfoList("roomlist");
//                      this.send(gson.toJson(listOut2));
//
//                    }
                  }
                  else {
                    GuestInfo guest = GuestInfo.GetUserinfo(GuestName);
                    RoomChange RoomContentChangeSendMessage = new RoomChange("roomchange", GuestName,
                            guest.getRoomID(), guest.getRoomID());
                    this.send(gson.toJson(RoomContentChangeSendMessage));
                  }
                  break;
                case "\"delete\"":
                  RoomInfo RoomDeleteMessage = gson.fromJson(msg, RoomInfo.class);
                  //check if this room name is valid
                  if (RoomContent.checkDeleteName(this.GuestName, RoomDeleteMessage.getRoomid())) {
                    // find all guests in this delete room
                    var roomGuest = RoomContent.getRoomIdentity(RoomDeleteMessage.getRoomid());
                    if (roomGuest != null) {
                      // send MainHall room content to every guest
//                      for (int i = 0; i < roomGuest.size(); i++) {
//                        String thisName = roomGuest.get(i);
//                        RoomContent RoomContentMessage2 = new RoomContent();
//                        RoomContentMessage2.setRoomid("");
//                        RoomContent RoomContentSendMessage2 = RoomContent.getRoomContent(RoomContentMessage2);
//                        RoomContentSendMessage2.setType("roomcontents");
//
//                        this.send2Guest(gson.toJson(RoomContentSendMessage2), thisName);
//                      }
                      // every guest join the MainHall
                      for (int i = 0; i < roomGuest.size(); i++) {
                        RoomChange RoomContentChangeSendMessage = RoomContent.SwapRoom(
                                roomGuest.get(i), "");
                        Vector<String> fromRoomPeople = RoomContent.getRoomIdentity(RoomContentChangeSendMessage.getFormer());
                        this.sendOthers(gson.toJson(RoomContentChangeSendMessage), fromRoomPeople);
                        Vector<String> toRoomPeople = RoomContent.getRoomIdentity(RoomContentChangeSendMessage.getRoomid());
                        this.sendOthers(gson.toJson(RoomContentChangeSendMessage), toRoomPeople);

                        i--;
                      }
                    }

                    // send roomlist to room owner
                    RoomContent.deleteRoomName(RoomDeleteMessage.getRoomid());
                    RoomInfoList listOutDelete = new RoomInfoList("roomlist");
                    this.send(gson.toJson(listOutDelete));

                  } else {
                    RoomInfoList listOutDelete = new RoomInfoList("roomlist");
                    // set delete room name to make client know that this room is not delete
                    listOutDelete.addRoom(RoomDeleteMessage.getRoomid());
                    this.send(gson.toJson(listOutDelete));
                  }
                  break;
                // send message
                case "\"message\"":
                  MessageGuest messageGuest = gson.fromJson(msg, MessageGuest.class);
                  var guset = GuestInfo.GetUserinfo(this.GuestName);
                  // get clients in the same room
                  var GuestInSameRoom = RoomContent.getRoomIdentity(guset.getRoomID());

                  messageGuest.setIdentity(this.GuestName);
                  this.sendOthers(gson.toJson(messageGuest), GuestInSameRoom);
                  break;
                case "\"quit\"":
                  var gusetQuit = GuestInfo.GetUserinfo(this.GuestName);
                  var GuestInSameRoom1Quit = RoomContent.getRoomIdentity(gusetQuit.getRoomID());
                  RoomChange RoomContentChangeSendMessage = new RoomChange("roomchange", GuestName,
                          gusetQuit.getRoomID(), "");
                  GuestInfo.quitGuest(this.GuestName);
                  //System.out.println(GuestName + " disconnect");
                  this.GuestName = null;
                  isRunning = false;
                  this.sendOthers(gson.toJson(RoomContentChangeSendMessage), GuestInSameRoom1Quit);
                  break;
                case "\"listneighbors\"":
                  String thisClient = socketM.getRemoteSocketAddress().toString().substring(1);
                  String thisServer = socketM.getLocalAddress().toString().substring(1);
                  var neighberList = new ListNeighber();
                  Vector<String> nowList = new Vector<String>();
                  for (MyChannel m : list) {
                    if(!(m.GuestName.equals(thisClient)) && !(m.GuestName.split(":")[0]).equals(thisServer)) {
                      nowList.add(m.GuestName);
                    }
                  }
                  nowList.add(Client.ServerName);
                  neighberList.setNeighbors(nowList);
                  this.send(gson.toJson(neighberList));
                  break;
                case "\"hostchange\"":
                  HostChange messageHostchange = gson.fromJson(msg, HostChange.class);


              }
            } finally {
              guestInfolock.unlock();
            }
            //sendOthers(msg);

          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }finally {
        try {
          dis.close();
          dos.close();
          if(socketM != null) {
            socketM.close();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      //dos.close();

    }
  }
}