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
    private String  NODE_ADDRESS;
    private Integer NODE_PORT;
    private byte[]  marshaledBytes;
    private boolean debug = true;

    //RECEIVES REQUEST
    public Deregister_Request(byte[] marshaledBytes) throws IOException {
        this.marshaledBytes = marshaledBytes;

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
        NODE_ADDRESS = new String(identifierBytes);

        //Stores NODE's PORT
        NODE_PORT = din.readInt();

        //complete, cleans up
        baInputStream.close();
        din.close();

        if(debug) {
            System.out.println("DEREGISTER_REQUEST (RECEIVED)");
            System.out.print("  (IP address: " + NODE_ADDRESS + ")");
            System.out.print("(Port number: " + NODE_PORT + ")");
            System.out.println();
        }

        //SENDS DEREGISTER_RESPONSE
        String add = Registry.NODE_LIST.REM_NODE(NODE_ADDRESS, NODE_PORT);

        byte status = Byte.parseByte(add.substring(0,1));
        String info = add.substring(1);
        new Deregister_Response(NODE_ADDRESS, NODE_PORT, status, info);
    }

    //SENDS REQUEST
    public Deregister_Request(Node Node) throws IOException {

        //creates socket to server
        Socket REG_SOCKET = new Socket(Node.getRegAddr(), Node.getRegPort());
        TCPSender sender = new TCPSender(REG_SOCKET);

        //creates Request message byte array
        byte[] marshaledBytes;

        //Initialize used streams
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout =
            new DataOutputStream(new BufferedOutputStream(baOutputStream));

        //insert the deregister request protocol
        dout.writeByte(1);

        //insert the Address then the port of the node
        byte[] ADDRESS = (new String(Node.getAddr())).getBytes();
        int elementLength = ADDRESS.length;
        dout.writeInt(elementLength);
        dout.write(ADDRESS);
        dout.writeInt(Node.getPort());

        //records payload and cleans up
        dout.flush();
        marshaledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();

        //sends request
        sender.sendData(marshaledBytes);

        if(debug) {
            System.out.println("DEREGISTER_REQUEST (SENT)");
            System.out.print("  (IP address: " + Node.getAddr() + ")");
            System.out.print("(Port number: " + Node.getPort() + ")");
            System.out.println();
        }
    }

    public int getType(){
        return 3;
    }
    public byte[] getBytes(){
        return marshaledBytes;
    }
}
