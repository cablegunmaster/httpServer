package com.jasper.unittest.mancala;

import com.jasper.mancala.Board;
import com.jasper.mancala.Mancala;
import org.junit.Assert;
import org.junit.Test;

public class MancalaTest {

    private Mancala mancala;

    @Test
    public void InitBoardConditionTurn() {
        mancala = new Mancala();
        Assert.assertEquals(mancala.getPlayerTurn(), 1); //p1 always begins.
    }

    @Test
    public void InitBoardConditionStonesDefault() {
        mancala = new Mancala();
        Assert.assertEquals(mancala.getScorePlayer1(), 0);
        Assert.assertEquals(mancala.getScorePlayer2(), 0);
    }

    @Test
    public void player1CheckScore() {
        mancala = new Mancala();
        mancala.setScorePlayer1(200);
        mancala.setScorePlayer2(100);
        Assert.assertTrue("Player 1 wins ", mancala.getScorePlayer1() > mancala.getScorePlayer2());
    }

    @Test
    public void player2CheckScore() {
        mancala = new Mancala();
        mancala.setScorePlayer1(100);
        mancala.setScorePlayer2(200);
        Assert.assertTrue("Player 2 wins ", mancala.getScorePlayer2() > mancala.getScorePlayer1());
    }

    @Test
    public void CheckWinConditionPlayer2Wins() {
        mancala = new Mancala();

        Board b = mancala.getBoard();
        b.addStoneOnField(1, -6);
        b.addStoneOnField(2, -6);
        b.addStoneOnField(3, -6);
        b.addStoneOnField(4, -6);
        b.addStoneOnField(5, -6);
        b.addStoneOnField(6, -6);

        mancala.isGameFinished();

        Assert.assertEquals("Player 2 wins", 2, mancala.getPlayerWinning());
    }

    @Test
    public void CheckWinConditionPlayer1Wins() {
        mancala = new Mancala();

        Board b = mancala.getBoard();
        b.addStoneOnField(8, -6);
        b.addStoneOnField(9, -6);
        b.addStoneOnField(10, -6);
        b.addStoneOnField(11, -6);
        b.addStoneOnField(12, -6);
        b.addStoneOnField(13, -6);

        mancala.isGameFinished(); //set all win condition

        Assert.assertEquals("Player 1 wins", 1, mancala.getPlayerWinning());
    }

    @Test
    public void reflectBoard(){
        Mancala m = new Mancala();

        // P1 M0 - 1 - 2 - 3 - 4 - 5 - 6 -
        // P2 M7 - 8 - 9 - 10- 11- 12 -13
        Assert.assertEquals(7, m.getOppositeBoardField(0));
        Assert.assertEquals(0, m.getOppositeBoardField(7));
        Assert.assertEquals(1,m.getOppositeBoardField(8));
        Assert.assertEquals(2,m.getOppositeBoardField(9));
        Assert.assertEquals(3,m.getOppositeBoardField(10));
        Assert.assertEquals(4,m.getOppositeBoardField(11));
        Assert.assertEquals(5,m.getOppositeBoardField(12));
        Assert.assertEquals(6,m.getOppositeBoardField(13));
    }
}
