package cs455.overlay.wireformats;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.node.Node;
import cs455.overlay.node.Registry;

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
                 new Register_Request(marshaledBytes, this.node);
                 break;
            case Protocol.REGISTER_RES:
                 new Register_Response(marshaledBytes);
                 break;
            case Protocol.DEREGISTER_REQ:
                 new Deregister_Request(marshaledBytes);
                 break;
            case Protocol.DEREGISTER_RES:
                 new Deregister_Response(marshaledBytes,(MessagingNode)this.node);
                 break;
            case Protocol.MESSAGING_NODES_LIST:
                 new MessagingNodesList(marshaledBytes, (MessagingNode)this.node);
                 break;
            case Protocol.MESSAGING_NODES_WEIGHTS:
                new MessagingNodesList(marshaledBytes, (MessagingNode)this.node);
                break;
            case Protocol.TASK_INITIATE:
                new TaskInitiate(marshaledBytes, (MessagingNode)this.node);
                break;
            case Protocol.DATA_MESSAGE:
                new MessageComs(marshaledBytes, (MessagingNode)this.node);
                break;
            case Protocol.TASK_COMPLETE:
                new TaskComplete(marshaledBytes, (Registry)this.node);
                break;
            case Protocol.PULL_TRAFFIC_SUMMARY:
                new PULL_TRAFFIC_SUMMARY(marshaledBytes, (MessagingNode) this.node);
                break;
            case Protocol.TRAFFIC_SUMMARY:
                new TRAFFIC_SUMMARY(marshaledBytes, (Registry)this.node);
                break;
            default:
                System.out.println("UNKNOWN MESSAGE TYPE RECEIVED");
                break;
        }
    }
}
