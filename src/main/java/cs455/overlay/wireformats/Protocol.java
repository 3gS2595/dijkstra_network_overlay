package cs455.overlay.wireformats;

public interface Protocol {
    byte REGISTER_REQ = 1;
    byte REGISTER_RES = 2;

    byte DEREGISTER_REQ = 3;
    byte DEREGISTER_RES = 4;
}
