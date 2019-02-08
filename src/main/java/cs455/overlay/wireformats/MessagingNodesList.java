package cs455.overlay.wireformats;

import java.util.HashMap;

public class MessagingNodesList {
    //Hash map that houses all the node information
    //Each entry will include SERVER_ADDRESS and PORT
    //Key is the node's SERVER_ADDRESS
    private HashMap<String, Pair> NODE_REGISTRY_HASH = new HashMap<String, Pair>();

    //CONSTRUCTOR
    public MessagingNodesList(){}

    String ADD_NODE(String ADDRESS, int PORT){
        String HASHKEY = ADDRESS + PORT;
        if(!NODE_REGISTRY_HASH.containsKey(HASHKEY)){
            NODE_REGISTRY_HASH.put(HASHKEY, new Pair(PORT, ADDRESS));
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

    class Pair{
        private Integer PORT; //first member of pair
        private String ADDRESS; //second member of pair

        public Pair(Integer PORT, String ADDRESS) {
            this.PORT = PORT;
            this.ADDRESS = ADDRESS;
        }

        //GETTERS
        public Integer getPORT() { return PORT; }
        public String getADDRESS() { return ADDRESS; }

        //COMPARE
        public boolean Compare(Pair x, Pair y){
            if(x.getADDRESS() == y.getADDRESS()
                && x.getPORT() == y.getPORT())
                return true;
            return false;
        }
    }
}


