package cs455.overlay.wireformats;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.node.Registry;
import cs455.overlay.transport.TCPSender;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class TaskComplete {
     //receives
     TaskComplete(byte[] marshaledBytes, Registry Node) throws IOException{
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
    public TaskComplete(MessagingNode Node) throws IOException {
        String key = Node.getKey();
        byte[][] messageBytes =  new byte[1][];
        messageBytes[0] = key.getBytes();
        TCPSender.sendMessage(Node.getRegKey(), (byte)10, 1, messageBytes);
    }


}
