package cs455.overlay.wireformats;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.node.Node;
import cs455.overlay.transport.TCPSender;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class TaskInitiate {

    public TaskInitiate(){
    }
        //SENDS REQUEST
    public TaskInitiate(String key, int rounds) throws IOException {
        byte[][] messageBytes = new byte[1][];

        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout =
            new DataOutputStream(new BufferedOutputStream(baOutputStream));

        //insert the deregister request protocol
        dout.writeByte(rounds);

        dout.flush();
        messageBytes[0] = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();

        TCPSender.sendMessage(key, (byte) 8, 1, messageBytes);
    }

    public void task(MessagingNode Node, ArrayList<String> connections) throws IOException {
        System.out.println("SIZE FUCK " + connections.size());
        for (String key : connections) {
            System.out.println(key);
                byte[][] messageBytes = new byte[1][];
                messageBytes[0] = Node.getKey().getBytes();
                if (key == null)
                    TCPSender.sendMessage(Node.getRegKey(), (byte) 1, 1, messageBytes);
                else {
                    TCPSender.sendMessage(key, (byte) 1, 1, messageBytes);
                }
            }
        }

}