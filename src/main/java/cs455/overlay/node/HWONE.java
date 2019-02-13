package cs455.overlay.node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;


public class HWONE{
    //Decide games vertecy count
    private static int numVertices;

    //Activity records
    static int total = 0;
    static int skipped = 0;

    //(Flag) negates game rules
    int negated = 0;

    public static void main(String[] args) {

        //Sets numVertices t arg if arg given
        if (args.length == 1)
            numVertices = Integer.parseInt(args[0]);
        else numVertices = 5;

        //Starts game
        HWONE game = new HWONE();
        game.run();
    }

    //USER INTERFACE AND OPTION INPUT
    private void run() {
        byte desiredPlayer = 0;
        int negate = 0;

        //game control input
        Scanner scanner = new Scanner(System.in);
        System.out.println("ENTER YOUR DESIRED QUALITIES");
        System.out.println("Find winning strategy for player A, B, or neither(C): ");
        String answer = String.valueOf(scanner.next().charAt(0));
        System.out.println("Negate winning condition? Y(yes) or N(no): ");
        String firstPlay = String.valueOf(scanner.next().charAt(0));
        System.out.println();

        //creates desired search
        if(answer.toUpperCase().charAt(0) == ('A')){
            desiredPlayer = 1;
        }
        if(answer.toUpperCase().charAt(0) == ('B')){
            desiredPlayer = 2;
        }
        if(answer.toUpperCase().charAt(0) == ('C')){
            desiredPlayer = 3;
        }

        //Logs if negattion has been chosen
        if (firstPlay.toUpperCase().charAt(0) == 'Y')
            negate = 1;
        if (firstPlay.toUpperCase().charAt(0) == 'N')
            negate = 0;

        if(negate == 1)
            System.out.println("Searching for " + answer.toUpperCase() +"'s \"negated\" winning strategy");
        else System.out.println("Searching for " + answer.toUpperCase() +"'s winning strategy");

        line[] data = new line[15];
        data[0] = new line((byte)0,(byte)1, (byte)1);
        Node root = new Node(data);
        BreadthFirstSearch bfs = new BreadthFirstSearch(root, desiredPlayer, negate);
        bfs.computeBFS();


    }

    //BFS MECHANISM AND TREE CREATOR
    public class BreadthFirstSearch {
        Node startNode;
        Node goalNode;
        byte wantWinner;
        byte wantloser;

        public BreadthFirstSearch(Node start, byte player, int negate){
            this.startNode = start;
            this.goalNode = null;
            this.wantWinner = player;
            this.wantloser = changePlayer(wantWinner);
            negated = negate;
        }

        public boolean computeBFS(){
            Queue<Node> queue = new LinkedList<>();
            queue.add(this.startNode);
            while(!queue.isEmpty()){

                //estimates nodes created
                if(total % 10000 == 0)
                    System.out.println("    I've looked at around " + total + " options so far, and skipped:" + skipped);
                total++;

                Node current = queue.remove();
                if((notOver(current) == wantWinner) ^ (notOver(current) == wantloser && negated == 1)) {
                    char res = ' ';
                    if(wantWinner == 1)
                        res = 'A';
                    if(wantWinner == 2)
                        res = 'B';

                    System.out.println("This was a winning strategy for player " + res);
                    System.out.println(current.ts());
                    System.out.println();
                    System.exit(1);
                } else {
                    ArrayList<Node> move = current.getChildren(wantWinner);
                    int skip = 0;
                    for(int i = 0; i < move.size(); i++){

                        //for C (neither)
                        if(wantWinner == 3) {
                            if (notOver(move.get(i)) != 0) {
                                skip = 1;
                            }
                        }
                        //for A or B
                        else if ((notOver(move.get(i)) == wantloser) && (negated == 0)) {
                            skip = 1;
                        }
                        //for A or B (negated)
                        else if ((notOver(move.get(i)) == wantWinner) && (negated == 1)) {
                            skip = 1;
                        }
                        //skipped node data intake
                        if (skip == 1){
                            skipped += move.size() - i;
                        }
                    }
                    if(skip == 0) {
                        queue.addAll(move);
                    }
                }
            }
            System.out.println("There was not a single instance of what you asked for occuring");
            return false;
        }

    }

    //GAMEBOARD OBJECT
    private class Node{
        line[] moves;

        //CONSTRUCTOR
        private Node(line[] moves){
            this.moves = moves.clone();
        }

        //CREATES ALL THE NODE KIDDIES! :D
        public ArrayList<Node> getChildren(byte wantWinner){

            //Returned variable
            ArrayList<Node> childNodes = new ArrayList<>();

            //logs moves tested and test board used to test moves
            line[] tested = this.moves.clone();
            line[] testBoard = this.moves.clone();

            //Last moves player
            byte lastPlayer = tested[NumMOves(this)-1].getColor();
            byte currentPlayer = changePlayer(lastPlayer);

            //Player we want to Win
            byte wantWin = wantWinner;

            //(case = negated) this will cause the current player to obstruct themselves
            if(negated == 1) {
                lastPlayer = currentPlayer;
                wantWin = currentPlayer;
            }

            //(Case = neither) both players obstruct each other
            //This is implemented by using the obstruction loop naturally used by B
            if(wantWin == 3)
                wantWin = lastPlayer;

            //Flags if obstruction was needed
            int obstructed = 0;

            //Obstructs desired winner
            if((lastPlayer == wantWin) && NumMOves(this) != (((numVertices-1)*numVertices)/2)-1) {
                for (byte v1 = 0; v1 < numVertices; v1++) {
                    for (byte v2 = 0; v2 < numVertices; v2++) {
                        if ((v1 != v2) && (!contains(tested, (new line(v1, v2, lastPlayer))))) {

                            //Constructs test move
                            testBoard[NumMOves(this)] = new line(v1, v2, lastPlayer);
                            Node temp = new Node(testBoard);

                            //If test move lets the previous player to win, it obstructs the winning move
                            if (notOver(temp) == lastPlayer) {
                                //adds move to history before playing
                                for (int x = 0; x < 15; x++) {
                                    if (tested[x] == null) {
                                        tested[x] = new line(v1, v2, currentPlayer);
                                        x = 100;
                                    }
                                }

                                //(case negated) place obstruction in tested[]
                                //this makes sure it is not played
                                if(negated == 1){
                                    skipped++;
                                } else {
                                    //Adds obstructive next board
                                    testBoard[NumMOves(this)] = new line(v1, v2, currentPlayer);
                                    temp = new Node(testBoard);
                                    childNodes.add(temp);
                                    obstructed = 1;
                                }
                            }
                        }
                    }
                }
            }
            //If no obstructive move made
            //Adds all possible moves
            if(obstructed == 0) {
                //cycles possible moves
                for (byte v1 = 0; v1 < numVertices; v1++) {
                    for (byte v2 = 0; v2 < numVertices; v2++) {

                        //If move is available
                        if ((v1 != v2) && (!contains(tested, (new line(v1, v2, (byte)1))))) {

                            //adds move to test history
                            for (int x = 0; x < 15; x++) {
                                if (tested[x] == null) {
                                    tested[x] = new line(v1, v2, currentPlayer);
                                    x = 100;
                                }
                            }

                            //Adds possible next board to list
                            testBoard = this.moves.clone();
                            testBoard[NumMOves(this)] = new line(v1, v2, currentPlayer);
                            childNodes.add(new Node(testBoard));
                        }
                    }
                }
            }
            return childNodes;
        }

        //TO STRING METHOD
        private String ts(){
            String string = "";
            for (int x = 0; x < 15; x++){
                if(this.moves[x] != null) {
                    string += this.moves[x].ts();
                }
            }
            return string;
        }
    }

    //SINGLE MOVE OBJECT
    private class line{
        private byte v1; //first vertex
        private byte v2; //second vertex
        private byte color = (byte)0; //0(null), 1(blue), or 2(red)

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

    //CHECKS FOR ENDGAME
    private byte notOver(Node node){
        //SEARCHING FOR 2 CONNECTED VECTORS
        for (int vect1 = 0; vect1 < NumMOves(node); vect1++) {
            for (int vect2 = 0; vect2 < NumMOves(node); vect2++) {
                if((node.moves[vect1].getColor() == node.moves[vect2].getColor())) {

                    //CONNECTION FOUND, NOW FINDING 3RD VECTOR
                    for (int vect3 = 0; vect3 < NumMOves(node); vect3++) {
                        if((vect1 != vect2 && vect2 != vect3 && vect3 != vect1)
                            && (node.moves[vect1].getColor() == node.moves[vect3].getColor())) {

                            //If this string is comprised of 3 pairs of three unique digits
                            //than we just damn done roped ourselves a triangle, YEEHAH COWBOY
                            String connects = Byte.toString(node.moves[vect1].v1) + Byte.toString(node.moves[vect1].v2)
                                + Byte.toString(node.moves[vect2].v1) + Byte.toString(node.moves[vect2].v2)
                                + Byte.toString(node.moves[vect3].v1) + Byte.toString(node.moves[vect3].v2);

                            //Identifies Winner and returns
                            if (connects.chars().distinct().count() == 3) {
                                if (node.moves[vect1].getColor() == 1)
                                    return 1;
                                if (node.moves[vect1].getColor() == 2)
                                    return 2;
                            }
                        }
                    }

                }
            }
        }
        return 0;
    }

    //GETS # OF MOVES PLAYED ON NODE'S BOARD
    private int NumMOves(Node node){
        int count = 0;
        for (int x = 0; x < 15; x++)
            if (node.moves[x] != null)
                count++;
        return count;
    }

    //RETURNS THE OPPOSING PLAYER
    private byte changePlayer(byte player){
        if(player == 1)
            return (byte)2;
        else return (byte)1;
    }

    //CHECKS TO SEE IF MOVE HAS ALREADY BEEN PLAYED
    private boolean contains(line[] data, line line){
        for (int x = 0; x < 15; x++)
            if (data[x] != null)
                if (  ((data[x].getV1() == line.getV1())
                    || (data[x].getV1() == line.getV2()))
                    &&((data[x].getV2() == line.getV1())
                    || (data[x].getV2() == line.getV2())))
                    return true;
        return false;
    }
}