package cs455.overlay.wireformats;

import java.util.HashMap;

public class MessagingNodesList {
    //Hash map that houses all the node information
    //Each entry will include SERVER_ADDRESS and PORT
    //Key is the node's SERVER_ADDRESS
    private HashMap NODE_REGISTRY_HASH;

    //CONSTRUCTOR
    public MessagingNodesList(){
        NODE_REGISTRY_HASH = new HashMap();
    }

    String ADD_NODE(String ADDRESS, int PORT){
        String HASHKEY = ADDRESS + PORT;
        if(!NODE_REGISTRY_HASH.containsKey(HASHKEY)){
            NODE_REGISTRY_HASH.put(HASHKEY, new pair(PORT, ADDRESS));
            return "1NODE REGISTERED";
        }
        return "0NODE ALREADY REGISTERED";
    }

    String REM_NODE(String ADDRESS, int PORT){
        String HASHKEY = ADDRESS + PORT;
        if(NODE_REGISTRY_HASH.containsKey(HASHKEY)){
            NODE_REGISTRY_HASH.remove(HASHKEY);
            return "1NODE DEREGISTERED";
        }
        return "0NODE NOT REGISTERED";
    }
}

class pair {
    int PORT;
    String ADDRESS;
    pair(int v, String w) { this.PORT = v; this.ADDRESS = w;}
}
