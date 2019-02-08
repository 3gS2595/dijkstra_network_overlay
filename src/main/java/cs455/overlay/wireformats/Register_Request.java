package cs455.overlay.wireformats;

import cs455.overlay.node.*;
import cs455.overlay.transport.TCPSender;
import java.io.*;
import java.net.*;

public class Register_Request implements Event {
    protected String NODE_ADDRESS;
    protected Integer NODE_PORT;
    protected boolean debug;

    //RECEIVES REQUEST
    public Register_Request(byte[] marshalledBytes, Node Node) throws IOException {
        debug = Node.getDebug();

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

        //complete, cleans up
        baInputStream.close();
        din.close();

        if(debug) {
            System.out.println("REGISTER_REQUEST (RECEIVED)");
            System.out.print("  (IP address: " + NODE_ADDRESS + ")");
            System.out.print("(Port number: " + NODE_PORT + ")");
            System.out.println();
        }

        //SENDS REGISTER_RESPONSE
        Registry reg = (Registry)Node;
        String add = reg.NODE_LIST.ADD_NODE(NODE_ADDRESS, NODE_PORT);

        byte status = Byte.parseByte(add.substring(0,1));
        String info = add.substring(1);
        new Register_Response(Node, status, info);
    }

    //SENDS REQUEST
    public Register_Request(Node Node) throws IOException {
        debug = Node.getDebug();

        //creates socket to server
        Socket REG_SOCKET = new Socket(Node.getRegAddr(), Node.getRegPort());
        TCPSender sender = new TCPSender(REG_SOCKET);

        //creates Request message byte array
        byte[] marshalledBytes;

        //Initialize used streams
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout =
            new DataOutputStream(new BufferedOutputStream(baOutputStream));

        //insert the register request protocol
        dout.writeByte(1);

        //insert the Address then the port of the node
        byte[] ADDRESS = (new String(Node.getAddr())).getBytes();
        int elementLength = ADDRESS.length;
        dout.writeInt(elementLength);
        dout.write(ADDRESS);
        dout.writeInt(Node.getPort());

        //records payload and cleans up
        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();

        //sends request
        sender.sendData(marshalledBytes);

        if(debug) {
            System.out.println("REGISTER_REQUEST (SENT)");
            System.out.print("  (IP address: " + Node.getAddr() + ")");
            System.out.print("(Port number: " + Node.getPort() + ")");
            System.out.println();
        }
    }
}
