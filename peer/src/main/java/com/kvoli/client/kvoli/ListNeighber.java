package comp90015a1.peer.src.main.java.com.kvoli.client.kvoli;

import java.util.Vector;

public class ListNeighber {
    Vector<String> neighbors;
    String type;
    ListNeighber(String type) {
        neighbors = new Vector<String>();
        this.type = "type";
    }

    public void setNeighbors(Vector<String> neighbors) {
        this.neighbors = neighbors;
    }

    public Vector<String> getNeighbors() {
        return neighbors;
    }
}
