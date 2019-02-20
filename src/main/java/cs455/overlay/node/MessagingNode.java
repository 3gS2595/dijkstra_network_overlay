package cs455.overlay.node;

import cs455.overlay.transport.*;
import cs455.overlay.util.DijkstrasPath;
import cs455.overlay.wireformats.Deregister_Request;
import cs455.overlay.wireformats.MessagingNodesList;
import cs455.overlay.wireformats.Register_Request;
import cs455.overlay.wireformats.TaskComplete;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;
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

    public Integer sendTracker = 0;
    public Integer relayTracker = 0;
    public Integer receiveTracker = 0;

    public float receiveSummation = 0;
    public float sendSummation = 0;



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
        try {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String in = scanner.nextLine();
                switch (in) {
                    case "print-shortest-path":
                        System.out.println("tat");
                        break;
                    case "exit-overlay":
                        node.terminateNode();
                        break;
                    case "tom":
                        this.taskInitiate(1);
                        break;
                    default:
                        System.out.println("command not recognized");
                        break;
                }
            }
        }catch (IOException e){

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
            this.networkConnections.add(temp.toKey());
            new Register_Request(this, temp.toKey());
        }
        System.out.println(network.length + " NODES SET TO INITIALIZE");

    }

    //INTAKES GIVEN NETWORK OVERLAY
    public void setNetworkWeights(ArrayList<String> connectionWeights){
        this.networkWeights = new ArrayList<>(connectionWeights);

        System.out.println(connectionWeights.size() + " NETWORK WEIGHTS RECEIVED");
        //DijkstrasPath temp = new DijkstrasPath();
        //String[] path = temp.DijkstrasPath(connectionWeights, this.getKey(), "lazer-VirtualBoc:1029");
        //System.out.println();
        //for(String key : path){
        //    System.out.println(key);
        //}
    }

    public void addConnection(String key){
        this.networkConnections.add(key);
    }

    public void taskInitiate(int rounds) throws IOException{
        Random rn = new Random(System.currentTimeMillis());
        ArrayList<String> temp = new ArrayList<>();

        for (String connection :networkWeights){
            String[] keys = connection.split(" ");
            if(!temp.contains(keys[0]))
                temp.add(keys[0]);
            if(!temp.contains(keys[1]))
                temp.add(keys[1]);
        }

        for(int i = 0; i < rounds;i++) {
            //picks random node to send to
            int node = rn.nextInt((temp.size() - 1) + 1);
            while (temp.get(node).contentEquals(this.getKey()))
                node = rn.nextInt((temp.size() - 1) + 1);

            DijkstrasPath finder = new DijkstrasPath();
            String path = finder.DijkstrasPath(networkWeights, this.getKey(), temp.get(node));
            String[] dest = path.split(" ");
            String nextPath = "";

            for (int x = 2; x < dest.length; x++)
                nextPath += dest[x] + " ";

            //constructs messageBytes
            int rando = rn.nextInt();
            byte[] payload = toByteArray(rando);
            byte[][] messageBytes = new byte[2][];
            messageBytes[0] = payload;
            messageBytes[1] = nextPath.getBytes();
            TCPSender.sendMessage(dest[2], (byte) 9, -5, messageBytes);
            this.sendTracker++;
            sendSummation += rando;
        }
        new TaskComplete(this);
    }

    private byte[] toByteArray(int value) {
        return new byte[] {
            (byte)(value >> 24),
            (byte)(value >> 16),
            (byte)(value >> 8),
            (byte)value };
    }

    //getters
    public boolean isMessenger(){ return true; }
    public String getKey() { return this.NODE_HOST + ":" + this.NODE_PORT; }
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
        return;
    }
}
