package cs455.overlay.wireformats;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.node.Node;
import cs455.overlay.node.Registry;
import cs455.overlay.transport.TCPSender;

import java.io.*;
import java.util.ArrayList;

public class TaskInitiate {

    public TaskInitiate(){
    }

    //SENDS REQUEST
    public TaskInitiate(String key, int roundsCnt) throws IOException {
        byte[][] messageBytes =  new byte[1][];
        String rounds = null;
        rounds +=roundsCnt;
        messageBytes[0] = rounds.getBytes();
        TCPSender.sendMessage(key, (byte) 8, 1, messageBytes);
    }

    //RECEIVES REQUEST
    public TaskInitiate(byte[] marshaledBytes, MessagingNode node) throws IOException {
        ByteArrayInputStream baInputStream =
            new ByteArrayInputStream(marshaledBytes);
        DataInputStream din =
            new DataInputStream(new BufferedInputStream(baInputStream));

        //Throws away type data;
        din.read();

        //Intakes nodes info, ADDRESS in data[0], PORT in data[1]
        int ROUNDS = din.readInt();

        //complete, cleans up
        baInputStream.close();
        din.close();

        node.taskInitiate(ROUNDS);
    }
}