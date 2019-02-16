package cs455.overlay.util;

import cs455.overlay.node.Registry;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.wireformats.*;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

//only used by the Registry Node
public class OverlayCreator {

    //creates and then sends the overlay to the MessagingNodes
    public OverlayCreator(int linkLimit){
        //VARIABLES DESCRIPTIONS
        //number of nodes that have been registered with this registry
        int numOfNodes = Registry.NODE_LIST.NODE_REGISTRY_ARRAY.size();

        //nodeList, an array of all registered nodes info
        ArrayList<MessagingNodesList.Pair> nodeList = new ArrayList<>();
        Object[] values = Registry.NODE_LIST.NODE_REGISTRY_ARRAY.values().toArray();
        //places all registered nodes into nodeList array
        for (int curNode = 0; curNode < numOfNodes; curNode++)
            nodeList.add((MessagingNodesList.Pair)values[curNode]);

        //network table is the final collection of all nodes and their connections
        //tested, used to verify a connection has not already been assigned
        HashMap<String, ArrayList<MessagingNodesList.Pair>> networkTable = new HashMap<>();
        HashMap<String, ArrayList<MessagingNodesList.Pair>> networkTableSenders = new HashMap<>();
        ArrayList<String> tested = new ArrayList<>();

        //links all nodes in circular path
        for (int curNode = 0; curNode < nodeList.size(); curNode++) {
            //the final return variable which houses a single nodes network connections
            ArrayList<MessagingNodesList.Pair> connections = new ArrayList<>();
            ArrayList<MessagingNodesList.Pair> connectionsSender = new ArrayList<>();


            //the two keys used in placing records of what nodes have already been
            //added to the overlay (avoids any overlap)
            String key2 = getKey(nodeList,curNode, 0);
            String key1;

            //CONNECTS to the node infront of it
            if(curNode + 1 < nodeList.size()) {
                connections.add(nodeList.get(curNode + 1));
                connectionsSender.add(nodeList.get(curNode + 1));
                key1 = getKey(nodeList,curNode, 1);
            } else {
                connections.add(nodeList.get(0));
                connectionsSender.add(nodeList.get(0));
                key1 = getKey(nodeList,0, 0);
            }
            tested.add(key1 + ":" + key2);
            tested.add(key2 + ":" + key1);

            //CONNECTS to the node behind it
            if(curNode - 1 > (-1)) {
                connections.add(nodeList.get(curNode - 1));
                key1 = getKey(nodeList,curNode, -1);
            } else {
                connections.add(nodeList.get(nodeList.size()-1));
                key1 = getKey(nodeList,nodeList.size(), -1);
            }
            tested.add(key1 + " " + key2);
            tested.add(key2 + " " + key1);

            //adds the final resulting
            String key = getKey(nodeList, curNode,0);
            networkTable.put(key, connections);
            networkTableSenders.put(key, connectionsSender);
        }

        //completes links on all nodes until limit is reached
        for (int curNode = 0; curNode < nodeList.size(); curNode++) {
            String connectionKey;
            String thisKey = getKey(nodeList, curNode,0);
            int modifier = curNode;

            //Stops once connection limit is reached
            if(networkTable.get(thisKey).size() < linkLimit){

                //LINKS CURRENT TO THE NODE TWO PLACES AHEAD
                if(curNode + 2 < nodeList.size()) {
                    connectionKey = getKey(nodeList, curNode,2);
                    modifier += 2;
                }
                //CATCHES THE FIRST AND LAST CASE AND CONNECTS THEM
                else {
                    int place;
                    if(curNode + 2 == nodeList.size())
                        place = 0;
                    else
                        place = 1;
                    connectionKey = getKey(nodeList, place,0);
                    modifier = place;
                }

                //If the onnection hasnt already been made it assigns it
                if(!tested.contains(thisKey + ":" + connectionKey)) {
                    networkTable.get(thisKey).add(nodeList.get(modifier));
                    networkTable.get(connectionKey).add(nodeList.get(curNode));
                    //adds to record to avoid using twice
                    tested.add(thisKey + ":" + connectionKey);
                    tested.add((connectionKey + ":" + thisKey));

                    networkTableSenders.get(thisKey).add(nodeList.get(modifier));
                }
            }
        }

        //sends netwrok information to respective nodes
        //DATA FORMAT SENT
        //(int) type
        //(int) number of incoming nodes
        //  for each node
        //  (int) length of string
        //  (String) address
        //  (int) port
        for (String thisKey: networkTableSenders.keySet()) {
            try {
                //Connection initialization based on keys information
                //curNetwork is the current nodes network networkTable entry
                ArrayList<MessagingNodesList.Pair> curNetwork = networkTable.get(thisKey);
                ArrayList<MessagingNodesList.Pair> curNetworks = networkTableSenders.get(thisKey);

                String[] temp = thisKey.split(":");
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
                int numberOfNodes = curNetworks.size();
                dout.writeInt(numberOfNodes);

                //records each node from current nodes networkTable entry
                for (MessagingNodesList.Pair messenger: curNetworks) {
                    //insert the Address then the port of the node
                    String notationString = messenger.getADDRESS().concat(":" + messenger.getPORT().toString());
                    byte[] data = notationString.getBytes();
                    dout.writeInt(data.length);
                    dout.write(data);
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

    private String getKey(ArrayList<MessagingNodesList.Pair> nodeList, int place, int modifier){
        return nodeList.get(place + modifier).getADDRESS() + ":"
            + nodeList.get(place + modifier).getPORT();
    }
}
