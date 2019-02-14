package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

public class MessagingNodesList implements Event{

    //TODO REMOVE DEBUG
    boolean debug = true;

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
        String key = ADDRESS + PORT;
        if(!NODE_REGISTRY_ARRAY.containsKey(key)){
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
        private String toStr() {
            return this.getADDRESS() + " " + this.getPORT();
        }
    }

    //RECEIVES REQUEST
    //Assigns given list to the node, array stored in messagingNode node
    public MessagingNodesList.Pair[] receive(byte[] marshaledBytes) throws IOException {
        MessagingNodesList.Pair[] OVERLAY_CONNECTION_NODES;
        this.marshaledBytes = marshaledBytes;

        //Incoming network info
        String NODE_ADDRESS;
        int NODE_PORT;

        ByteArrayInputStream baInputStream =
            new ByteArrayInputStream(marshaledBytes);
        DataInputStream din =
            new DataInputStream(new BufferedInputStream(baInputStream));

        //Throws away type data;
        din.read();

        //creates sized array for overlay nodes
        int arrayLength = din.readInt();
        OVERLAY_CONNECTION_NODES = new MessagingNodesList.Pair[arrayLength];

        for(int i = 0; i < arrayLength; i++){
            //stores string
            int strLength = din.readInt();
            byte[] identifierBytes = new byte[strLength];
            din.readFully(identifierBytes);
            NODE_ADDRESS = new String(identifierBytes);

            //Stores NODE's PORT
            NODE_PORT = din.readInt();

            //inserts node into array
            OVERLAY_CONNECTION_NODES[i] = new Pair(NODE_PORT, NODE_ADDRESS);


        }

        //complete, cleans up
        baInputStream.close();
        din.close();

        //DEBUG
        if (debug) {
            String temp = "";
            for (int i = 0; i < arrayLength; i++){
                temp = temp.concat(OVERLAY_CONNECTION_NODES[i].toStr() + "\n");
            }
            System.out.println("OVERLAY NODE LIST RECEIVED\n" + temp);
        }

        //SENDS DEREGISTER_RESPONSE
        //new Deregister_Response(NODE_ADDRESS, NODE_PORT, status, info);
        return OVERLAY_CONNECTION_NODES;
    }
    public int getType(){
        return 5;
    }
    public byte[] getBytes(){
        return this.marshaledBytes;
    }

}


