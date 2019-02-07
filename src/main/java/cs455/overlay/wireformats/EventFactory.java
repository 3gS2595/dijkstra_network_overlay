package cs455.overlay.wireformats;

import java.io.*;

public class EventFactory {
    private int type;
    private long timestamp;
    private String identifier;
    private int PORT;

    //Unmarshalling (DECRYPT)
    public EventFactory(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream =
            new ByteArrayInputStream(marshalledBytes);
        DataInputStream din =
            new DataInputStream(new BufferedInputStream(baInputStream));

        type = din.readInt();
        timestamp = din.readLong();
        int identifierLength = din.readInt();
        byte[] identifierBytes = new byte[identifierLength];
        din.readFully(identifierBytes);

        switch(type) {
            case Protocol.REGISTER_REQ:
                new Register_Request(identifierBytes);
                break;
            case Protocol.REGISTER_RES:
                new Register_Receive(identifierBytes);
                break;
            case Protocol.DEREGISTER_REQ:
                new Register_Request(identifierBytes);
                break;
            case Protocol.DEREGISTER_RES:
                new Register_Receive(identifierBytes);
                break;
            default:
                System.out.println("UNKNOWN MESSAGE TYPE RECEIVED");
                break;
        }



        identifier = new String(identifierBytes);

        PORT = din.readInt();

        baInputStream.close();
        din.close();
    }

    //Marshalling (ENCRYPT)
    public byte[] getBytes() throws IOException {
        //initializes final return var
        //initializes streams, in & out
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout =
            new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(type);
        dout.writeLong(timestamp);

        //
        byte[] identifierBytes = identifier.getBytes();
        int elementLength = identifierBytes.length;
        dout.writeInt(elementLength);
        dout.write(identifierBytes);

        dout.writeInt(PORT);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();

        baOutputStream.close();
        dout.close();
        return marshalledBytes;
    }


}
