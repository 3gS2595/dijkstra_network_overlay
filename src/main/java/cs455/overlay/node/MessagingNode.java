package cs455.overlay.node;

import cs455.overlay.transport.*;
import cs455.overlay.util.DijkstrasPath;
import cs455.overlay.wireformats.Deregister_Request;
import cs455.overlay.wireformats.MessagingNodesList;
import cs455.overlay.wireformats.Register_Request;
import cs455.overlay.wireformats.TaskInitiate;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class MessagingNode implements Node{

    //TODO DISABLE DEBUG TOGGLE
    private boolean debug = true;
    private ArrayList<String> networkConnections = new ArrayList<>();
    private ArrayList<String> networkWeights = new ArrayList<>();

    //Registry's network information
    private String  REGISTRY_HOST;
    private Integer REGISTRY_PORT;

    //MessengerNode's network information
    private String  NODE_HOST;
    private Integer NODE_PORT;

    private static Boolean running = true;


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
            Thread serverThread = new Thread(new TCPServerThread(NODE_PORT, this));
            serverThread.start();

            if (debug) {
                System.out.println("INITIALIZED MESSENGER NODE\n" +
                    "NODE_HOST: " + NODE_HOST + "\n" +
                    "NODE_PORT: " + NODE_PORT + "\n");
            }
            this.userInput(this);

        } catch (IOException e){
            System.out.println("Registry::failed_starting_server_thread:: " + e);
            System.exit(1);
        }
    }

    //USER INPUT
    private void userInput(MessagingNode node){
        //USER COMMAND INPUT
        Scanner scanner = new Scanner(System.in);
        while(true){
            String in = scanner.nextLine();
            switch (in) {
                case "print-shortest-path":
                    System.out.println("tat");
                    break;
                case "exit-overlay":
                    node.terminateNode();
                    break;
                case "tom":
                    System.out.println(networkConnections.size());
                    break;
                default:
                    System.out.println("command not recognized");
                    break;
            }
        }
    }

    private void terminateNode(){
        try {
            new Deregister_Request(this);
        } catch (IOException e){
            System.out.println("Registry::failed_starting_server_thread:: " + e);
        }
    }

        //INTAKES GIVEN NETWORK OVERLAY
    public void setNetwork(MessagingNodesList.Pair[] network) throws IOException{
        for(MessagingNodesList.Pair temp : network){
            System.out.println(temp.toKey());
            this.networkConnections.add(temp.toKey());
            new Register_Request(this, temp.toKey());
        }
    }

    //INTAKES GIVEN NETWORK OVERLAY
    public void setNetworkWeights(ArrayList<String> connectionWeights){
        this.networkWeights = new ArrayList<>(connectionWeights);
        System.out.println("WEIGHTS");
        for (String temp : connectionWeights){
            String[] tempA = temp.split(" ");
            System.out.println(temp);
        }
        //DijkstrasPath temp = new DijkstrasPath();
        //String[] path = temp.DijkstrasPath(connectionWeights, this.getKey(), "lazer-VirtualBoc:1029");
        //System.out.println();
        //for(String key : path){
        //    System.out.println(key);
        //}
    }

    public void addConnection(String key){
        System.out.println(key);
        this.networkConnections.add(key);
    }

    //IDENTIFICATION
    public boolean isMessenger(){ return true; }

    public void taskInitiate(){ }


    //GETTERS
    public String getAddr() { return this.NODE_HOST; }
    public int    getPort() { return this.NODE_PORT; }

    private String getRegAddr() { return this.REGISTRY_HOST; }
    private int    getRegPort() { return this.REGISTRY_PORT; }

    public String getKey() { return this.getAddr() + ":" + this.getPort(); }
    public String getRegKey() { return REGISTRY_HOST + ":" + REGISTRY_PORT; }

    //First Arg  = registry's Host address
    //Second Arg = registry's port number
    //Reroutes arguments to MessagingNode's Constructor
    public static void main(String[] args){
        if(args.length != 2) {
            System.out.println("INCORRECT ARGUMENTS FOR MESSENGER NODE");
            return;
        }
        MessagingNode me = new MessagingNode(args[0], Integer.parseInt(args[1]));
        me.networkConnections = new ArrayList<>();
        while(running != true){

        }
        return;
    }
}
