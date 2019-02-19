package cs455.overlay.util;

import java.util.ArrayList;
import java.util.HashMap;

public class DijkstrasPath {
    public DijkstrasPath() {}

    public String DijkstrasPath(ArrayList<String> connectionWeights, String sourceKey, String destKey) {
        HashMap<String, Integer> dist = new HashMap<>();
        HashMap<String, String> path = new HashMap<>();

        dist.put(sourceKey, 0);

        ArrayList<String> queue = new ArrayList<>();
        queue.add(sourceKey);

        ArrayList<String> queue2;


        //sets all non connected nodes to 1000000 (infinte?)
        for (String temp : connectionWeights) {
            String[] tempA = temp.split(" ");
            if (!tempA[0].equals(sourceKey) && !dist.containsKey(tempA[0])) {
                dist.put(tempA[0], 1000000);
                queue.add(tempA[0]);
            }
            else if (!tempA[1].equals(sourceKey) && !dist.containsKey(tempA[0])) {
                dist.put(tempA[1], 1000000);
                queue.add(tempA[1]);
            }
        }
        queue2 = new ArrayList<>(queue);

        while (queue.size() > 0) {
            //begins
            int min = 0;
            for (int i = 0; i < queue.size(); i++) {
                if (dist.get(queue.get(i)) < dist.get(queue.get(min))) {
                    min = i;
                }
            }
            //for each neighbor of minimum weight
            String v = queue.remove(min);

            for (String temp : connectionWeights) {
                //System.out.println(temp);
                String[] u = temp.split(" ");
                //if neighbor
                if (u[0].equals(v)) {
                    int alt = dist.get(v) + Integer.parseInt(u[2]);
                    if (alt < dist.get(u[1])) {
                        dist.replace(u[1], alt);
                        path.put(u[1], v);
                    }
                }
                if (u[1].equals(v)) {
                    int alt = dist.get(v) + Integer.parseInt(u[2]);
                    if (alt < dist.get(u[0])) {
                        dist.replace(u[0], alt);
                        path.put(u[0], v);
                    }
                }
            }
        }
        String pathString = getPath(path, destKey, sourceKey);
        String[] parsed = pathString.split(" ");
        String reversed = " ";
        int i = parsed.length -1;
        for (String temp : parsed){
            reversed += parsed[i] + " ";
            i--;
        }
        return reversed;
    }

    private String getPath( HashMap<String, String> record, String key, String Source){
        if (key == Source)
            return (key);
        return (key  + " " + getPath(record, record.get(key), Source));
    }
}
