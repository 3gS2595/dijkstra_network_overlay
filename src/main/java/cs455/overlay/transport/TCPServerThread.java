package cs455.overlay.transport;

//This is the server program.
import cs455.overlay.node.Node;
import cs455.overlay.wireformats.Register_Request;

import java.io.*;
import java.net.*;
import java.lang.*;

public class TCPServerThread implements Runnable{
    private Node    Node;
    private Integer OUR_PORT;
    private ServerSocket serverSocket = null;

    public void run(){
        try {
            //Create the server socket
            Integer NUM_POSSIBLE_CONNECTIONS = 11;
            this.serverSocket = new ServerSocket(OUR_PORT, NUM_POSSIBLE_CONNECTIONS);

            //Register's node with registry (MessengerNode)
            if (Node.isMessenger() == true) {
                new Register_Request(Node);
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
            incomingConnectionSocket.close();
            serverSocket.close();

        } catch (SocketException se) {
            System.out.println(se.getMessage());
            System.exit(1);
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