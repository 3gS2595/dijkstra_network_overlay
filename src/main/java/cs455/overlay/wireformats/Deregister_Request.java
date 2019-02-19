package cs455.overlay.wireformats;

import cs455.overlay.node.Registry;
import cs455.overlay.node.Node;
import cs455.overlay.transport.TCPSender;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;
import java.net.Socket;

public class Deregister_Request implements Event {

    //TODO DISABLE DEBUG TOGGLE
    private boolean debug = true;
    private byte[]  marshaledBytes;

    //SENDS REQUEST
    public Deregister_Request(Node Node) throws IOException {
        byte[][] messageBytes =  new byte[1][];
        messageBytes[0] = Node.getKey().getBytes();
        TCPSender.sendMessage(Node.getRegKey(), (byte)3, 1, messageBytes);
    }

    //RECEIVES REQUEST
    Deregister_Request(byte[] marshaledBytes) throws IOException {
        this.marshaledBytes = marshaledBytes;
        //Incoming network info
        String NODE_ADDRESS;
        int NODE_PORT = 2;

        ByteArrayInputStream baInputStream =
            new ByteArrayInputStream(marshaledBytes);
        DataInputStream din =
            new DataInputStream(new BufferedInputStream(baInputStream));

        //Throws away type data;
        din.readByte();

        //Stores NODE's ADDRESS
        int identifierLength = din.readInt();
        byte[] identifierBytes = new byte[identifierLength];
        din.readFully(identifierBytes);
        String raw = new String(identifierBytes);
        String[] data = raw.split(":");
        NODE_ADDRESS = data[0];
        NODE_PORT = Integer.parseInt(data[1]);

        //complete, cleans up
        baInputStream.close();
        din.close();

        //SENDS DEREGISTER_RESPONSE
        String add = Registry.NODE_LIST.REM_NODE(NODE_ADDRESS, NODE_PORT);

        byte status = Byte.parseByte(add.substring(0,1));
        String info = add.substring(1);
        new Deregister_Response(NODE_ADDRESS, NODE_PORT, status, info);
    }



    public int getType(){
        return 3;
    }

    public byte[] getBytes(){
        return marshaledBytes;
    }
}
