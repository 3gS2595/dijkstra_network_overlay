package cs455.overlay.node;

import java.io.IOException;
import java.net.ServerSocket;

public interface Node {
  static int acquirePORT(){
    //Find available port to use
    int PORT = -1;
    for (int tempPort = 1024; tempPort <= 65535; tempPort++) {
      ServerSocket ss = null;
      try {
        ss = new ServerSocket(tempPort);
        ss.setReuseAddress(true);
        PORT = tempPort;
        if (ss != null){
          ss.close();
        }
        break;
      } catch (IOException ex) {
        continue; // try next port
      }
    }
    //error trap if no port found
    if (PORT == -1) {
      // if the program gets here, no port in the range was found
      //TODO FILE ERROR
    }
    return PORT;
  }
  //void main(String[] args);
	//void get_server_address();
	//void get_port_address();
}	
