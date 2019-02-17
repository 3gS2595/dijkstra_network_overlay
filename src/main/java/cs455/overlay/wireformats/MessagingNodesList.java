package cs455.overlay.wireformats;

import cs455.overlay.node.MessagingNode;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MessagingNodesList implements Event{

    //TODO REMOVE DEBUG
    boolean debug = true;

    public MessagingNodesList.Pair[] OVERLAY_CONNECTION_NODES;
    public ArrayList<String> OVERLAY_CONNECTION_WEIGHTS = new ArrayList<>();

    //Hash map that houses all the node information
    //Each entry will include SERVER_ADDRESS and PORT
    //Key is the node's SERVER_ADDRESS
    public HashMap<String, Pair> NODE_REGISTRY_ARRAY = new HashMap<>();
    private byte[] marshaledBytes;

    //CONSTRUCTOR
    public MessagingNodesList(){}

    public int size(){
        return NODE_REGISTRY_ARRAY.size();
    }

    String ADD_NODE(String ADDRESS, int PORT){
        String key = ADDRESS + ":" + PORT;
        if(!NODE_REGISTRY_ARRAY.containsKey(key)){
            NODE_REGISTRY_ARRAY.put(key, new Pair(PORT, ADDRESS));
            return "1NODE REGISTERED";
        }
        return "0NODE ALREADY REGISTERED";
    }

    String REM_NODE(String ADDRESS, int PORT){
        String key = ADDRESS + ":" + PORT;
        if(this.NODE_REGISTRY_ARRAY.containsKey(key)){
            this.NODE_REGISTRY_ARRAY.remove(key);
            return "1NODE DEREGISTERED";
        }
        return "0NODE NOT REGISTERED";
    }

    public String print(){
        String ret = "";
        for (String cur : NODE_REGISTRY_ARRAY.keySet()){
            ret += cur + "\n";
        }
        ret = (ret.substring(0, (ret.length()-1)));
        return ret;
    }

    public class Pair{
        private Integer PORT; //first member of pair
        private String ADDRESS; //second member of pair

        private Pair(Integer PORT, String ADDRESS) {
            this.PORT = PORT;
            this.ADDRESS = ADDRESS;
        }

        //GETTERS
        public Integer getPORT() { return PORT; }
        public String getADDRESS() { return ADDRESS; }

        //TOSTRING
        public String toKey() {
            return this.getADDRESS() + ":" + this.getPORT();
        }
    }

    //RECEIVES OVERLAY REQUEST
    //Assigns given list to the node, array stored in messagingNode node
    public MessagingNodesList(byte[] marshaledBytes, MessagingNode node) throws IOException {
        this.marshaledBytes = marshaledBytes;

        //Incoming network info
        String NODE_ADDRESS;
        int NODE_PORT;

        ByteArrayInputStream baInputStream =
            new ByteArrayInputStream(marshaledBytes);
        DataInputStream din =
            new DataInputStream(new BufferedInputStream(baInputStream));

        //Throws away type data;
        int type = din.readByte();

        int arrayLength = din.readInt();
        OVERLAY_CONNECTION_NODES = new MessagingNodesList.Pair[arrayLength];

        if (type == 5) {
            //creates sized array for overlay nodes
            for (int i = 0; i < arrayLength; i++) {
                //stores string
                int strLength = din.readInt();
                byte[] identifierBytes = new byte[strLength];
                din.readFully(identifierBytes);
                String raw = new String(identifierBytes);
                String[] nodeData = raw.split(":");
                NODE_ADDRESS = nodeData[0];
                NODE_PORT = Integer.parseInt(nodeData[1]);

                //inserts node into array
                OVERLAY_CONNECTION_NODES[i] = new Pair(NODE_PORT, NODE_ADDRESS);
            }
        } else if (type == 6) {
            //creates sized array for overlay nodes

            for (int i = 0; i < arrayLength; i++) {
                //stores string
                int strLength = din.readInt();
                byte[] identifierBytes = new byte[strLength];
                din.readFully(identifierBytes);
                String raw = new String(identifierBytes);
                String[] nodeData = raw.split(" ");
                String[] node1 = nodeData[0].split(":");
                String[] node2 = nodeData[1].split(":");
                NODE_ADDRESS = node1[0];
                NODE_PORT = Integer.parseInt(node1[1]);
                String NODE_ADDRESS2 = node2[0];
                int NODE_PORT2 = Integer.parseInt(node2[1]);
                int weight = Integer.parseInt(nodeData[2]);

                //add connection weight
                OVERLAY_CONNECTION_WEIGHTS.add(raw);
            }
        }

        //complete, cleans up
        baInputStream.close();
        din.close();

        if(type == 5) {
            node.setNetwork(OVERLAY_CONNECTION_NODES);
        }
        if(type == 6) {
            node.setNetworkWeights(OVERLAY_CONNECTION_WEIGHTS);
        }
        //SENDS DEREGISTER_RESPONSE
        //new Deregister_Response(NODE_ADDRESS, NODE_PORT, status, info);
    }

    public String listWeights(){

        return null;
    }


    public int getType(){
        return 5;
    }
    public byte[] getBytes(){
        return this.marshaledBytes;
    }

}


