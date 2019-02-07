package cs455.overlay.wireformats;

import cs455.overlay.node.Node;
import cs455.overlay.transport.TCPSender;

import java.io.*;
import java.net.Socket;

public class Register_Response {

    private String ADDITIONALINFO;
    private byte STATUS;
    private boolean debug = true;

    public Register_Response(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream =
            new ByteArrayInputStream(marshalledBytes);
        DataInputStream din =
            new DataInputStream(new BufferedInputStream(baInputStream));

        //Throws away type data;
        din.readByte();

        STATUS = din.readByte();

        //Stores NODE's ADDRESS
        int identifierLength = din.readInt();
        byte[] identifierBytes = new byte[identifierLength];
        din.readFully(identifierBytes);
        ADDITIONALINFO = new String(identifierBytes);
        //Stores NODE's PORT


        if(debug) {
            System.out.println("RECEIVED");
            System.out.println("Message Type (int)       : REGISTER_RESPONSE");
            System.out.println("Status Code (byte)       : " + STATUS);
            System.out.println("Additional Info (String) : " + ADDITIONALINFO);
            System.out.println();
        }
        baInputStream.close();
        din.close();

    }

    public Register_Response(String NODE_ADDRESS, int NODE_PORT, byte STATUS, String INFO){
        try{
            //creates socket to server
            Socket REG_SOCKET = new Socket(NODE_ADDRESS, NODE_PORT);
            TCPSender sender = new TCPSender(REG_SOCKET);

            //creates Request message byte array
            byte[] marshalledBytes;

            //Initialize used streams
            ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
            DataOutputStream dout =
                new DataOutputStream(new BufferedOutputStream(baOutputStream));

            //insert the register request protocol
            dout.writeByte(2);
            dout.writeByte(STATUS);

            //insert the Address then the port of the node
            byte[] ADDITIONALINFO = (INFO).getBytes();
            int elementLength = ADDITIONALINFO.length;
            dout.writeInt(elementLength);
            dout.write(ADDITIONALINFO);

            //records the byte array before final clean up
            dout.flush();
            marshalledBytes = baOutputStream.toByteArray();

            //final clean up
            baOutputStream.close();
            dout.close();

            //sends request
            sender.sendData(marshalledBytes);

            if(debug) {
                System.out.println("SENT");
                System.out.println("Message Type (int)       : REGISTER_RESPONSE");
                System.out.println("Status Code (byte)       : " + STATUS);
                System.out.println("Additional Info (String) : " + INFO);
                System.out.println();
            }

        } catch (IOException e) {
            System.out.println("Register_request::sending request:: " + e);
            System.exit(1);
        }
    }

}
