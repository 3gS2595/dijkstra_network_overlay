package cs455.overlay.wireformats;

import java.util.ArrayList;
import java.util.HashMap;

public class MessagingNodesList {
    //Hash map that houses all the node information
    //Each entry will include SERVER_ADDRESS and PORT
    //Key is the node's SERVER_ADDRESS
    private HashMap<String, Pair> NODE_REGISTRY_ARRAY = new HashMap();

    //CONSTRUCTOR
    public MessagingNodesList(){}

    String ADD_NODE(String ADDRESS, int PORT){
        String key = ADDRESS + PORT;
        if(!NODE_REGISTRY_ARRAY.containsValue(key)){
            NODE_REGISTRY_ARRAY.put(key, new Pair(PORT, ADDRESS));
            return "1NODE REGISTERED";
        }
        return "0NODE ALREADY REGISTERED";
    }

    String REM_NODE(String ADDRESS, int PORT){
        String key = ADDRESS + PORT;
        if(this.NODE_REGISTRY_ARRAY.containsKey(key)){
            this.NODE_REGISTRY_ARRAY.remove(key);
            return "1NODE DEREGISTERED";
        }
        return "0NODE NOT REGISTERED";
    }

    public String print(){
        return NODE_REGISTRY_ARRAY.toString();
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


