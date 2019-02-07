package cs455.overlay.transport;

//This is the client program.
import cs455.overlay.wireformats.*;

import java.io.*;
import java.net.*;

public class TCPReceiverThread implements Runnable{
    private Socket           socketToTheServer;
    private DataInputStream  din;

    public void run() {
        int dataLength;
        while (socketToTheServer != null) {
            try {
                //receive and record message
                dataLength = din.readInt();
                byte[] data = new byte[dataLength];
                din.readFully(data, 0, dataLength);

                //send bytes for Unmarshalling and handling
                new EventFactory(data);
            } catch (SocketException se) {
                System.out.println(se.getMessage());
                break;
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage()) ;
                break;
            }
        }

    }

    public TCPReceiverThread(Socket socket) throws IOException {
        this.socketToTheServer = socket;
        din = new DataInputStream(socket.getInputStream());
    }


}
