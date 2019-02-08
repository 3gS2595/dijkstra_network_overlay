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

    public void run(){
        try {
            //Create the server socket
            Integer NUM_POSSIBLE_CONNECTIONS = 11;
            ServerSocket ourServerSocket = new ServerSocket(OUR_PORT, NUM_POSSIBLE_CONNECTIONS);

            while(ourServerSocket != null) {
                //Register's node with registry (MessengerNode)
                if (Node.getRegPort() != -1) {
                    new Register_Request(Node);
                }

                //Loop should continue indefinitely
                Socket incomingConnectionSocket = null;
                while (!Thread.currentThread().isInterrupted()) {
                    //Block on accepting connections. Once it has received a connection it will return a socket for us to use.
                    incomingConnectionSocket = ourServerSocket.accept();

                    //If we get here we are no longer blocking, so we accepted a new connection
                    //create/initialize server thread
                    Thread ReceiverThread = new Thread(new TCPReceiverThread(incomingConnectionSocket));
                    ReceiverThread.start();
                }

                //clean up
                incomingConnectionSocket.close();
            }
            ourServerSocket.close();

        } catch (SocketException se) {
            System.out.println(se.getMessage());
            System.exit(1);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage()) ;
            System.exit(1);
        }
    }

    //CONSTRUCTOR
    public TCPServerThread(int port, Node node){
        this.Node = node;
        this.OUR_PORT = port;
    }
}