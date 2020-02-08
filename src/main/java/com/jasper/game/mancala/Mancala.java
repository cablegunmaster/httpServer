package com.jasper.game.mancala;

import com.jasper.game.IGame;
import com.jasper.game.Player;
import com.jasper.model.Request;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public class Mancala implements IGame {

    public final static int PLAYER_ONE = 1;
    public final static int PLAYER_TWO = 2;

    private int playerTurn = 1; //1 always start.
    private int playerWinning  = 0;

    private Board board;
    private Player player1;
    private Player player2;

    private boolean gameStarted = false;

    public Mancala() {
        setBoard(new Board(6));
    }

    public String getName() {
        return Object.class.getName();
    }

    @Override
    public void setMove(int move, Player player) {
        Board board = getBoard();

        if (checkPlayerMoveIsValid(move, player.getPlayerNumber()) && !isGameFinished()) {
            int stonesInHand = board.grabStonesFromPit(move);
            int field = getCounterClockWiseField(move);

            for (int stonesLeftInHand = stonesInHand; stonesLeftInHand > 0; stonesLeftInHand--) {
                stonesLeftInHand = dropStoneOnBoard(field, stonesLeftInHand);
                field = getCounterClockWiseField(field);
            }
        }
    }

    public int dropStoneOnBoard(int field, int stonesLeftInHand) {
        if (canAddStoneOnCurrentField(field, playerTurn) &&
                stonesLeftInHand == 1) {

            board.addStoneOnField(field, 1);

            if (board.getStoneAmountFromField(field) == 1) {
                addCapturedStonesToMancala(field);
                board.addStoneOnField(field, -1);
            }

            switchTurn();
        } else {
            stonesLeftInHand++;
        }
        return stonesLeftInHand;
    }

    private void addCapturedStonesToMancala(int field) {
        int collectedStones = board.getStoneAmountFromField(getOppositeBoardField(field));
        int storeLocationP1 = board.getMancalaStorePlayerOne();
        int storeLocationP2 = board.getMancalaStorePlayerTwo();

        if (playerTurn == 1) {
            board.addStoneOnField(storeLocationP1, collectedStones + 1);
        } else {
            board.addStoneOnField(storeLocationP2, collectedStones + 1);
        }
    }

    public boolean checkPlayerMoveIsValid(int move, int playerNumber) {
        return isMoveValidOnBoard(move, playerNumber) &&
                playerTurn == playerNumber;
    }

    public boolean canAddStoneOnCurrentField(int field, int playerNumber) {
        return (isFieldOnMancalaStore(field, playerNumber) ||
                (board.isPlayerOneField(field) && !board.isMancalaStoreForPlayerOne(field)) ||
                board.isPlayerTwoField(field) && !board.isMancalaStoreForPlayerTwo(field));
    }

    public boolean isFieldOnMancalaStore(int field, int playerNumber) {
        return (playerNumber == PLAYER_ONE && board.isMancalaStoreForPlayerOne(field) ||
                playerNumber == PLAYER_TWO && board.isMancalaStoreForPlayerTwo(field));
    }

    public int getCounterClockWiseField(int field) {
        if (field == 0) {
            return board.getBoardLength() * 2 + 1;
        }
        return field - 1;
    }

    /**
     * @param field currentField
     * @return field but X amount of steps back.
     */
    public int getOppositeBoardField(int field) {
        Board b = getBoard();
        int boardLength = (b.getBoardLength() + 1);
        int totalLength = (b.getBoardLength() * 2 + 1);

        if (field >= 0 && field <= 6) {
            return field + boardLength;
        } else if (field >= boardLength && field <= totalLength) {
            return field - boardLength;
        } else {
            return -1;
        }
    }


    public void switchTurn() {
        setPlayerTurn(PLAYER_ONE == getPlayerTurn() ? PLAYER_TWO : PLAYER_ONE);
    }

    public boolean isMoveValidOnBoard(int move, int player) {
        return !board.isMancalaStore(move)
                && board.isValidFieldForPlayer(move, player)
                && board.fieldHasStones(move);
    }

    public boolean isGameFinished() {
        playerWinning = getBoard().getPlayerWinning();
        return getBoard().getPlayerWinning() > 0;
    }


    @CheckForNull
    public Player getPlayerByRequest(Request client) {
        if (getPlayer1() != null &&
                getPlayer1().getClient().equals(client)) {
            return getPlayer1();
        } else {
            if (getPlayer2() != null &&
                    getPlayer2().getClient().equals(client)) {
                return getPlayer2();
            }
        }
        return null;
    }

    @Override
    public boolean isGameFull() {
        return getPlayer1() != null && getPlayer2() != null;
    }

    @Override
    public boolean isStarted() {
        return !isGameFinished() && isGameStarted();
    }

    @Override
    public String getHelpMessage() {
        return "command list overview: <br/>" +
                "!list - shows all games <br/>" +
                "!create - create new game <br/>" +
                "!join 'number' - join a game <br/>" +
                "!move 'number' - make a move in game<br/>" +
                "!show - return list of valid moves" +
                "!turn - return which turn it is";
    }

    @Override
    public void addPlayer(Player player) {
        if (getPlayer1() == null) {
            setPlayer1(player);
        } else if (getPlayer2() == null) {
            setPlayer2(player);
        }
    }

    @Override
    public boolean contains(Player player) {
        return player.equals(player1) || player.equals(player2);
    }

    @Nonnull
    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setPlayerTurn(int playerTurn) {
        this.playerTurn = playerTurn;
    }

    @Override
    public void startGame() {
        gameStarted = true;
    }

    public int getPlayerTurn() {
        return playerTurn;
    }

    @Override
    public String getBoardContent() {
        return board.toString();
    }

    @CheckForNull
    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    @CheckForNull
    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public int getPlayerWinning() {
        return playerWinning;
    }

    public void setPlayerWinning(int playerWinning) {
        this.playerWinning = playerWinning;
    }
}
