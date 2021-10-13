package comp90015a1.peer.src.main.java.com.kvoli;

import comp90015a1.peer.src.main.java.com.kvoli.client.kvoli.Client;
import comp90015a1.peer.src.main.java.com.kvoli.server.kvoli.Server;
import comp90015a1.peer.src.main.java.com.kvoli.client.kvoli.Connection;
import java.io.IOException;

public class Peer {
    public static void main(String[] args) throws IOException, InterruptedException {
        int serverport = 6666;
        if(args.length == 1 ) {
            serverport =Integer.parseInt(args[0]);
        }
        Server.createServer(serverport);
        do {
            String Ip = "localhost";
            int port = serverport;
            int localport = 0;
            if (Connection.IsNewConnecttion){

                String[] ipPort =  Connection.Ip.split(":");
                Ip = ipPort[0];
                port = Integer.parseInt(ipPort[1]);
                localport = Integer.parseInt(Connection.port);
            }
            Client client = new Client();
            client.createClient(Ip,port, localport, serverport);

        } while (Connection.IsNewConnecttion);
    }
}
