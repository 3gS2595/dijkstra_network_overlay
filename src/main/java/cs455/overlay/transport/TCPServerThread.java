package cs455.overlay.transport;

//This is the server program.
import cs455.overlay.node.Node;
import java.io.*;
import java.net.*;

public class TCPServerThread implements Runnable{
    protected Integer         OUR_PORT;
    protected Node            Node;
    protected Integer         NUM_POSSIBLE_CONNECTIONS = 11;
    protected ServerSocket    ourServerSocket = null;

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
            //create/initialize server thread
            Thread RecieverThread = new Thread(new TCPReceiverThread(incomingConnectionSocket));
            RecieverThread.start();

            //We have yet to block again, so we can handle this connection however we would like to.
            //For now, let's send a message and then wait for the response.

            //Let's send a message to our new friend
            byte[] msgToClient = new String("What class is this video for?").getBytes();
            TCPSender send = new TCPSender(incomingConnectionSocket);
            send.sendData(msgToClient);

            incomingConnectionSocket.close();
            ourServerSocket.close();

        } catch (IOException e) {
            System.out.println("Server::main::accepting_connections:: " + e);
            System.exit(1);
        }
    }

    public TCPServerThread(int port, Node node){
        this.Node = node;
        OUR_PORT = port;
    }
}