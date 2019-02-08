package cs455.overlay.wireformats;

import cs455.overlay.transport.TCPSender;
import cs455.overlay.node.*;
import java.io.*;
import java.net.Socket;

public class Deregister_Response {
    boolean debug = true;

    //RECEIVES RESPONSE
    public Deregister_Response(byte[] marshaledBytes) throws IOException {
        ByteArrayInputStream baInputStream =
            new ByteArrayInputStream(marshaledBytes);
        DataInputStream din =
            new DataInputStream(new BufferedInputStream(baInputStream));

        //Throws away message type data;
        din.readByte();

        //Stores the Request Response's status code
        byte STATUS = din.readByte();

        //Stores the Request Response's additional info
        int statusCodeLength = din.readInt();
        byte[] identifierBytes = new byte[statusCodeLength];
        din.readFully(identifierBytes);
        String ADDITIONAL_INFO = new String(identifierBytes);

        //Final clean up
        baInputStream.close();
        din.close();

        if(debug) {
            System.out.println("DEREGISTER_RESPONSE(RECEIVED)");
            System.out.print("  (Status Code: " + STATUS + ")");
            System.out.print("(Additional Info: " + ADDITIONAL_INFO + ")");
            System.out.println();
        }
    }

    //SENDS RESPONSE
    public Deregister_Response(String NODE_ADDRESS, int NODE_PORT, byte STATUS, String INFO)  throws IOException {

        //creates Request message byte array
        byte[] marshaledBytes;

        //creates socket to server
        Socket REG_SOCKET = new Socket(NODE_ADDRESS, NODE_PORT);

        //Initialize used streams
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout =
            new DataOutputStream(new BufferedOutputStream(baOutputStream));

        //insert the deregister request protocol
        dout.writeByte(2);
        dout.writeByte(STATUS);

        //insert the Address then the port of the node
        byte[] ADDITIONAL_INFO = (INFO).getBytes();
        int elementLength = ADDITIONAL_INFO.length;
        dout.writeInt(elementLength);
        dout.write(ADDITIONAL_INFO);

        //records payload and cleans up
        dout.flush();
        marshaledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();

        //sends request
        TCPSender sender = new TCPSender(REG_SOCKET);
        sender.sendData(marshaledBytes);

        if(debug) {
            System.out.println("DEREGISTER_RESPONSE (SENT)");
            System.out.print("  (Status Code: " + STATUS + ")");
            System.out.print("(Additional Info: " + INFO + ")");
            System.out.println();
        }
    }
}
