package cs455.overlay.wireformats;

public interface Protocol {
    int REGISTER_REQ = 1;
    int REGISTER_RES = 2;
    int DEREGISTER_REQ = 3;
    int DEREGISTER_RES = 4;
    int MESSAGING_NODES_LIST = 5;
    int MESSAGING_NODES_WEIGHTS = 6;
    int TASK_INITIATE = 8;
    int DATA_MESSAGE = 9;
    int TASK_COMPLETE = 10;
    int PULL_TRAFFIC_SUMMARY = 11;
    int TRAFFIC_SUMMARY = 12;

}
