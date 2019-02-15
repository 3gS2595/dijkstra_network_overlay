package cs455.overlay.wireformats;

import cs455.overlay.node.Registry;
import cs455.overlay.transport.TCPSender;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

//TODO CLEAN UP THE INSANITY THAT THIS HAS BECOME
//works tho :)

public class createOverlay {
    //sends and creates the overlay
    public createOverlay(int linkLimit){
        int numOfNodes = Registry.NODE_LIST.NODE_REGISTRY_ARRAY.size();
        ArrayList<MessagingNodesList.Pair> nodeList = new ArrayList<>();
        Object[] values = Registry.NODE_LIST.NODE_REGISTRY_ARRAY.values().toArray();
        HashMap<String, ArrayList<MessagingNodesList.Pair>> networkTable = new HashMap<>();
        ArrayList<String> tested = new ArrayList<>();

        //places all registered nodes into nodeList array
        for (int curNode = 0; curNode < numOfNodes; curNode++) {
            nodeList.add((MessagingNodesList.Pair)values[curNode]);
        }

        //links all nodes in circular path
        for (int curNode = 0; curNode < nodeList.size(); curNode++) {
            ArrayList<MessagingNodesList.Pair> connections = new ArrayList<>();
            String key2 = nodeList.get(curNode).getADDRESS() + " " + + nodeList.get(curNode).getPORT();

            //adds node infront
            if(curNode + 1 < nodeList.size()) {
                connections.add(nodeList.get(curNode + 1));
                String key1 = nodeList.get(curNode + 1).getADDRESS() + " " + nodeList.get(curNode + 1).getPORT();
                tested.add(key1 + " " + key2);
                tested.add(key2 + " " + key1);
            } else {
                connections.add(nodeList.get(0));
                String key1 = nodeList.get(0).getADDRESS() + " " + nodeList.get(0).getPORT();
                tested.add(key1 + " " + key2);
                tested.add(key2 + " " + key1);
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
                    }
                } else {
                    //CATCHES FIRST AND LAST CASES (CONNECTS THEM)
                    int place = 1;
                    if(curNode + 2 == nodeList.size()) {
                        place = 0;
                    }
                    String connectionKey = nodeList.get(place).getADDRESS()
                        + " "
                        + nodeList.get(place).getPORT();
                    networkTable.get(thisKey).add(nodeList.get(place));
                    networkTable.get(connectionKey).add(nodeList.get(curNode));
                }

            }
        }

        //sends netwrok information to respective nodes
        for (String thisKey: networkTable.keySet()) {
            try {
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
                for (MessagingNodesList.Pair messenger: curNetwork) {
                    //insert the Address then the port of the node
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
}
