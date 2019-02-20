package cs455.overlay.wireformats;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.transport.TCPSender;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.util.Random;

public class MessageComs implements Event {

    //RECEIVES REQUEST
    MessageComs(byte[] marshaledBytes, MessagingNode Node) throws IOException {
        ByteArrayInputStream baInputStream =
            new ByteArrayInputStream(marshaledBytes);
        DataInputStream din =
            new DataInputStream(new BufferedInputStream(baInputStream));

        byte type = din.readByte();
        //Throws away type data;
        int Payload = din.readInt();

        int length = din.readInt();
        if(length > 0) {
            byte[] path = new byte[length];
            din.read(path);
            String raw = new String(path);
            String newPath = "";
            String[] data = raw.split(" ");
            for (int i = 1; i < data.length;i++) {
                newPath += data[i] + " ";
            }
            if(data.length != 1) {
                Random rn = new Random(System.currentTimeMillis());
                int rando = rn.nextInt();
                byte[] payload = toByteArray(rando);
                byte[][] messageBytes = new byte[2][];
                messageBytes[0] = payload;
                messageBytes[1] = newPath.getBytes();
                TCPSender.sendMessage(data[1], (byte) 9, -5, messageBytes);
                Node.relayTracker++;
            }else {
                System.out.println("DO YOU NOW DE WEY");
                Node.receiveTracker++;
                Node.receiveSummation += Payload;
            }
        }

        //complete, cleans up
        baInputStream.close();
        din.close();
    }

    byte[] toByteArray(int value) {
        return new byte[] {
            (byte)(value >> 24),
            (byte)(value >> 16),
            (byte)(value >> 8),
            (byte)value };
    }

    public int getType(){ return 2; }
    public byte[] getBytes(){ return null; }
}
