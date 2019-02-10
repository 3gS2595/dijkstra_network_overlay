package cs455.overlay.node;

import java.util.*;


public class HWONE{
    private static int numVertices = 6;
    int cnt = 0;
    int cnt2 = 0;


    public static void main(String[] args) {
        if (args.length == 1)
            numVertices = Integer.parseInt(args[0]);
        else {
            numVertices = 6;
        }


        //creates a root node with one move made
        List<line> data = new ArrayList<>();
        data.add(new line(1,2, 2));
        Node root = new Node(data);
        HWONE game = new HWONE();
        game.play(root, 1);
        System.out.println("player A: " + game.cnt);
        System.out.println("player B: " + game.cnt2);



    }
    private void play(Node root, int turn){
        int player = turn;
        //base case is in move method
        //System.out.println("play: children: " + root.getNum());
        //creates the children
        if(root.children.size() == 0){
            move(root, player);
        }
        if(player == 1)
            player = 2;
        else if (player == 2)
            player = 1;
        //creates the grandchildren
        if(root.children.size() != 0){
            for (int child = 1; child < root.children.size(); child++){
                if(notOver(root))
                    play(root.children.get(child), player);
            }
        }
    }

    private void move(Node node, int player){
        List<line> data = new ArrayList<>(node.getData());
        //BASE CASE FOR RECURSION
        if(node.getNum() == 14) {
            System.out.println(node.getData().toString());
        } else {
            //cycles through all possible lines
            for (int v1 = 0; v1 < numVertices; v1++) {
                for (int v2 = 0; v2 < numVertices; v2++) {
                    //If the move is available
                    if ((v1 != v2)
                        && !(data.contains(new line(v1, v2, 1)) || data.contains(new line(v1, v2, 2)))
                        && !(data.contains(new line(v2, v1, 1)) || data.contains(new line(v2, v1, 2)))) {
                        data.add(new line(v1, v2, player));

                        List<line> nn = new ArrayList<>(node.getData());
                        nn.add(new line(v1, v2, player));
                        node.addChild(new Node(nn));
                    }
                }
            }
        }
    }

    private boolean notOver(Node node){
        for (int vect1 = 0; vect1 < node.data.size(); vect1++) {
            for (int vect2 = 0; vect2 < node.data.size(); vect2++) {
                //if not overlapping with other loop
                if ((vect1 != vect2)){
                    //if the vectors connect
                    if((node.data.get(vect1).connects(node.data.get(vect2)))) {
                        //if two connecting line are found it looks for a third
                        for (int vect3 = 0; vect3 < node.data.size(); vect3++) {
                            //if not overlapping with other loops
                            if ((vect3 != vect2) && (vect3 != vect1)){
                                //if the third vector connects the loop
                                if(node.data.get(vect3).connects(node.data.get(vect1),(node.data.get(vect2)))){

                                    if(node.data.get(vect1).getColor() == 1) {
                                        cnt++;
                                    }
                                    if(node.data.get(vect1).getColor() == 2) {
                                        cnt2++;
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

    private static class Node{
        List<line> data;
        List<Node> children = new ArrayList<>();

        //CONSTRUCTOR
        Node(List<line> data){
            this.data = data;
        }
        Node(){};

        void isNow(Node node){
            this.data = new ArrayList<>(node.data);
            this.children = new ArrayList<>(node.children);
        }

        //ADDS A CHILD
        void addChild(Node node){
            this.children.add(node);
        }

        //GET CHILDREN
        List<line> getData(){
            return this.data;
        }

        //GET CHILDREN
        int getNum(){
            return this.children.size();
        }


    }

    private static class line{
        private int v1; //first vertex
        private int v2; //second vertex
        private int color; //0(null), 1(blue), or 2(red)

        public line(Integer v1, Integer v2, Integer color) {
            this.v1 = v1;
            this.v2 = v2;
            this.color = color;
        }

        //GETTERS
        public int getV1() { return v1; }
        public int getV2() { return v2; }
        public int getColor() { return color; }

        public boolean connects(line line){
            if  ((  this.v1 == line.v2) || (this.v1 == line.v1)
                || (this.v2 == line.v1) || (this.v2 == line.v2)
                && (this.getColor() == line.getColor())) {
                return true;
            }
            return false;
        }

        public boolean connects(line line1, line line2){
            if  (((this.v1 == line1.v1) || (this.v1 == line1.v2)
                 && (this.v2 == line2.v1) || (this.v2 == line2.v1))
                ^
                 ((this.v2 == line1.v1) || (this.v2 == line1.v2)
                 && (this.v1 == line2.v1) || (this.v1 == line2.v1))
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

        //COMPARE
        public boolean equals(Object o){
            if(o instanceof line){
                if ((((line) o).getColor() == this.getColor())
                    && (((line) o).getV1() == this.getV1())
                    && (((line) o).getV2() == this.getV2()))
                    return true;
            }
            return false;
        }
    }
}