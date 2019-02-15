package cs455.overlay.node;

import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.wireformats.EventFactory;
import cs455.overlay.wireformats.MessagingNodesList;

import java.util.*;
import java.net.*;
import java.util.Scanner;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;

public class Registry implements Node{
    //TODO DISABLE DEBUG TOGGLE
    private boolean debug = true;

    //The only EventFactory Instance
    public final static EventFactory Factory = new EventFactory();

    //Actual "registry" (hashed)
    public final static MessagingNodesList NODE_LIST = new MessagingNodesList();

    //Registry's network information
    private String  REGISTRY_HOST;
    private Integer REGISTRY_PORT;
    private TCPServerThread ServerThread;

    //CONSTRUCTOR
    private Registry(int ARG_REGISTER_PORT) {

        //Records the given temrinal argument
        this.REGISTRY_PORT = ARG_REGISTER_PORT;
        try {
            //Attains the Server address of the used machine
            this.REGISTRY_HOST = InetAddress.getLocalHost().getHostName();

            //Enters the registry info for entire JVM to access
            this.Factory.set(this.REGISTRY_HOST, this.REGISTRY_PORT);

            //DEBUG
            if (debug) {
                System.out.println("INITIALIZED REGISTRY NODE\n" +
                    "REGISTRY_HOST: " + REGISTRY_HOST + "\n" +
                    "REGISTRY_PORT: " + REGISTRY_PORT + "\n");
            }

            //Initializes a TCPServerThread
            ServerThread = new TCPServerThread(this.REGISTRY_PORT, this);
            Thread newServerThread = new Thread(ServerThread);
            newServerThread.start();

        } catch (IOException e) {
            System.out.println("Registry::failed_starting_server_thread:: " + e);
            System.exit(1);
        }
    }

    //USER COMMAND INPUT
    private static void userInput(Registry node){
        Scanner scanner = new Scanner(System.in);
        while(true){
            String in = scanner.nextLine();
            String[] start = in.split(" ");
            switch (start[0]) {
                case "list-messaging-nodes":
                    System.out.println(node.NODE_LIST.print());
                    break;
                case "list-weights":
                    System.out.println("tat");
                    break;
                case "setup":
                    node.sendOverlay(Integer.parseInt(start[1]));
                    break;
                case "send-overlay-link-weights":
                    break;
                default:
                    System.out.println("command not recognized");
                    break;
            }
        }
    }

    //sends and creates the overlay
    private void sendOverlay(int linkLimit){
        int numOfNodes = NODE_LIST.NODE_REGISTRY_ARRAY.size();
        ArrayList<MessagingNodesList.Pair> nodeList = new ArrayList<>();
        Object[] values = NODE_LIST.NODE_REGISTRY_ARRAY.values().toArray();
        HashMap<String, ArrayList<MessagingNodesList.Pair>> networkTable = new HashMap<>();
        ArrayList<String> tested = new ArrayList<>();

        //places all registered nodes into nodeList array
        for (int curNode = 0; curNode < numOfNodes; curNode++) {
            nodeList.add((MessagingNodesList.Pair)values[curNode]);
        }

        //links all nodes in circular path
        for (int curNode = 0; curNode < nodeList.size(); curNode++) {
            ArrayList<MessagingNodesList.Pair> connections = new ArrayList<>();

            System.out.println(nodeList.size());
            //adds node infront
            if(curNode + 1 < nodeList.size()) {
                connections.add(nodeList.get(curNode + 1));
                tested.add(nodeList.get(curNode + 1).getADDRESS()
                    + " "
                    + nodeList.get(curNode + 1).getPORT()
                    + " "
                    + nodeList.get(curNode).getADDRESS()
                    + " " +
                    + nodeList.get(curNode).getPORT());
            } else {
                connections.add(nodeList.get(0));
            }

            //adds node behind
            if(curNode - 1 > (-1)) {
                connections.add(nodeList.get(curNode - 1));
            } else {
                connections.add(nodeList.get(nodeList.size()-1));
            }

            String key = nodeList.get(curNode).getADDRESS()
                + " "
                + nodeList.get(curNode).getPORT();
            networkTable.put(key, connections);
        }

        //completes links on all nodes until limit is reached
        for (int curNode = 0; curNode < nodeList.size(); curNode++) {
            String thisKey = nodeList.get(curNode).getADDRESS()
                + " "
                + nodeList.get(curNode).getPORT();
            //cycles until limit is met.
            if(networkTable.get(thisKey).size() < linkLimit){
                //LINKS CURRENT TO THE NODE TWO PLACES AHEAD
                if(curNode + 2 < nodeList.size()) {
                    String connectionKey = nodeList.get(curNode + 2).getADDRESS()
                        + " "
                        + nodeList.get(curNode + 2).getPORT();

                        if(!tested.contains(thisKey + " " + connectionKey)) {
                            networkTable.get(thisKey).add(nodeList.get(curNode + 2));
                            networkTable.get(connectionKey).add(nodeList.get(curNode));
                            tested.add(thisKey + " " + connectionKey);
                            tested.add((connectionKey + " " + thisKey));
                            System.out.println(curNode);
                        }
                } else {
                    System.out.println("??");
                    //CATCHES FIRST AND LAST CASES (CONNECTS THEM)
                    int place = 1;
                    if(curNode + 1 == nodeList.size()) {
                        place = 0;
                    }
                    String connectionKey = nodeList.get(place).getADDRESS()
                        + " "
                        + nodeList.get(place).getPORT();
                    networkTable.get(thisKey).add(nodeList.get(place));
                    networkTable.get(connectionKey).add(nodeList.get(curNode));
                    System.out.println("???");
                }

            }
        }

            //sends netwrok information to respective nodes
        Iterator iter = networkTable.keySet().iterator();
        while (iter.hasNext()) {
            try {
                String thisKey = (String)iter.next();
                ArrayList<MessagingNodesList.Pair> curNetwork = networkTable.get(thisKey);
                String[] temp = thisKey.split(" ");
                String Address = temp[0];
                int Port = (Integer.parseInt(temp[1]));
                Socket REG_SOCKET = new Socket(Address, Port);
                TCPSender sender = new TCPSender(REG_SOCKET);

                ///creates Request message byte array
                byte[] marshaledBytes;

                //Initialize used streams
                ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
                DataOutputStream dout =
                    new DataOutputStream(new BufferedOutputStream(baOutputStream));

                //insert the deregister request protocol
                dout.writeByte(5);

                //inserts the number of nodes
                int numberOfNodes = curNetwork.size();
                dout.writeInt(numberOfNodes);

                //attatches spider legs to nodes that have not been connected yet
                //(jumps ahead and connects non linearly)
                for (int i = 0; i < curNetwork.size(); i++){
                    //insert the Address then the port of the node
                    MessagingNodesList.Pair messenger = curNetwork.get(i);
                    byte[] ADDRESS = messenger.getADDRESS().getBytes();
                    dout.writeInt(ADDRESS.length);
                    dout.write(ADDRESS);
                    dout.writeInt(messenger.getPORT());
                }

                //records payload and cleans up
                dout.flush();
                marshaledBytes = baOutputStream.toByteArray();
                baOutputStream.close();
                dout.close();

                //sends request
                sender.sendData(marshaledBytes);
                REG_SOCKET.close();

            } catch (IOException e) {
                System.out.println("Registry::failed_starting_server_thread:: " + e);
                System.exit(1);
            }

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

    //First Arg = TCP Port to use for registry
    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("INCORRECT ARGUMENTS FOR REGISTRY NODE");
            return;
        }
        Registry thisRegistry = new Registry(Integer.parseInt(args[0]));
        userInput(thisRegistry);
    }
}