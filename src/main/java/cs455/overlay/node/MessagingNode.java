package main.java.cs455.overlay.node;

import cs455.overlay.node.Node;

public class MessagingNode implements Node{
  //Registry Node's network information
  static String REGISTRY_HOST;
  static int REGISTRY_PORT;

  //Messenger Node's network information
	String SERVER_ADDRESS;
	int PORT;

	//First Arg = registry's Host address
  //Second Arg =  registry's port number
	public static void main(String[] args){
    REGISTRY_HOST = args[0];
    REGISTRY_PORT = Integer.parseInt(args[1]);

	}
}
