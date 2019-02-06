package cs455.overlay.transport;

//This is the server program.
import java.io.*;
import java.net.*;

public class TCPServerThread implements Runnable{
    protected Integer         OUR_PORT;
    protected Integer         NUM_POSSIBLE_CONNECTIONS = 11;
    protected ServerSocket    ourServerSocket = null;
    protected DataInputStream incomingInputStream = null;

    public void run(){
        try {
            //Create the server socket
            ourServerSocket = new ServerSocket(OUR_PORT, NUM_POSSIBLE_CONNECTIONS);
        } catch(IOException e) {
            System.out.println("Client::main::creating_the_socket:: " + e);
            System.exit(1);
        }
        try {
            //Block on accepting connections. Once it has received a connection it will return a socket for us to use.
            Socket incomingConnectionSocket = ourServerSocket.accept();

            //If we get here we are no longer blocking, so we accepted a new connection
            System.out.println("We received a connection!");

            //We have yet to block again, so we can handle this connection however we would like to.
            //For now, let's send a message and then wait for the response.
            DataInputStream inputStream = new DataInputStream(incomingConnectionSocket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(incomingConnectionSocket.getOutputStream());

            //Let's send a message to our new friend
            byte[] msgToClient = new String("What class is this video for?").getBytes();
            Integer msgToClientLength = msgToClient.length;

            //Our self-inflicted protocol says we send the length first
            outputStream.writeInt(msgToClientLength);
            //Then we can send the message
            outputStream.write(msgToClient, 0, msgToClientLength);

            //Now we wait for their response.
            Integer msgLength = 0;
            //Try to read an integer from our input stream. This will block if there is nothing.
            msgLength = inputStream.readInt();

            //If we got here that means there was an integer to
            // read and we have the length of the rest of the next message.
            System.out.println("Received a message length of: " + msgLength);

            //Try to read the incoming message.
            byte[] incomingMessage = new byte[msgLength];
            incomingInputStream.readFully(incomingMessage, 0, msgLength);

            //You could have used .read(byte[] incomingMessage), however this will read
            // *potentially* incomingMessage.length bytes, maybe less.
            //Whereas .readFully(...) will read exactly msgLength number of bytes.

            System.out.println("Received Message: " + new String(incomingMessage));

            //Close streams and then sockets
            inputStream.close();
            outputStream.close();
            incomingConnectionSocket.close();
            ourServerSocket.close();

        } catch (IOException e) {
            System.out.println("Server::main::accepting_connections:: " + e);
            System.exit(1);
        }
    }

    public TCPServerThread(int port){
        OUR_PORT = port;
    }
}