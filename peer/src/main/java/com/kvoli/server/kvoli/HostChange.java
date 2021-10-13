package comp90015a1.peer.src.main.java.com.kvoli.server.kvoli;

import java.util.Vector;

public class HostChange {
    String type;
    String host;

    public HostChange() {

    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    HostChange(String host) {
        type = "hostchange";
        this.host = host;
    }
    public static Vector<HostListServer>HostMapArray = new Vector<HostListServer>();

}
class HostListServer {
    String clientHost;
    String serverHost;
    HostListServer() {

    }
    HostListServer(String client, String server) {
        clientHost = client;
        serverHost = server;
    }
}