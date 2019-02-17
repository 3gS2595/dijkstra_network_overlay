package cs455.overlay.transport;

import cs455.overlay.node.Node;
import cs455.overlay.wireformats.Register_Request;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerThread implements Runnable{
    private Integer OUR_PORT;
    private Node    Node;

    public void run(){
        try {
            //Create the server socket
            int NUM_POSSIBLE_CONNECTIONS = 11;
            ServerSocket serverSocket = new ServerSocket(OUR_PORT, NUM_POSSIBLE_CONNECTIONS);

            //Register's node with registry (MessengerNode)
            if (Node.isMessenger()) {
                new Register_Request(this.Node, null);
            }

            //Loop should continue indefinitely
            Socket incomingConnectionSocket = null;
            while (!Thread.currentThread().isInterrupted()) {
                //Block on accepting connections. Once it has received a connection it will return a socket for us to use.
                incomingConnectionSocket = serverSocket.accept();

                //If we get here we are no longer blocking, so we accepted a new connection
                //create/initialize server thread
                Thread ReceiverThread = new Thread(new TCPReceiverThread(incomingConnectionSocket, this.Node));
                ReceiverThread.start();
            }

            //clean up
            serverSocket.close();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            System.exit(1);
        }
    }


    //CONSTRUCTOR
    public TCPServerThread(int port, Node node){
        this.Node = node;
        this.OUR_PORT = port;
    }
}