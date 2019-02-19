package cs455.overlay.wireformats;

import cs455.overlay.node.MessagingNode;
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

public class MessageComs implements Event {

    //SENDS REQUEST
    public MessageComs(Node Node, String key, String[] path) throws IOException {
        byte[][] messageBytes =  new byte[1][];
        messageBytes[0] = Node.getKey().getBytes();
        if(key == null)
            TCPSender.sendMessage(Node.getRegKey(), (byte)1, 1, messageBytes);
        else {
            TCPSender.sendMessage(key, (byte) 1, 1, messageBytes);
        }
    }

    //RECEIVES REQUEST
    MessageComs(byte[] marshaledBytes, Node node) throws IOException {
        ByteArrayInputStream baInputStream =
            new ByteArrayInputStream(marshaledBytes);
        DataInputStream din =
            new DataInputStream(new BufferedInputStream(baInputStream));

        //Throws away type data;
        din.read();

        //Intakes nodes info, ADDRESS in data[0], PORT in data[1]
        int identifierLength = din.readInt();
        byte[] identifierBytes = new byte[identifierLength];
        din.readFully(identifierBytes);
        String raw = new String(identifierBytes);
        String[] data = raw.split(":");

        String ADDRESS = data[0];
        int PORT = Integer.parseInt(data[1]);

        //complete, cleans up
        baInputStream.close();
        din.close();

        //REGISTERS THE NODE
        //TODO CLEAN UP AND MAKE RESPONSE WORK AND REPOT STATUS
        String response = "";
        if(!node.isMessenger()) {
            response = Registry.NODE_LIST.ADD_NODE(ADDRESS, PORT);
            //SENDS REGISTER_RESPONSE
            new Register_Response(ADDRESS, PORT, response);
        }
        else {
            MessagingNode messager = (MessagingNode)node;
            //messager.addConnection(ADDRESS + ":" + PORT);
        }


    }

    public int getType(){ return 2; }
    public byte[] getBytes(){ return null; }
}
