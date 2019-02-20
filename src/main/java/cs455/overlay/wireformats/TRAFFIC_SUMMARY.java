package cs455.overlay.wireformats;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.node.Registry;
import cs455.overlay.transport.TCPSender;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Random;

public class TRAFFIC_SUMMARY {
    //receives
    TRAFFIC_SUMMARY(byte[] marshaledBytes, Registry Node) throws IOException{

        byte[] marshelled = marshaledBytes;
        ByteArrayInputStream baInputStream =
            new ByteArrayInputStream(marshelled);
        DataInputStream din =
            new DataInputStream(new BufferedInputStream(baInputStream));

        //Throws away message type data;
        din.readByte();
        int count = din.readInt();

        String full = " ";
        for(int i= 0 ; i < count; i ++) {
            //Stores the Request Response's additional info
            int statusCodeLength = din.readInt();
            byte[] identifierBytes = new byte[statusCodeLength];
            din.readFully(identifierBytes);
            String key = new String(identifierBytes);
            full = full + key;
        }
        if(!Node.completed.contains(full)) {
            Node.completed.add(full);
        }
        //Final clean up
        baInputStream.close();
        din.close();
    }

    //sends
    public TRAFFIC_SUMMARY(MessagingNode Node) throws IOException  {
        try { Thread.sleep(1000); }catch (InterruptedException e){ }

        byte[][] messageBytes =  new byte[6][];
        messageBytes[0] = ((Node.getKey()) + " ").getBytes();
        messageBytes[1] = (Integer.toString(Node.sendTracker) + " ").getBytes();
        messageBytes[2] = (Integer.toString(Node.receiveTracker) + " ").getBytes();
        messageBytes[3] = (Float.toString(Node.sendSummation) + " ").getBytes();
        messageBytes[4] = (Float.toString(Node.receiveSummation) + " ").getBytes();
        messageBytes[5] = (Integer.toString(Node.relayTracker) + " ").getBytes();
        TCPSender.sendMessage(Node.getRegKey(), (byte)12, 6, messageBytes);
    }
}