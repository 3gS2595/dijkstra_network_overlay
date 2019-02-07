package cs455.overlay.transport;

//This is the server program.
import cs455.overlay.node.Node;
import cs455.overlay.wireformats.Register_Request;
import java.io.*;
import java.net.*;
import java.lang.*;

public class TCPServerThread implements Runnable{
    private Integer       OUR_PORT;
    private Node          Node;
    private Integer       NUM_POSSIBLE_CONNECTIONS = 11;
    private ServerSocket  ourServerSocket = null;

    public void run(){
        try {
            //Create the server socket
            ourServerSocket = new ServerSocket(OUR_PORT, NUM_POSSIBLE_CONNECTIONS);

            //if MessagingNode, this sends the register request
            if(Node.getRegPort() != -1) {
                new Register_Request(Node);
            }

            Socket incomingConnectionSocket = null;
            while(!Thread.currentThread().isInterrupted()) {
                //Block on accepting connections. Once it has received a connection it will return a socket for us to use.
                incomingConnectionSocket = ourServerSocket.accept();
                //If we get here we are no longer blocking, so we accepted a new connection
                //create/initialize server thread
                Thread ReceiverThread = new Thread(new TCPReceiverThread(incomingConnectionSocket, Node));
                ReceiverThread.start();
            }

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