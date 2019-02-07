package cs455.overlay.wireformats;

import cs455.overlay.node.*;
import cs455.overlay.transport.TCPSender;

import java.io.*;
import java.net.*;

public class EventFactory implements Event{

    //Unmarshalling (DECRYPT)
    public EventFactory(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream =
            new ByteArrayInputStream(marshalledBytes);
        DataInputStream din =
            new DataInputStream(new BufferedInputStream(baInputStream));
        int type = din.read();
        baInputStream.close();
        din.close();

        switch(type) {
            case Protocol.REGISTER_REQ:
                new Register_Request(marshalledBytes);
                break;
            case Protocol.REGISTER_RES:
                new Register_Response(marshalledBytes);
                break;
            default:
                System.out.println("UNKNOWN MESSAGE TYPE RECEIVED");
                break;
        }
    }

    //Marshalling (ENCRYPT)
    public EventFactory(Node Node, Integer protocol){
        try{
            //creates socket to server
            Socket REG_SOCKET = new Socket(Node.getRegAddr(), Node.getRegPort());
            TCPSender sender = new TCPSender(REG_SOCKET);
            byte[] payload;

            //Initialize used streams
            ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
            DataOutputStream dout =
                new DataOutputStream(new BufferedOutputStream(baOutputStream));

            switch(protocol){
                case Protocol.REGISTER_REQ:
                    //insert the register request protocol
                    dout.writeInt(1);
                    //insert the Address
                    byte[] ADDRESS = (new String(Node.getAddr())).getBytes();
                    int elementLength = ADDRESS.length;
                    dout.writeInt(elementLength);
                    dout.write(ADDRESS);
                    //insert port
                    dout.writeInt(Node.getPort());
                    break;
            }

            //records the byte array before final clean up
            dout.flush();
            payload = baOutputStream.toByteArray();

            //final clean up
            baOutputStream.close();
            dout.close();

            //sends request
            sender.sendData(payload);
        } catch (IOException e) {
            System.out.println("Register_request::sending request:: " + e);
            System.exit(1);
        }
    }
}
