package com.jasper.unittest.mancala;

import com.jasper.game.Player;
import com.jasper.game.mancala.Board;
import com.jasper.game.mancala.Mancala;
import org.junit.Assert;
import org.junit.Test;

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

    Player pl = new Player(null,1);
    Player p2 = new Player(null,2);

    @Test
    public void boardSizeTest() {
        Mancala mancala = new Mancala();
        Board board = mancala.getBoard();
        Assert.assertEquals(6, board.getBoardLength());
    }

    /**
     * Can only move the 6 places on P1 side.
     * P1 till P6
     */
    @Test
    public void moveValidOnStandardBoardPlayer1() {
        //Default 14 length board Mancala store on 0 and 7.
        Mancala mancala = new Mancala();
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
    public void moveValidOnStandardBoardPlayer2() {
        Mancala mancala = new Mancala();
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
    public void checkFieldSize() {
        Board b = new Board(6, 6);
        Assert.assertEquals(14, b.getPlayingField().length);
    }

    @Test
    public void checkBoardIsFilled() {
        Board b = new Board(6, 6);
        b.fillBoard();

        for (int i = 0; i < b.getPlayingField().length; i++) {
            if (i != 0 && i != 7) {
                Assert.assertEquals(6, b.getPlayingField()[i]);
            }
        }

        System.out.println(b.toString());

        //Mancala store are on these fields.
        Assert.assertEquals(0, b.getPlayingField()[0]);
        Assert.assertEquals(0, b.getPlayingField()[7]);
    }

    @Test
    public void checkOneSideEmptyIsFalse() {
        Board b = new Board();
        b.fillBoard();
        Assert.assertFalse(b.isOneSideEmpty());
    }

    @Test
    public void checkOneSideEmptyIsTrue() {
        Board b = new Board();
        b.setBoardLength(6);
        Assert.assertTrue(b.isOneSideEmpty());
    }

    @Test
    public void checkP1SideIsEmpty() {
        Board b = new Board();
        b.setBoardLength(6);

        b.addStoneOnField(3, 1);

        Assert.assertTrue(b.isOneSideEmpty());
    }

    @Test
    public void checkP2SideIsEmpty() {
        Board b = new Board();
        b.setBoardLength(6);

        b.addStoneOnField(9, 1);

        Assert.assertTrue(b.isOneSideEmpty());
    }

    @Test
    public void checkOneSideIsEmptyWhenBothSidesAreStillFilled() {
        Board b = new Board();
        b.setBoardLength(6);

        b.addStoneOnField(3, 1);
        b.addStoneOnField(9, 1);

        Assert.assertFalse(b.isOneSideEmpty());
    }

    @Test
    public void emptyCheckSkipsMancalaStoreWhenFieldHasStonesFilledIn() {
        Board b = new Board();
        b.setBoardLength(6);

        b.addStoneOnField(0, 1);
        b.addStoneOnField(7, 1);

        Assert.assertTrue(b.isOneSideEmpty());
    }

    @Test
    public void testMoveIsCorrectlyDropped() {
        Mancala m = new Mancala();



        System.out.println(m.getBoard().toString());
        System.out.println("turn player" + m.getPlayerTurn());

        m.setMove(6, pl);
        System.out.println(m.getBoard().toString());
        System.out.println("turn player" + m.getPlayerTurn());

        m.setMove(5, pl);
        System.out.println(m.getBoard().toString());
        System.out.println("turn player" + m.getPlayerTurn());

        m.setMove(8, p2);
        System.out.println(m.getBoard().toString());
        System.out.println("turn player" + m.getPlayerTurn());

        m.setMove(1, pl);
        System.out.println(m.getBoard().toString());
        System.out.println("turn player" + m.getPlayerTurn());
    }

    @Test
    public void testMoveIsDroppedOnMancalaStoreToPlayer1() {
        Mancala m = new Mancala();
        Assert.assertEquals(1, m.getPlayerTurn());

        m.setMove(6, p2);
        Assert.assertEquals(1, m.getPlayerTurn());
    }

    @Test
    public void testReverseClockMove() {
        Mancala m = new Mancala();

        Assert.assertEquals(12, m.goOneFieldBack(13));
        Assert.assertEquals(13, m.goOneFieldBack(0));
    }

    @Test
    public void isLastMoveOnMancalaStoreTest() {
        Mancala m = new Mancala();

        Assert.assertTrue(m.isFieldOnMancalaStore(0, 1));
        Assert.assertFalse(m.isFieldOnMancalaStore(0, 2));
        Assert.assertFalse(m.isFieldOnMancalaStore(7, 1));
        Assert.assertTrue(m.isFieldOnMancalaStore(7, 2));
    }
}
