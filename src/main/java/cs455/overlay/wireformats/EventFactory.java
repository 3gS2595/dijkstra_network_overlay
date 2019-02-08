package cs455.overlay.wireformats;

import java.io.*;

public class EventFactory{

    //Registry's network information
    Integer REGISTRY_PORT;
    String  REGISTRY_HOST;

    //Called from registry to initialize vars
    public void set(String REG_HOST, Integer REG_PORT){
        this.REGISTRY_PORT = REG_PORT;
        this.REGISTRY_HOST = REG_HOST;
    }

    //Initial constructor called by registry
    public EventFactory(){}

    //Unmarshalling (DECRYPT)
    public void newEvent(byte[] marshaledBytes) throws IOException {

        //Retreives message "type" from marsheledBytes
        ByteArrayInputStream baInputStream =
            new ByteArrayInputStream(marshaledBytes);
        DataInputStream din =
            new DataInputStream(new BufferedInputStream(baInputStream));
        int type = din.read();
        baInputStream.close();
        din.close();

        //Routes all incoming messages
        switch(type) {
            case Protocol.REGISTER_REQ:
                new Register_Request(marshaledBytes);
                break;
            case Protocol.REGISTER_RES:
                new Register_Response(marshaledBytes);
                break;
            case Protocol.DEREGISTER_REQ:
                new Deregister_Request(marshaledBytes);
                break;
            case Protocol.DEREGISTER_RES:
                new Deregister_Response(marshaledBytes);
                break;
            default:
                System.out.println("UNKNOWN MESSAGE TYPE RECEIVED");
                break;
        }
    }
}
