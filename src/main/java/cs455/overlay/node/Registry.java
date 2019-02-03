package cs455.overlay.node;

import cs455.overlay.transport.TCPServerThread;

import java.io.DataInputStream;
import java.net.InetAddress;
import java.io.IOException;

public class Registry implements Node{
	//Registry's network information
	static String SERVER_ADDRESS;
	static int PORT;

	//First Arg = TCP Port to use for registry
  public static void main(String[] args){
    if (args.length >0){
      PORT = Integer.parseInt(args[0]);
    }
    DataInputStream INCOMING_MESSAGE = null;
    //Records the Server address of the used machine
    //Initializes a TCPServerThread
    try {
      SERVER_ADDRESS = InetAddress.getLocalHost().getHostName();
      TCPServerThread tcpServer = new TCPServerThread(PORT, INCOMING_MESSAGE);
    } catch (IOException e){
      System.out.println("Registry::failed_starting_server_thread:: " + e);
      System.exit(1);
    }
  }
}
