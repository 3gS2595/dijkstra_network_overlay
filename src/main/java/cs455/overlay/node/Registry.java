package cs455.overlay.node;

import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.MessagingNodesList;

import java.io.*;
import java.net.*;

public class Registry implements Node{
    //Actual "registry" (hashed)
    public MessagingNodesList NODE_LIST = new MessagingNodesList();

    //Registry's network information'
    private String  REGISTRY_HOST;
    private Integer REGISTRY_PORT;

    //TODO DISABLE DEBUG TOGGLE
    private boolean debug = true;

    //CONSTRUCTOR
    private Registry(int REGPORT){
        this.REGISTRY_PORT = REGPORT;

        //Records the Server address of the used machine
        //Initializes a TCPServerThread
        try {
            this.REGISTRY_HOST = InetAddress.getLocalHost().getHostName();
            if(debug) {
                System.out.println("INITIALIZED REGISTRY NODE");
                System.out.println("SERVER_ADDRESS: " + REGISTRY_HOST);
                System.out.println("PORT          : " + REGISTRY_PORT);
                System.out.println();
            }

            Thread newServerThread = new Thread(new TCPServerThread(REGISTRY_PORT, this));
            newServerThread.start();
        } catch (IOException e){
            System.out.println("Registry::failed_starting_server_thread:: " + e);
            System.exit(1);
        }
    }

    //First Arg = TCP Port to use for registry
    public static void main(String[] args) {
        if(args.length != 1){
            System.out.println("INCORRECT ARGUMENTS FOR REGISTRY NODE");
            return;
        }
        Registry node = new Registry(Integer.parseInt(args[0]));
    }

    //Used to declare DEBUG mode on all Registry nodes
    public boolean getDebug() { return this.debug; }

    public String getAddr(){
        return REGISTRY_HOST;
    }
    public int    getPort() { return  REGISTRY_PORT;  }

    //SHOULD NEVER GET CALLED ON A REGISTRY NODE
    public String getRegAddr() { return "-1"; }
    public int    getRegPort() { return  -1;  }
}
