package cs455.overlay.util;

import cs455.overlay.node.Registry;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.wireformats.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


//only used by the Registry Node
public class OverlayCreator {
    private ArrayList<String> tested = new ArrayList<>();

    //creates and then sends the overlay to the MessagingNodes
    public OverlayCreator(int linkLimit) throws IOException{

        //number of nodes that have been registered with this registry
        int numOfNodes = Registry.NODE_LIST.NODE_REGISTRY_ARRAY.size();

        //nodeList, an array of all registered nodes info
        ArrayList<MessagingNodesList.Pair> nodeList = new ArrayList<>();
        Object[] values = Registry.NODE_LIST.NODE_REGISTRY_ARRAY.values().toArray();
        for (int curNode = 0; curNode < numOfNodes; curNode++)
            nodeList.add((MessagingNodesList.Pair)values[curNode]);

        //network table is the final collection of all nodes and their connections
        HashMap<String, ArrayList<MessagingNodesList.Pair>> networkTable = new HashMap<>();
        HashMap<String, ArrayList<MessagingNodesList.Pair>> networkTableSenders = new HashMap<>();

        //links all nodes in circular path
        for (int curNode = 0; curNode < nodeList.size(); curNode++) {
            //the final return variable which houses a single nodes network connections
            ArrayList<MessagingNodesList.Pair> connections = new ArrayList<>();
            ArrayList<MessagingNodesList.Pair> connectionsSender = new ArrayList<>();


            //the two keys used in placing records of what nodes have already been
            //added to the overlay (avoids any overlap)
            String thisKey = getKey(nodeList,curNode, 0);
            String otherNodesKey;

            //CONNECTS to the node infront of it
            if(curNode + 1 < nodeList.size()) {
                connections.add(nodeList.get(curNode + 1));
                connectionsSender.add(nodeList.get(curNode + 1));
                otherNodesKey = getKey(nodeList,curNode, 1);
            } else {
                connections.add(nodeList.get(0));
                connectionsSender.add(nodeList.get(0));
                otherNodesKey = getKey(nodeList,0, 0);
            }
            recordConnections(otherNodesKey, thisKey);

            //CONNECTS to the node behind it
            if(curNode - 1 > (-1)) {
                connections.add(nodeList.get(curNode - 1));
                otherNodesKey = getKey(nodeList,curNode, -1);
            } else {
                connections.add(nodeList.get(nodeList.size()-1));
                otherNodesKey = getKey(nodeList,nodeList.size(), -1);
            }
            recordConnections(otherNodesKey, thisKey);

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
                    recordConnections(thisKey, connectionKey);

                    networkTableSenders.get(thisKey).add(nodeList.get(modifier));
                }
            }
        }

        //CREATES AND SENDS THE MESSAGES
        //these variable used sending the weights at the end
        int wnum = 0;
        byte[][] weightBytes = new byte[numOfNodes*linkLimit][];

        //sends netwrok information to respective nodes
        for (String thisKey: networkTableSenders.keySet()) {
            //curNetwork is the current nodes network networkTable entry
            ArrayList<MessagingNodesList.Pair> curNetworks = networkTableSenders.get(thisKey);
            int numberOfNodes = curNetworks.size();
            byte[][] messageBytes = new byte[numberOfNodes][];

            //tailors array connection messages array
            int num = 0;
            for (MessagingNodesList.Pair messenger: curNetworks) {
                //insert the Address then the port of the node
                String notationString = messenger.getADDRESS().concat(":" + messenger.getPORT().toString());
                byte[] data = notationString.getBytes();
                messageBytes[num] = data;
                num++;
            }
            //SENDS connection information
            TCPSender.sendMessage(thisKey, 5, numberOfNodes, messageBytes);

            //generates connection weights array
            for (MessagingNodesList.Pair messenger: curNetworks) {
                //generates weight and then sends
                Random rn = new Random();
                int weight = rn.nextInt(10) + 1;
                String notationString = thisKey.concat(" " +
                    messenger.getADDRESS() +
                    ":" + messenger.getPORT().toString() + " " + weight);
                byte[] data = notationString.getBytes();
                weightBytes[wnum] = data;
                wnum++;
            }
        }

        //SENDS weight information
        for (String thisKey: networkTableSenders.keySet()) {
            TCPSender.sendMessage(thisKey, 6, wnum, weightBytes);
        }
    }

    private void recordConnections(String key1, String key2){
        tested.add(key1 + " " + key2);
        tested.add(key2 + " " + key1);
    }

    private String getKey(ArrayList<MessagingNodesList.Pair> nodeList, int place, int modifier){
        return nodeList.get(place + modifier).getADDRESS() + ":"
            + nodeList.get(place + modifier).getPORT();
    }
}
