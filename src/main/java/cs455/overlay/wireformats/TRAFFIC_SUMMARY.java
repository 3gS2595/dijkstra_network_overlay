package cs455.overlay.wireformats;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.node.Registry;
import cs455.overlay.transport.TCPSender;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class TRAFFIC_SUMMARY {
    //receives
    TRAFFIC_SUMMARY(byte[] marshaledBytes, Registry Node) throws IOException{
        System.out.println("ASDASDASDASDASDASDDDDDDDDDDDDDDDDA");
        System.exit(1);
        ByteArrayInputStream baInputStream =
            new ByteArrayInputStream(marshaledBytes);
        DataInputStream din =
            new DataInputStream(new BufferedInputStream(baInputStream));

        //Throws away message type data;
        din.readByte();

        //Stores the Request Response's additional info
        int statusCodeLength = din.readInt();
        byte[] identifierBytes = new byte[statusCodeLength];
        din.readFully(identifierBytes);
        String key = new String(identifierBytes);
        if(!Node.completed.contains(key)) {
            Node.completed.add(key);
            System.out.println("ADDED");
        }
        //Final clean up
        baInputStream.close();
        din.close();
    }

    //sends
    public TRAFFIC_SUMMARY(MessagingNode Node) throws IOException {
        byte[][] messageBytes =  new byte[6][];
        messageBytes[0] = ((Node.getKey()) + " ").getBytes();
        messageBytes[0] = (Integer.toString(Node.sendTracker) + " ").getBytes();
        messageBytes[1] = (Integer.toString(Node.receiveTracker) + " ").getBytes();
        messageBytes[2] = (Float.toString(Node.sendSummation) + " ").getBytes();
        messageBytes[3] = (Float.toString(Node.receiveSummation) + " ").getBytes();
        messageBytes[4] = (Integer.toString(Node.receiveTracker) + " ").getBytes();
        TCPSender.sendMessage(Node.getRegKey(), (byte)12, 6, messageBytes);
    }
}