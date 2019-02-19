package cs455.overlay.dijkstra;

import java.util.HashMap;

public class ShortestPath {
    private String ShortestPath(HashMap<String, String> record, String key, String Source){
        if (key == Source)
            return (key);
        return (key  + " " + ShortestPath(record, record.get(key), Source));
    }
}
