package cs455.overlay.wireformats;

import cs455.overlay.node.*;
import cs455.overlay.transport.TCPSender;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;

public class Register_Response implements Event {

    //TODO DISABLE DEBUG TOGGLE
    private boolean debug = true;
    private byte[] marshaledBytes;

    //RECEIVES RESPONSE
    public Register_Response(byte[] marshaledBytes) throws IOException {
        this.marshaledBytes = marshaledBytes;
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
        String ADDITIONAL_INFO = new String(identifierBytes);
        int Satus = Character.getNumericValue(ADDITIONAL_INFO.charAt(0));
        String Info = ADDITIONAL_INFO.substring(1);

        //Final clean up
        baInputStream.close();
        din.close();
    }

    //SENDS RESPONSE
    public Register_Response(String NODE_ADDRESS, int NODE_PORT, String INFO)  throws IOException {
        String key = NODE_ADDRESS + ":" + NODE_PORT;
        byte[][] messageBytes =  new byte[1][];
        messageBytes[0] = INFO.getBytes();
        TCPSender.sendMessage(key, (byte)2, 1, messageBytes);
    }

    public int getType(){ return 2; }
    public byte[] getBytes(){ return this.marshaledBytes; }
}
