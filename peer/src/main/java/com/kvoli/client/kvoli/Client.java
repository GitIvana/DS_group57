package comp90015a1.peer.src.main.java.com.kvoli.client.kvoli;

import com.google.gson.*;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

public class Client {
  // this client's name
  public static String GuestName;
  public static String ServerName;
  // this client's location
  public static String GuestRoom;
  // Check wether the client is waiting for a reply
  public static int Outstructure = 1;
  public static int Serverport;
  static CountDownLatch countDownLatch;
  // output structure
  public static void init() {
    Outstructure = 1;
    GuestRoom = "";
    ServerName = "";
    countDownLatch = new CountDownLatch(2);
    Connection.IsNewConnecttion = false;
  }
  public static void printClient() {
    //System.out.println(GuestName);
    System.out.format("[%s]%s> ", GuestRoom, GuestName);
  }
  public void createClient(String host, int port, int localport, int serverport) throws IOException {

    //create two thread for every client
    // one is to send message and command
    // one is to receive message from server
    Client.init();
    Serverport = serverport;
    Socket socket=new Socket(host,port, null, localport);
    GuestName = socket.getLocalAddress().toString().substring(1)+":"+ socket.getLocalPort();
    ServerName = socket.getRemoteSocketAddress().toString().substring(1);
    Thread SendThread = new Thread(new Send(socket));
    SendThread.start();
    Thread RecieveThread = new Thread(new Recieve(socket));
    RecieveThread.start();
    try {
      countDownLatch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

  }

}

class crtAndDel{
  private crtAndDel(){
  }
  // client is creating a room
  public static Create crt;
  // client is deleting a room
  public static Delete del;
}

class Send implements Runnable {

  private Socket socket;

  private DataOutputStream writer;
  private InputStreamReader inputStreamReader;
  private BufferedReader bR;
  //client send thread state
  private boolean isRunning;

  public Send() throws IOException {
  }
  public void quit() {
    isRunning =false;
  }
  public Send(Socket socketNow) throws IOException {
    socket = socketNow;
    writer=new DataOutputStream(socket.getOutputStream());
    inputStreamReader = new InputStreamReader(System.in);
    bR = new BufferedReader(inputStreamReader);
    isRunning=true;
  }
  public DataOutputStream getWriter(){
    return writer;
  }

  public void sendMsg(String msg) throws IOException {
    if(null!=msg&&!msg.equals("")){
      try {
        writer.writeUTF(msg);
      } catch (IOException e) {
        isRunning=false;
        writer.close();
        //bR.close();
      }
    }
  }
  private String getMsgFromConsole(){
    try {
//      System.out.println(inputStreamReader);
      return bR.readLine();
    } catch (IOException e) {
      System.out.println(e);
      return "";
    }
  }
  public void send() throws IOException {
    if(Connection.IsNewConnecttion == true) {
      String serverhost = Client.GuestName;
      HostChange JI = new HostChange(serverhost.split(":")[0] + Client.Serverport);
      String json0 = new Gson().toJson(JI);
      sendMsg(json0);
      //etWriter().flush();
      Client.Outstructure = 0;
      Connection.IsNewConnecttion = false;
    }
    //Accept input by line
    // System.out.println("wait to input");
    String inputString = getMsgFromConsole();
    //System.out.println(inputString);

    if(null!=inputString&&!inputString.equals("")){
      //Check whether it is a command or a message
      switch (inputString.charAt(0)){
        case '#':
          inputString = inputString.substring(1);
          String parts[] = inputString.split(" ");
          // the length of #list and #quit is 1, and the length of other command is 2
          if (parts[0].equals("list") || parts[0].equals("quit") || parts[0].equals("listneighbors")) {
            if ((parts.length != 1)) {
              System.out.println("input is Invalid");
              Client.Outstructure = 0;
              //Client.printClient();
              break;
            }
          }
          else if (parts[0].equals("connection")) {
            if ((parts.length != 2 && parts.length != 3)) {
              System.out.println("input is Invalid");
              Client.Outstructure = 0;
              //Client.printClient();
              break;
            }
          }
          else if (parts[0].equals("join")) {
            if (parts.length > 2) {
              System.out.println("input is Invalid");
              Client.Outstructure = 0;
              //Client.printClient();
              break;
            }
          }
          else if (parts.length!=2){
            System.out.println("input is Invalid");
            //Client.printClient();
            Client.Outstructure = 0;
            break;
          }
          switch (parts[0]){
//            case "identitychange":
//              IdentityChange IC = new IdentityChange(parts[0],parts[1]);
//              String json1 = new Gson().toJson(IC);
//              sendMsg(json1);
//              Client.Outstructure = 1;
//              //getWriter().flush();
//              break;
            case "join":
              String tmpp = "";
              if (parts.length == 2) {
                tmpp = parts[1];
              }
              Join JI = new Join(parts[0],tmpp);
              String json2 = new Gson().toJson(JI);
              sendMsg(json2);
              //etWriter().flush();
              Client.Outstructure = 1;
              break;
            case "who":
              Who wo = new Who(parts[0],parts[1]);
              String json3 = new Gson().toJson(wo);
              sendMsg(json3);
              //getWriter().flush();
              Client.Outstructure = 1;
              break;
            case "list":
              LIST lt = new LIST(parts[0]);
              String json4 = new Gson().toJson(lt);
              sendMsg(json4);
              //getWriter().flush();
              Client.Outstructure = 1;
              break;
            case "createroom":
              Create crtv1 = new Create(parts[0],parts[1]);
              String jsonv1 = new Gson().toJson(crtv1);
              crtAndDel.crt = crtv1;
              Client.Outstructure = 1;
              sendMsg(jsonv1);
              break;
            case "delete":
              Delete del = new Delete(parts[0],parts[1]);
              crtAndDel.del = del;
              Client.Outstructure = 1;
              String json6 = new Gson().toJson(del);
              sendMsg(json6);
              break;
            case "quit":
              Quit qt = new Quit(parts[0]);
              String json7 = new Gson().toJson(qt);
              sendMsg(json7);
              isRunning=false;
              writer.close();
             // bR.close();
              Client.Outstructure = 1;
              break;
            case "connection":
              Quit qtCon = new Quit("quit");
              String json8 = new Gson().toJson(qtCon);
              sendMsg(json8);
              isRunning=false;
              writer.close();
              //bR.close();
              Client.Outstructure = 1;
              if (parts.length == 2) {
                Connection.Ip = parts[1];
                Connection.IsNewConnecttion = true;
                Connection.port = "0";
              }
              else if (parts.length == 3){
                Connection.Ip = parts[1];
                Connection.IsNewConnecttion = true;
                Connection.port = parts[2];
              }
              break;
            case "listneighbors":
              ListNeighber lneighber = new ListNeighber(parts[0]);
              String json9 = new Gson().toJson(lneighber);
              sendMsg(json9);
              //getWriter().flush();
              Client.Outstructure = 1;
              break;
            default:
              System.out.println("Command is Invalid");
              Client.Outstructure = 0;
              Client.printClient();
              break;
          }
          break;
        default:
          if(Client.GuestRoom.equals("")) {
            System.out.println("not in a room");
            Client.printClient();
            break;
          } else {
            Message msg = new Message("message",inputString);
            String json = new Gson().toJson(msg);
            //System.out.println("lalal1");
            sendMsg(json);
            //System.out.println("lalal2");
            Client.Outstructure = 1;
          }
      }
    } else {
      System.out.println("Input is null");
    }
  }
  @Override
  public void run() {
    try{
      while (isRunning){
        try {
          send();
        } catch (IOException e) {
          isRunning = false;
          if(socket != null) {
            try {
              socket.close();
            } catch (IOException ioException) {
              ioException.printStackTrace();
            }
          }
          e.printStackTrace();
        }
      }
    } finally {
      Client.countDownLatch.countDown();
      /*try {
        bR.close();
      } catch (IOException e) {
        e.printStackTrace();
      }*/
    }

  }
}

class Recieve implements Runnable {
  //private BufferedReader reader;
  private Socket socket;
  private DataInputStream dis;
  //Initial continuous output of 4 pieces of information
  private int initNum = 0;
  //Initial output complete state
  private int initOutNum = 0;
  private boolean isRunning=true;
  private Gson gson = new Gson();
  public Recieve() throws IOException {
  }
  public Recieve(Socket socketNow) throws IOException {
    try {
      dis=new DataInputStream(socketNow.getInputStream());
      socket = socketNow;
    } catch (IOException e) {
      isRunning=false;
      dis.close();
      //bw.close();
    }

    //reader= new BufferedReader(new InputStreamReader(socket.getInputStream()));
  }

  /*public BufferedReader getReader(){
    return reader;
  }*/
  public void recieve() throws IOException {
    String jsstring="";
    try {
      jsstring=dis.readUTF();
      //System.out.println(jsstring);
      JsonElement je = new JsonParser().parse(jsstring);
      String type = je.getAsJsonObject().get("type").toString();
      // Format output
//      if(Client.Outstructure == 0 && initOutNum == 1) {
//        System.out.println();
//      }
      if(Client.Outstructure == 0) {
        System.out.println();
      }
      switch (type){
        case "\"newidentity\"":
          NewIdentity msg0 = gson.fromJson(jsstring,NewIdentity.class);
          // check whether the user is a new guest
          if(msg0.getFormer().equals("")) {
            System.out.format("Connected to localhost as %s \n",msg0.getIdentity());

            //Client.GuestName = msg0.getIdentity();
//            Client.GuestRoom = "MainHall";
          }
          else if (msg0.getFormer().equals(msg0.getIdentity())) {
            // check whether the name have been changed successfully
            System.out.println("Requested identity invalid or in use");
          }else{
            //check whether this guest name is changing
            if(msg0.getFormer().equals(Client.GuestName)) {
              //Client.GuestName = msg0.getIdentity();
            }
            System.out.format("%s is now %s \n",msg0.getFormer(),msg0.getIdentity());
          }
          break;
        case "\"roomchange\"":
          RoomChange msg2 = gson.fromJson(jsstring,RoomChange.class);
          if(msg2.getFormer().equals(msg2.getRoomid())){
            System.out.println("The requested room is invalid or non existent.");
          } else {
            //check whether this guest room is changing
            if(msg2.getidentity().equals(Client.GuestName)) {
              Client.GuestRoom = msg2.getRoomid();
            }
            // check whether the user is a new guest
            if (msg2.getFormer().equals("")) {
              System.out.format("%s moves to %s \n", msg2.getidentity(), msg2.getRoomid());
            } else {
              System.out.format("%s moved from %s to %s \n",msg2.getidentity(),msg2.getFormer(),msg2.getRoomid());
            }


          }
          break;
        case "\"roomcontents\"":
          RoomContent msg3 = gson.fromJson(jsstring,RoomContent.class);
          // check whether this room is created
          if (msg3.roomid == null) {
            System.out.println("this room is not created");
            break;
          }
          // check whether this room is empty
          if(msg3.getIdentities() == null || msg3.getIdentities().size() == 0) {
            System.out.println(msg3.roomid + " is empty");
            break;
          }
          System.out.format("%s contains ", msg3.roomid);
          for (String i: msg3.getIdentities()){
            /*if(msg3.owner.equals(i)) {
              System.out.print(i+"* ");
            }else*/
            System.out.print(i+" ");
          }
          System.out.println();
          break;
        case "\"roomlist\"":
          RoomList msg4 = gson.fromJson(jsstring,RoomList.class);

          if(crtAndDel.crt != null && !crtAndDel.crt.getRoomID().equals("")) {
            // client is creating a room
            int flag = 0;
            for (RoomInfo i: msg4.getrooms()){
              if(i.getRoomid().equals(crtAndDel.crt.getRoomID())) {
                flag = 1;
                break;
              }
            }
            if(flag == 0) {
              System.out.format("Room %s is invalid or already in use. \n", crtAndDel.crt.getRoomID());
            } else {
              System.out.format("Room %s created. \n", crtAndDel.crt.getRoomID());
            }
            crtAndDel.crt.setRoomID("");
          } else if(crtAndDel.del != null && !crtAndDel.del.getRoomID().equals("")) {
            // client is deleting a room
            int flag = 0;
            for (RoomInfo i: msg4.getrooms()){
              if(i.getRoomid().equals(crtAndDel.del.getRoomID())) {
                flag = 1;
                break;
              }
            }
            if(flag == 1) {
              System.out.format("Room %s is invalid or not create. \n", crtAndDel.del.getRoomID());
            } else {
              System.out.format("Room %s delete. \n", crtAndDel.del.getRoomID());
            }
            crtAndDel.del.setRoomID("");
          } else {
            //print roomlist
            for (RoomInfo i: msg4.getrooms()){
              System.out.format("%s : %s guests\n",i.getRoomid(),i.getCount());
            }
          }
          break;
        case "\"quit\"":
          this.isRunning = false;
          break;
        case "\"message\"":
          ServerMsg message = gson.fromJson(jsstring,ServerMsg.class);
          System.out.format("%s: %s \n",message.getidentity(),message.getcontent());
          break;
      }
      Client.Outstructure = 0;
      Client.printClient();
//      if(initNum == 0) {
//        Client.printClient();
//        initOutNum = 1;
//      } else
//        initNum --;
    } catch (IOException e) {
      isRunning=false;
      dis.close();
    }
  }
  @Override
  public void run() {
    try {
      while (isRunning) {
        try {
          recieve();
        } catch (IOException e) {
          try {
            if(socket != null)
            socket.close();
          } catch (IOException ioException) {
            ioException.printStackTrace();
          }
          e.printStackTrace();
        }
      }
    }finally {
      try {
        socket.close();
        Client.countDownLatch.countDown();
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    }

  }
}