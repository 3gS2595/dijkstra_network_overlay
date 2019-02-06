package cs455.overlay.node;

import cs455.overlay.transport.*;
import cs455.overlay.wireformats.*;
import java.io.*;
import java.net.*;

public class MessagingNode implements Node{
    //Registry's network information
    private String  REGISTRY_HOST;
    private Integer REGISTRY_PORT;
    //MessengerNode's network information
    private String  NODE_HOST;
    private Integer NODE_PORT;

    //RecieverThread
    TCPRecieverThread RecieverThread;

    private MessagingNode(String RHOST, int RPORT){
        //TODO IMPLEMENT REGISTRY AND UNCOMMENT THESE
        this.REGISTRY_HOST = RHOST;
        this.REGISTRY_PORT = RPORT;

        //Initialize the TCPServerThread
        try {
            //acquire available tcp port
            this.NODE_HOST = InetAddress.getLocalHost().getHostName();
            this.NODE_PORT = Node.acquirePORT();

            //create/initialize server thread
            Thread newServerThread = new Thread(new TCPServerThread(NODE_PORT));
            newServerThread.start();

            //create/initialize server thread
            Thread RecieverThread = new Thread(new TCPRecieverThread(NODE_HOST, NODE_PORT));
            RecieverThread.start();

        } catch (IOException e){
            System.out.println("Registry::failed_starting_server_thread:: " + e);
            System.exit(1);
        }
    }

    //First Arg = registry's Host address
    //Second Arg =  registry's port number
    public static void main(String[] args){
        if(args.length != 2){
            System.out.println("INCORRECT ARGUMENTS, HOST then PORT REQUIRED");
            return;
        }
        String args0 = args[0];
        int args1 = Integer.parseInt(args[1]);
        MessagingNode node = new MessagingNode(args0, args1);
    }
}
