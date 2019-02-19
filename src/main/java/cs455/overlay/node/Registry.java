package cs455.overlay.node;

import cs455.overlay.transport.TCPSender;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.OverlayCreator;
import cs455.overlay.wireformats.EventFactory;
import cs455.overlay.wireformats.MessagingNodesList;
import cs455.overlay.wireformats.TaskInitiate;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Scanner;


public class Registry implements Node{
    //TODO DISABLE DEBUG TOGGLE
    private boolean debug = true;

    //Registry's network information
    private String  REGISTRY_HOST;
    private Integer REGISTRY_PORT;
    private static ArrayList<String> networkTable;

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

    //TODO SET TASK_INITIATE TO START MESSAGING NODES TALKING
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
                    listWeights();
                    break;

                //TODO REGISTRY NEEDS TO CHECK OVERLAY
                case "setup":
                    OverlayCreator overlay = new OverlayCreator();
                    int size = 4;
                    if (start.length != 1)
                        size = Integer.parseInt(start[1]);
                    setNetwork(overlay.OverlayCreate(size, 1));
                    break;
                case "send":
                    sendWeights();
                    break;
                case "start":
                    taskInitiates();
                    break;
                default:
                    System.out.println("command not recognized");
                    break;
            }
            System.out.println();
        }
    }

    private static void listWeights(){
        for (String key: networkTable)
            System.out.println(key);
    }

    private static void sendWeights() throws IOException{
        int wnum = 0;
        byte[][] weightBytes = new byte[networkTable.size()][];
        for (String thisKey: networkTable) {
            byte[] data = thisKey.getBytes();
            weightBytes[wnum] = data;
            wnum++;
        }
        //SENDS weight information
        for (String thisKey: NODE_LIST.NODE_REGISTRY_ARRAY.keySet()) {
            TCPSender.sendMessage(thisKey, 6, wnum, weightBytes);
        }
    }



    //RECEIVES OVERLAY AND WEIGHT IN ONE SWEEP
    private static void setNetwork(ArrayList<String> table){
        networkTable = table;
    }

    //Identification
    public boolean isMessenger(){ return false; }

    //GETTERS
    public String getAddr() { return REGISTRY_HOST; }
    public int    getPort() { return REGISTRY_PORT; }


    public String    getKey() {return this.getAddr() + ":" + this.getPort(); }
    public String    getRegKey() {return this.getAddr() + ":" + this.getPort(); }

    public void taskInitiate() {}

    public static void taskInitiates() {
        try {
            for (String key : NODE_LIST.NODE_REGISTRY_ARRAY.keySet()) {
                new TaskInitiate(key, 5);
            }
        }catch (IOException e){
        }
    }


    //First Arg = TCP Port to use for registry
    public static void main(String[] args) throws IOException{
        if(args.length != 1) {
            System.out.println("INCORRECT ARGUMENTS FOR REGISTRY NODE");
            return;
        }
        Registry thisRegistry = new Registry(Integer.parseInt(args[0]));
        userInput(thisRegistry);
    }
}