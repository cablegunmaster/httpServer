package com.jasper.unittest.mancala;

import com.jasper.game.mancala.Board;
import com.jasper.game.mancala.Mancala;
import org.junit.Assert;
import org.junit.Test;

import static com.jasper.game.mancala.Mancala.PLAYER_TWO;

/**
 * boardLength  the distance from Player1 Mancala to Player 2 Mancala.
 * Ex. 1 equals a board size of
 * P1 - Pit - P2
 * Pit
 * <p>
 * Ex 2.
 * P1 - Pit - Pit - P2
 * Pit - Pit
 * stonesPerPit amount of stones per pit.
 * <p>
 * Board example for 14 fielded Mancala.
 * P1 M0 - 1 - 2 - 3 - 4 - 5 - 6 -
 * P2 M7 - 8 - 9 - 10- 11- 12 -13
 */
public class BoardTest {

    Mancala mancala = new Mancala();

    @Test
    public void setupRulesCheckBoardIsCorrectSize() {
        Board b = new Board(6, 6);
        Assert.assertEquals(14, b.getPlayingField().length);
    }

    @Test
    public void setupRulesCheckBoardIsFilledCorrectly() {
        Board b = new Board(6, 6);
        b.fillBoard();

        for (int i = 0; i < b.getPlayingField().length; i++) {
            if (i != 0 && i != 7) {
                Assert.assertEquals(6, b.getPlayingField()[i]);
            }
        }

        System.out.println(b.toString());

        //Check if Mancala store are on the expected fields.
        Assert.assertEquals(0, b.getPlayingField()[0]);
        Assert.assertEquals(0, b.getPlayingField()[7]);
    }

    /**
     * Can only move the 6 places on P1 side.
     * P1 till P6
     */
    @Test
    public void checkIsMoveValidOnStandardBoardForPlayer1() {
        //Default 14 length board Mancala store on 0 and 7.
        mancala = new Mancala();
        mancala.getBoard().fillBoard();

        Assert.assertFalse(mancala.isMoveValidOnBoard(-1, 1));
        Assert.assertFalse(mancala.isMoveValidOnBoard(0, 1)); //mancala store P1
        Assert.assertTrue(mancala.isMoveValidOnBoard(1, 1));
        Assert.assertFalse(mancala.isMoveValidOnBoard(7, 1)); //mancala store P2

        Assert.assertFalse(mancala.isMoveValidOnBoard(11, 1));
        Assert.assertFalse(mancala.isMoveValidOnBoard(12, 1));
        Assert.assertFalse(mancala.isMoveValidOnBoard(13, 1));
    }

    /**
     * Can only move the 6 place on the P2 side.
     * P8 till P13
     */
    @Test
    public void checkIsMoveValidOnStandardBoardForPlayer2() {
        mancala = new Mancala();
        mancala.getBoard().fillBoard();

        Assert.assertFalse(mancala.isMoveValidOnBoard(-1, 2));
        Assert.assertFalse(mancala.isMoveValidOnBoard(0, 2)); //mancala store P1
        Assert.assertFalse(mancala.isMoveValidOnBoard(1, 2));
        Assert.assertFalse(mancala.isMoveValidOnBoard(7, 2)); //mancala store P2

        Assert.assertTrue(mancala.isMoveValidOnBoard(11, 2));
        Assert.assertTrue(mancala.isMoveValidOnBoard(12, 2));
        Assert.assertTrue(mancala.isMoveValidOnBoard(13, 2));
    }

    @Test
    public void checkIsBoardEmpty() {
        Board b = new Board();
        b.setBoardLength(6);
        Assert.assertEquals(2, b.getPlayerWinning());
    }

    @Test
    public void checkIsBoardEmptyAndSkipMancalaStore() {
        Board b = new Board();
        b.setBoardLength(6);

        b.addStoneOnField(0, 1);
        b.addStoneOnField(7, 1);

        Assert.assertEquals(2, b.getPlayerWinning());
    }

    @Test
    public void checkIsBoardNotEmptyAfterFilling() {
        Board b = new Board();
        b.fillBoard();
        Assert.assertEquals(0, b.getPlayerWinning());
    }

    @Test
    public void checkBoardOnPlayerOneSideIsEmpty() {
        Board b = new Board();
        b.setBoardLength(6);

        b.addStoneOnField(3, 1);

        Assert.assertEquals(Mancala.PLAYER_ONE, b.getPlayerWinning());
    }

    @Test
    public void checkBoardOnPlayerTwoSideIsEmpty() {
        Board b = new Board();
        b.setBoardLength(6);

        b.addStoneOnField(9, 1);

        Assert.assertEquals(Mancala.PLAYER_TWO, b.getPlayerWinning());
    }

    @Test
    public void checkBoardIsNotEmptyWhenBothSidesAreStillFilledWithOneStone() {
        Board b = new Board();
        b.setBoardLength(6);

        b.addStoneOnField(3, 1);
        b.addStoneOnField(9, 1);

        Assert.assertEquals(0, b.getPlayerWinning());
    }

    @Test
    public void checkBoardGoesCounterClockwise() {
        Mancala m = new Mancala();
        Assert.assertEquals(12, m.getCounterClockWiseField(13));
    }

    @Test
    public void checkBoardGoesMinusOne() {
        Mancala m = new Mancala();
        Assert.assertEquals(13, m.getCounterClockWiseField(0));
    }
}
