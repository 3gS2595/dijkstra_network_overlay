package cs455.overlay.wireformats;

import cs455.overlay.node.*;
import cs455.overlay.transport.TCPSender;

import java.io.*;
import java.net.*;

public class Register_Request implements Event {
    protected String NODE_ADDRESS;
    protected Integer NODE_PORT;
    protected boolean debug = true;

    public Register_Request(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream =
            new ByteArrayInputStream(marshalledBytes);
        DataInputStream din =
            new DataInputStream(new BufferedInputStream(baInputStream));

        //Throws away type data;
        din.readByte();

        //Stores NODE's ADDRESS
        int identifierLength = din.readInt();
        byte[] identifierBytes = new byte[identifierLength];
        din.readFully(identifierBytes);
        NODE_ADDRESS = new String(identifierBytes);
        //Stores NODE's PORT
        NODE_PORT = din.readInt();

        if(debug) {
            System.out.println("RECEIVED");
            System.out.println("Message Type (int)  : REGISTER_REQUEST");
            System.out.println("IP address (String) : " + NODE_ADDRESS);
            System.out.println("Port number (int)   : " + NODE_PORT);
            System.out.println();
        }

        baInputStream.close();
        din.close();
    }

    public Register_Request(Node Node){
        try{
            //creates socket to server
            Socket REG_SOCKET = new Socket(Node.getRegAddr(), Node.getRegPort());
            TCPSender sender = new TCPSender(REG_SOCKET);

            //creates Request message byte array
            byte[] marshalledBytes;
            byte[] ADDRESS = (new String(Node.getAddr())).getBytes();

            //Initialize used streams
            ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
            DataOutputStream dout =
                new DataOutputStream(new BufferedOutputStream(baOutputStream));

            //insert the register request protocol
            dout.writeByte(1);

            //insert the Address then the port of the node
            int elementLength = ADDRESS.length;
            dout.writeInt(elementLength);
            dout.write(ADDRESS);
            dout.writeInt(Node.getPort());

            //records the byte array before final clean up
            dout.flush();
            marshalledBytes = baOutputStream.toByteArray();

            //final clean up
            baOutputStream.close();
            dout.close();

            //sends request
            sender.sendData(marshalledBytes);
        } catch (IOException e) {
            System.out.println("Register_request::sending request:: " + e);
            System.exit(1);
        }
    }
}
