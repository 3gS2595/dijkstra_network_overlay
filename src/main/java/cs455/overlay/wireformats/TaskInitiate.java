package cs455.overlay.wireformats;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.transport.TCPSender;

import java.io.*;

public class TaskInitiate {

    //SENDS REQUEST
    public TaskInitiate(String key, int roundsCnt) throws IOException {
        byte[][] messageBytes =  new byte[1][];
        String rounds = "";
        rounds = Integer.toString(roundsCnt);
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

        int identifierLength = din.readInt();
        byte[] identifierBytes = new byte[identifierLength];
        din.readFully(identifierBytes);
        String raw = new String(identifierBytes);
        int ROUNDS = Integer.parseInt(raw);
        System.out.println(ROUNDS);

        //complete, cleans up
        baInputStream.close();
        din.close();

        node.taskInitiate(ROUNDS);
    }
}