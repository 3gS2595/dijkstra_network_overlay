package cs455.overlay.wireformats;

import cs455.overlay.transport.TCPSender;
import cs455.overlay.node.*;
import java.io.*;
import java.net.Socket;

public class Register_Response {
    private boolean debug;

    //RECEIVES RESPONSE
    public Register_Response(byte[] marshaledBytes, Node Node) throws IOException {
        debug = Node.getDebug();

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
            System.out.println("REGISTER_RESPONSE(RECEIVED)");
            System.out.print("  (Status Code: " + STATUS + ")");
            System.out.print("(Additional Info: " + ADDITIONAL_INFO + ")");
            System.out.println();
        }
    }

    //SENDS RESPONSE
    public Register_Response(Node Node, byte STATUS, String INFO)  throws IOException {
        debug = Node.getDebug();

        //creates Request message byte array
        byte[] marshalledBytes;

        //creates socket to server
        Socket REG_SOCKET = new Socket(Node.getAddr(), Node.getPort());
        TCPSender sender = new TCPSender(REG_SOCKET);

        //Initialize used streams
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout =
            new DataOutputStream(new BufferedOutputStream(baOutputStream));

        //insert the register request protocol
        dout.writeByte(2);
        dout.writeByte(STATUS);

        //insert the Address then the port of the node
        byte[] ADDITIONAL_INFO = (INFO).getBytes();
        int elementLength = ADDITIONAL_INFO.length;
        dout.writeInt(elementLength);
        dout.write(ADDITIONAL_INFO);

        //records payload and cleans up
        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();

        //sends request
        sender.sendData(marshalledBytes);

        if(debug) {
            System.out.println("REGISTER_RESPONSE (SENT)");
            System.out.print("  (Status Code: " + STATUS + ")");
            System.out.print("(Additional Info: " + INFO + ")");
            System.out.println();
        }
    }
}
