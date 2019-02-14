package cs455.overlay.wireformats;

import cs455.overlay.node.*;
import java.io.*;

public class EventFactory{

    //Registry's network information
    Integer REGISTRY_PORT;
    String  REGISTRY_HOST;
    Node node;

    //Called from registry to initialize vars
    public void set(String REG_HOST, Integer REG_PORT){
        this.REGISTRY_PORT = REG_PORT;
        this.REGISTRY_HOST = REG_HOST;
    }

    //Initial constructor called by registry
    public EventFactory(){}

    //Unmarshalling (DECRYPT)
    public Event newEvent(byte[] marshaledBytes, Node node) throws IOException {
        this.node = node;

        //Retreives message "type" from marsheledBytes
        ByteArrayInputStream baInputStream =
            new ByteArrayInputStream(marshaledBytes);
        DataInputStream din =
            new DataInputStream(new BufferedInputStream(baInputStream));
        int type = din.read();
        baInputStream.close();
        din.close();

        MessagingNodesList used = new MessagingNodesList();
        //Routes all incoming messages
        switch(type) {
            case Protocol.REGISTER_REQ:
                return new Register_Request(marshaledBytes);
            case Protocol.REGISTER_RES:
                return new Register_Response(marshaledBytes);
            case Protocol.DEREGISTER_REQ:
                return new Deregister_Request(marshaledBytes);
            case Protocol.DEREGISTER_RES:
                return new Deregister_Response(marshaledBytes);
            case Protocol.MESSAGING_NODES_LIST: {
                this.node = node;
                ((MessagingNode) this.node).setNetwork(used.receive(marshaledBytes));
                return null;
            }
            default:
                System.out.println("UNKNOWN MESSAGE TYPE RECEIVED");
        }
        return null;
    }
}
