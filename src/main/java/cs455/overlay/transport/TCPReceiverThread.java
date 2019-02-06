package cs455.overlay.transport;

//This is the client program.
import java.io.*;
import java.net.*;

public class TCPReceiverThread implements Runnable{
    protected Socket           socketToTheServer = null;
    protected DataInputStream  din = null;

    public void run() {
        int dataLength;
        while (socketToTheServer != null) {
            try {
                dataLength = din.readInt();
                byte[] data = new byte[dataLength];
                din.readFully(data, 0, dataLength);
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
