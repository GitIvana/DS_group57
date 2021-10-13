package comp90015a1.peer.src.main.java.com.kvoli.server.kvoli;

import java.util.Vector;

public class ListNeighber {
    Vector<String> neighbors;
    String type;
    ListNeighber() {
        neighbors = new Vector<String>();
        type = "listneighbors";
    }

    public void setNeighbors(Vector<String> neighbors) {
        this.neighbors = neighbors;
    }

    public Vector<String> getNeighbors() {
        return neighbors;
    }
}
