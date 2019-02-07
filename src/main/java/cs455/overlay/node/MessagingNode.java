package cs455.overlay.node;

import cs455.overlay.transport.*;
import java.io.*;
import java.net.*;

public class MessagingNode implements Node{
    //Registry's network information
    private String  REGISTRY_HOST;
    private Integer REGISTRY_PORT;

    //MessengerNode's network information
    private String  NODE_HOST;
    private Integer NODE_PORT;

    //TODO DISABLE DEBUG TOGGLE
    private boolean debug = true;

    //CONSTRUCTOR
    private MessagingNode(String REGHOST, int REGPORT){
        this.REGISTRY_HOST = REGHOST;
        this.REGISTRY_PORT = REGPORT;

        //Initializes the TCPServerThread
        try {
            //acquire available tcp port
            this.NODE_HOST = InetAddress.getLocalHost().getHostName();
            this.NODE_PORT = Node.acquirePORT();

            //create/initialize server thread
            Thread newServerThread = new Thread(new TCPServerThread(NODE_PORT, this));
            newServerThread.start();

            if(debug) {
                System.out.println("INITIALIZED MESSAGING NODE");
                System.out.println("NODE_HOST: " + NODE_HOST);
                System.out.println("NODE_PORT: " + NODE_PORT);
                System.out.println();
            }

        } catch (IOException e){
            System.out.println("Registry::failed_starting_server_thread:: " + e);
            System.exit(1);
        }
    }

    //First Arg = registry's Host address
    //Second Arg =  registry's port number
    public static void main(String[] args){
        if(args.length != 2){
            System.out.println("INCORRECT ARGUMENTS FOR MESSENGER NODE");
            return;
        }
        String args0 = args[0];
        int args1 = Integer.parseInt(args[1]);
        MessagingNode node = new MessagingNode(args0, args1);
    }

    //Used in constructing registry request atm in TCPServerThread
    //Can be used to identify type of node (reg returns -1)
    public String getAddr() { return this.NODE_HOST; }
    public int    getPort() { return this.NODE_PORT; }

    public String getRegAddr() { return this.REGISTRY_HOST; }
    public int    getRegPort(){
        return this.REGISTRY_PORT;
    }

}
