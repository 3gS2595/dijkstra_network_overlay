package cs455.overlay.wireformats;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.transport.TCPSender;
import java.io.IOException;

public class PULL_TRAFFIC_SUMMARY {
    //receives
    PULL_TRAFFIC_SUMMARY(byte[] marshaledBytes, MessagingNode Node) throws IOException{
        new TRAFFIC_SUMMARY(Node);
        System.out.println("HEYEYEYEYE");
        System.exit(1);
    }

    //sends
    public PULL_TRAFFIC_SUMMARY(String key) throws IOException {
        byte[][] messageBytes =  new byte[1][];
        messageBytes[0] = "do it".getBytes();
        TCPSender.sendMessage(key, (byte)11, 1, messageBytes);
    }
}