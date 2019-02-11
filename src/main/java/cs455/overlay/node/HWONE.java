package cs455.overlay.node;

import java.util.*;


public class HWONE{
    private static int numVertices = 6;
    static int total = 0;
    int cnt = 0;
    int cnt2 = 0;

    public static void main(String[] args) {
        if (args.length == 1)
            numVertices = Integer.parseInt(args[0]);
        else {
            numVertices = 6;
        }
        HWONE game = new HWONE();
        game.run();
        //creates a root node with one move made

    }

    private void run(){
        line[] data = new line[15];
        data[0] = new line((byte)0,(byte)1, (byte)1);
        Node root = new Node(data);
        play(root, (byte)2);
        System.out.println("player A: " + cnt);
        System.out.println("player B: " + cnt2);
    }

    private void play(Node root, byte turn){
        if(total % 10000 == 0) {
            System.out.println(total);
        }
        byte player = turn;
        //creates the children
        if (getNumberOfChildren(root) == 0) {
            move(root, player);
        }
        if (player == 1) {
            player = 2;
        } else { player = 1; }
        //creates the grandchildren

        if(getNumberOfChildren(root) != 0) {
            for (int child = 0; child < getNumberOfChildren(root); child++) {
                play(root.children[child], player);
            }
        }
    }

    private void move(Node node, byte player){
        //TODO MOVE THE KID GENERATION INTO HERE WTF;
        //STILL UNDER CONSTRUCTION
//        if(getNumberOfChildren(node) != 0) {
//            for (int child = 0; child < getNumberOfChildren(node); child++) {
//                move(node.children[child], player);
//            }
//        }

        line[] tested = node.moves.clone();
        //cycles through all possible lines
        for (byte v1 = 0; v1 < numVertices; v1++) {
            for (byte v2 = 0; v2 < numVertices; v2++) {
                //If the move is available
                if ((v1 != v2) && (!contains(tested, (new line(v1, v2, (byte) 1))))) {

                    for (int x = 0; x < 15; x++) {
                        if (tested[x] == null) {
                            tested[x] = new line(v1, v2, player);
                            x = 16;
                        }
                    }

                    line[] nn = node.moves.clone();
                    nn[getNumberOfMOves(node)] = new line(v1, v2, player);

                    //finds any complete games
                    if (!(notOver(new Node(nn)) == 0)) {
                        node = null;
                        return;
                    }
                    node.addChild(new Node(nn));
                }
            }
        }
    }

    //CHECK FOR ENDGAME
    private byte notOver(Node node){
        for (int vect1 = 0; vect1 < getNumberOfMOves(node); vect1++) {
            for (int vect2 = 0; vect2 < getNumberOfMOves(node); vect2++) {
                if((node.moves[vect1].getColor() == node.moves[vect2].getColor())){
                    //if two connecting lineS are found it looks for a third
                    for (int vect3 = 0; vect3 < getNumberOfMOves(node); vect3++) {
                        //if not overlapping with other loops
                        if(vect1 != vect2 && vect2 != vect3 && vect3 != vect1) {
                            if ((node.moves[vect1].getColor() == node.moves[vect3].getColor())) {

                                String connects = Byte.toString(node.moves[vect1].v1) + Byte.toString(node.moves[vect1].v2)
                                    + Byte.toString(node.moves[vect2].v1) + Byte.toString(node.moves[vect2].v2)
                                    + Byte.toString(node.moves[vect3].v1) + Byte.toString(node.moves[vect3].v2);
                                if (connects.chars().distinct().count() == 3) {
                                    //System.out.println(connects);
                                    if (node.moves[vect1].getColor() == 1) {
                                        cnt++;
                                        System.out.println("1 " + node.ts());
                                        return 1;
                                        //System.out.println("1");
                                    }
                                    if (node.moves[vect1].getColor() == 2) {
                                        cnt2++;
                                        System.out.println("2 " + node.ts());
                                        return 2;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }

    //SINGLE MOVE
    private class line{
        private byte v1; //first vertex
        private byte v2; //second vertex
        private byte color; //0(null), 1(blue), or 2(red)

        private line(byte v1, byte v2, byte color) {
            this.v1 = v1;
            this.v2 = v2;
            this.color = color;
        }

        //GETTERS
        private byte getV1() { return v1; }
        private byte getV2() { return v2; }
        private byte getColor() { return color; }

        private boolean connects(line line){
            if ((this.v1 == line.v2) || (this.v1 == line.v1)
                || (this.v2 == line.v1) || (this.v2 == line.v2)
                && (this.getColor() == line.getColor())) {
                return true;
            }
            return false;
        }

        //TOSTRING
        private  String ts( ){
            String ret = "(" + this.getV1() + ", " + this.getV2() + ", " + this.getColor() + ")";
            return ret;
        }

        public void isNow(line o){
            this.v1 = o.v1;
            this.v2 = o.v2;
            this.color = o.v2;
        }
        //COMPARE
        public boolean equal(line o){
            if (o.getColor() == this.getColor()
                && (o.getV1() == this.getV1())
                && (o.getV2() == this.getV2()))
                return true;
            return false;
        }
    }

    //GAMEBOARD (AND CHILDREN)
    private class Node{
        line[] moves = new line[15];
        Node[] children = new Node[15];

        //CONSTRUCTOR
        private Node(line[] moves){
            this.moves = moves.clone();
        }

        private String ts(){
            String string = "";
            for (int x = 0; x < 15; x++){
                if(this.moves[x] != null) {
                    string += this.moves[x].ts();
                }
            }
            return string;
        }

        //ADDS A CHILD
        private void addChild(Node node){
            for (int x = 0; x < 15; x++) {
                if (this.children[x] == null) {
                    this.children[x] = node;
                    x = 16;
                }
            }
            total++;
        }

    }

    private int getNumberOfMOves(Node node){
        int count = 0;
        for (int x = 0; x < 15; x++) {
            if (node.moves[x] != null)
                count++;
        }
        return count;
    }

    private int getNumberOfChildren(Node node){
        int count = 0;
        for (int x = 0; x < 15; x++) {
            if (node.children[x] != null) {
                count++;
            }
        }
        return count;
    }

    //checks to see if a line is present within a line[]
    private boolean contains(line[] data, line line){
        for (int x = 0; x < 15; x++) {
            if (data[x] != null) {
                if (   ((data[x].getV1() == line.getV1())
                    || (data[x].getV1() == line.getV2()))
                    && ((data[x].getV2() == line.getV1())
                    || (data[x].getV2() == line.getV2()))) {
                    return true;
                }
            }
        }
        return false;
    }
}