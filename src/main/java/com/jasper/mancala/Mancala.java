package com.jasper.mancala;

public class Mancala {

    private final int player1 = 1;
    private final int player2 = 2;
    private int turn = 1; //1 always start.
    private int move;
    private int stonesPlayer1= 0;
    private int stonesPlayer2= 0;
    private int stonesPerPit = 6;

    //When game starts get default values.
    public Mancala() {
        reset();
    }

    public void reset() {
        stonesPlayer1 = 0;
        stonesPlayer2 = 0;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn() {
        this.turn = player1 == getTurn() ? player2 : player1;
    }

    public int getMove() {
        return move;
    }

    /**
     * 0 - 1 - 2 - 3 -  4 -  5
     * 6 - 7 - 8 - 9 - 10 - 11
     * @param move integer move a player made.
     */
    //move can only be 0-11
    public void setMove(int move) {
        if(move > 0 && move < 12) {
            this.move = move;
        }else {
            throw new IllegalArgumentException("Move is not supported");
        }
    }

    public int getStonesPlayer1() {
        return stonesPlayer1;
    }

    public void setStonesPlayer1(int stonesPlayer1) {
        this.stonesPlayer1 = stonesPlayer1;
    }

    public int getStonesPlayer2() {
        return stonesPlayer2;
    }

    public void setStonesPlayer2(int stonesPlayer2) {
        this.stonesPlayer2 = stonesPlayer2;
    }
}
