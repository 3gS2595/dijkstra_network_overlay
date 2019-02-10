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
        byte player = turn;
        //base case is in move method
        //System.out.println("play: children: " + root.getNum());
        //creates the children
        if(root.getNum() == 0){
            move(root, player);
        }
        int checked = 0;
        if(root.getNum() != 0) {
            for (int child = 0; child < root.getNum(); child++) {
                if(!notOver(root.children[child])) {
                    checked++;
                    if (checked == root.getNum()) {
                        for (int winners = 0; winners < root.getNum(); winners++) {
                            System.out.println(root.children[child].ts());
                        }
                    }
                }
            }
        }

        //changes players after every move
        if(player == 1)
            player = 2;
        else if (player == 2)
            player = 1;

        //TODO TODO TODO
        //IF YOU GET A SINGLE FAILURE MOVE TO THE NEXT BRANCH
        //CONTINUE UNTIL ONE CBRANCH DOES NOT FIND FAILUE
        //send failure flag around with parent?

        if(total % 100000 == 0) {
            System.out.println(total);
            System.out.println(cnt);
            System.out.println(cnt2);
        }

        //creates the grandchildren
        if(root.getNum() != 0){
            for (int child = 0; child < root.getNum(); child++){
                    play(root.children[child], player);
            }
        }
    }

    private void move(Node node, byte player){
        line[] data = node.getData().clone();
        //BASE CASE FOR RECURSION
        // isolate to one scenario
        //if it finds a winner from player b quit
        boolean failure = false;
        for (int child = 0; child < node.getNum(); child++) {
            if (!notOver(node.children[child])){
                failure = true;
            }
        }

        //cycles through all possible lines
        if(!failure) {
            for (byte v1 = 0; v1 < numVertices; v1++) {
                for (byte v2 = 0; v2 < numVertices; v2++) {
                    //If the move is available
                    if ((v1 != v2)
                        && !((contains(data, (new line(v1, v2, (byte) 1)))) || (contains(data, (new line(v1, v2, (byte) 2)))))
                        && !((contains(data, (new line(v2, v1, (byte) 1)))) || (contains(data, (new line(v2, v1, (byte) 2)))))) {
                        for (int x = 0; x < 15; x++) {
                            if (data[x] == null) {
                                data[x] = new line(v1, v2, player);
                                x = 16;
                            }
                        }

                        line[] nn = node.getData().clone();
                        for (int x = 0; x < 15; x++) {
                            if (nn[x] == null) {
                                nn[x] = new line(v1, v2, player);
                                x = 16;
                            }

                        }
                        node.addChild(new Node(nn));
                    }
                }
            }
        }
    }

    private boolean contains(line[] data, line line){
        for (int x = 0; x < 15; x++) {
            if (data[x] != null) {
                if (data[x].equal(line)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean notOver(Node node){
        for (int vect1 = 0; vect1 < node.data.length; vect1++) {
            for (int vect2 = 0; vect2 < node.data.length; vect2++) {
                //if not overlapping with other loop
                if ((vect1 != vect2)
                    && (node.data[vect1] != null)
                    && (node.data[vect2] != null)
                    && (node.data[vect1].getColor() ==1 && node.data[vect2].getColor()==1)){

                    String connects = Byte.toString(node.data[vect1].v1) + Byte.toString(node.data[vect1].v2)
                        + Byte.toString(node.data[vect2].v1) + Byte.toString(node.data[vect2].v2);

                    if(connects.chars().distinct().count() == 3) {
                        //if two connecting line are found it looks for a third
                        for (int vect3 = 0; vect3 < node.data.length; vect3++) {
                            //if not overlapping with other loops
                            if ((vect3 != vect2)
                                && (vect3 != vect1)
                                && (node.data[vect3] != null)
                                && (node.data[vect1].getColor() == node.data[vect3].getColor())){

                                //if the third vector connects the loop
                                connects = connects.concat(Byte.toString(node.data[vect3].v1) + Byte.toString(node.data[vect3].v2));

                                if(connects.chars().distinct().count() == 3){
                                    //System.out.println(connects);

                                    if(node.data[vect1].getColor() == 1) {
                                        cnt++;
                                        System.out.println("1");
                                    }
                                    if(node.data[vect1].getColor() == 2) {
                                        cnt2++;
                                        System.out.println("2");
                                    }
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private class Node{
        line[] data = new line[15];
        Node[] children = new Node[15];

        //CONSTRUCTOR
        Node(line[] data){
            this.data = data;
        }
        Node(){};

        void isNow(Node node){
            this.data = node.data.clone();
            this.children = node.children.clone();
        }

        String ts(){
            String string = "";
            for (int x = 0; x < 15; x++){
                if(data[x] != null) {
                    string += this.data[x].ts();
                }
            }
            return string;
        }

        //ADDS A CHILD
        void addChild(Node node){
            for (int x = 0; x < 15; x++) {
                if (this.children[x] == null) {
                    this.children[x] = node;
                    x = 16;
                }
            }
            total++;
        }


        //GET CHILDREN
        line[] getData(){
            return this.data;
        }

        //GET CHILDREN
        int getNum(){
            int count = 0;
            for (int x = 0; x < 15; x++) {
                if (this.children[x] != null)
                    count++;
            }
            return count;
        }


    }

    private class line{
        private byte v1; //first vertex
        private byte v2; //second vertex
        private byte color; //0(null), 1(blue), or 2(red)

        public line(byte v1, byte v2, byte color) {
            this.v1 = v1;
            this.v2 = v2;
            this.color = color;
        }

        //GETTERS
        public byte getV1() { return v1; }
        public byte getV2() { return v2; }
        public byte getColor() { return color; }

        public boolean connects(line line){
            if (line instanceof line && this instanceof line) {
                if ((this.v1 == line.v2) || (this.v1 == line.v1)
                    || (this.v2 == line.v1) || (this.v2 == line.v2)
                    && (this.getColor() == line.getColor())) {
                    return true;
                }
            }
            return false;
        }

        public boolean connects(line line1, line line2){
            if  ((((this.v1 == line1.v1) || (this.v1 == line1.v2)
                && (this.v2 == line2.v1) || (this.v2 == line2.v1))
                ^
                ((this.v2 == line1.v1) || (this.v2 == line1.v2)
                    && (this.v1 == line2.v1) || (this.v1 == line2.v1)))
                && (this.getColor() == line1.getColor())
                && (this.getColor() == line2.getColor()))
            {
                return true;
            }
            return false;
        }

        //TOSTRING
        public String ts( ){
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
}