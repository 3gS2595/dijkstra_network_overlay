package cs455.overlay.wireformats;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.node.Node;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class EventFactory{

    //Registry's network information
    private Node node;

    //Initial constructor called by registry
    public EventFactory(){}

    //Unmarshalling (DECRYPT)
    public void newEvent(byte[] marshaledBytes, Node receivingNode) throws IOException {
        this.node = receivingNode;

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
            case Protocol.MESSAGING_NODES_LIST: {
                 new MessagingNodesList(marshaledBytes, (MessagingNode)this.node);
                 break;
            }
            case Protocol.MESSAGING_NODES_WEIGHTS: {
                System.out.println("WEIGHTS");
                break;
            }
            default:
                System.out.println("UNKNOWN MESSAGE TYPE RECEIVED");
                break;
        }
    }
}
