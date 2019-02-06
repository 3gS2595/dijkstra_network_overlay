package cs455.overlay.transport;

//This is the client program.
import java.io.*;
import java.net.*;

public class TCPReceiverThread implements Runnable{
    protected Socket           socketToTheServer = null;
    protected DataInputStream  incomingInputStream = null;
    protected DataInputStream  inputStream = null;

    public void run(){
        try {
            //We create the socket AND try to connect to the address and port we are running the server on
            inputStream = new DataInputStream(socketToTheServer.getInputStream());
        } catch(IOException e) {
            System.out.println("Client::main::creating_the_socket:: " + e);
            System.exit(1);
        }
        // We assume that if we get here we have connected to the server.
        System.out.println("Connected to the server.");

        try {
            Integer msgLength = 0;
            //Try to read an integer from our input stream. This will block if there is nothing.
            msgLength = inputStream.readInt();

            //If we got here that means there was an integer to read and we have the
            // length of the rest of the next message.
            System.out.println("Received a message length of: " + msgLength);

            //Try to read the incoming message.
            byte[] incomingMessage = new byte[msgLength];
            incomingInputStream.readFully(incomingMessage, 0, msgLength);

            //You could have used .read(byte[] incomingMessage), however this will read
            // *potentially* incomingMessage.length bytes, maybe less.
            // Whereas .readFully(...) will read exactly msgLength number of bytes.

            System.out.println("Received Message: " + new String(incomingMessage));

            //Close streams and then sockets
            inputStream.close();
            socketToTheServer.close();
        } catch(IOException e) {
            System.out.println("Client::main::talking_to_the_server:: " + e);
            System.exit(1);
        }
    }

    //First Argument = SERVER_ADDRESS
    //Second Argument = PORT
    public TCPReceiverThread(Socket socket) throws IOException {
        this.socketToTheServer = socket;
    }
}
