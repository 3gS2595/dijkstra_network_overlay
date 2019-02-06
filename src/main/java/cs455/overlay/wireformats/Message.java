package cs455.overlay.wireformats;

public class Message {
    private String  SOURCE_ADDRESS;
    private Integer SOURCE_PORT;
    private String  DESTINATION_ADDRESS;
    private Integer DESTINATION_PORT;
    private Integer PAYLOAD;

    public void message(String dest_addr, int dest_port, String sor_addr, int sor_port, int payload){
        this.DESTINATION_ADDRESS = dest_addr;
        this.DESTINATION_PORT = dest_port;
        this.SOURCE_ADDRESS = sor_addr;
        this.SOURCE_PORT = sor_port;
        this.PAYLOAD = payload;

    }

}
