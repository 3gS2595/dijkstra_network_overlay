package cs455.overlay.node;

import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.OverlayCreator;
import cs455.overlay.wireformats.EventFactory;
import cs455.overlay.wireformats.MessagingNodesList;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;


public class Registry implements Node{
    //TODO DISABLE DEBUG TOGGLE
    private boolean debug = true;

    //Registry's network information
    private String  REGISTRY_HOST;
    private Integer REGISTRY_PORT;
    public  final static int[] IS_SETUP = new int[1];

    //The only EventFactory Instance
    public final static EventFactory Factory = new EventFactory();

    //Actual "registry" (hashed)
    public final static MessagingNodesList NODE_LIST = new MessagingNodesList();

    //CONSTRUCTOR
    private Registry(int ARG_REGISTER_PORT) {

        //Records the given temrinal argument
        this.REGISTRY_PORT = ARG_REGISTER_PORT;
        try {
            //Attains the Server address of the used machine
            this.REGISTRY_HOST = InetAddress.getLocalHost().getHostName();

            //DEBUG
            if (debug) {
                System.out.println("INITIALIZED REGISTRY NODE\n" +
                    "REGISTRY_HOST: " + REGISTRY_HOST + "\n" +
                    "REGISTRY_PORT: " + REGISTRY_PORT + "\n");
            }

            //Initializes a TCPServerThread
            TCPServerThread ServerThread = new TCPServerThread(this.REGISTRY_PORT, this);
            Thread newServerThread = new Thread(ServerThread);
            newServerThread.start();

        } catch (IOException e) {
            System.out.println("Registry::failed_starting_server_thread:: " + e);
            System.exit(1);
        }
    }

    //USER COMMAND INPUT
    private static void userInput(Registry node) throws IOException{
        Scanner scanner = new Scanner(System.in);
        while(true){
            String in = scanner.nextLine();
            String[] start = in.split(" ");
            switch (start[0]) {
                case "list-messaging-nodes":
                    System.out.println(NODE_LIST.print());
                    break;
                case "list-weights":
                    System.out.println("tat");
                    break;
                case "setup":
                    IS_SETUP[0] = 1;
                    new OverlayCreator(Integer.parseInt(start[1]));
                    break;
                case "send-overlay-link-weights":
                    break;
                case "start":
                    break;
                default:
                    System.out.println("command not recognized");
                    break;
            }
            System.out.println();
        }
    }

    //Identification
    public boolean isMessenger(){ return false; }

    //GETTERS
    public String getAddr() { return REGISTRY_HOST; }
    public int    getPort() { return REGISTRY_PORT; }

    //NORMALLY NEVER CALLED ON REGISTRY NODE
    public String getRegAddr() { return "-1"; }
    public int    getRegPort() { return  -1;  }

    public String    getKey() {return this.getAddr() + ":" + this.getPort(); }
    public String    getRegKey() {return this.getAddr() + ":" + this.getPort(); }



    //First Arg = TCP Port to use for registry
    public static void main(String[] args) throws IOException{
        IS_SETUP[0] = 0;
        if(args.length != 1) {
            System.out.println("INCORRECT ARGUMENTS FOR REGISTRY NODE");
            return;
        }
        Registry thisRegistry = new Registry(Integer.parseInt(args[0]));
        userInput(thisRegistry);
    }
}