package cs455.overlay.transport;

//This is the client program.
import java.io.*;
import java.net.*;

public class TCPRecieverThread implements Runnable{
    protected Integer          PORT;
    protected String           SERVER_ADDRESS;
    protected Socket           socketToTheServer = null;
    protected DataInputStream  incomingInputStream = null;
    protected DataInputStream  inputStream = null;
    protected DataOutputStream outputStream = null;

    public void run(){
        try {
            //We create the socket AND try to connect to the address and port we are running the server on
            socketToTheServer = new Socket(SERVER_ADDRESS, PORT);
            inputStream = new DataInputStream(socketToTheServer.getInputStream());
            outputStream = new DataOutputStream(socketToTheServer.getOutputStream());
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

            //Now, let's respond.
            byte[] msgToServer = ("CS455").getBytes();
            Integer msgToServerLength = msgToServer.length;
            //Our self-inflicted protocol says we send the length first
            outputStream.writeInt(msgToServerLength);
            //Then we can send the message
            outputStream.write(msgToServer, 0, msgToServerLength);

            //Close streams and then sockets
            inputStream.close();
            outputStream.close();
            socketToTheServer.close();
        } catch(IOException e) {
            System.out.println("Client::main::talking_to_the_server:: " + e);
            System.exit(1);
        }
    }

    //First Argument = SERVER_ADDRESS
    //Second Argument = PORT
    public TCPRecieverThread(String SERV_ADDR, int port) throws IOException {
        //TODO SANATIZE THE INPUT
        SERVER_ADDRESS = SERV_ADDR;
        PORT = port;
    }
}
