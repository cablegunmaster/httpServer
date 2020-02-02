package com.jasper.mancala;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mancala {

    private final static Logger LOG = LoggerFactory.getLogger(Mancala.class);

    final static int PLAYER_ONE = 1;
    final static int PLAYER_TWO = 2;

    private int scorePlayer1 = 0;
    private int scorePlayer2 = 0;

    private int playerTurn = 1; //1 always start.
    private int playerWinning;

    private Board board;

    private Player player1;
    private Player player2;

    //When game starts get default values.
    public Mancala() {
        reset();
        setBoard(new Board(6));
    }

    public int addPlayer(Player player) {
        if (getPlayer1() == null) {
            setPlayer1(player);
            return getPlayer1().playerNumber;
        } else if (getPlayer2() == null) {
            setPlayer2(player);
            startGame(getPlayer1(), getPlayer2());
            return getPlayer2().playerNumber;
        }
        throw new IllegalArgumentException("Game is already running");
    }

    public void startGame(Player playerOne, Player playerTwo) {
        playerOne.setPlayerNumber(1);
        playerTwo.setPlayerNumber(2);
        player1 = playerOne;
        player2 = playerTwo;
    }

    public void reset() {
        scorePlayer1 = 0;
        scorePlayer2 = 0;
    }

    public void setMove(int move, int playerNumber) {
        Board board = getBoard();

        if (checkMoveIsValid(move, playerNumber) && !isGameFinished()) {
            int amount = board.getFromPit(move);
            int field = goOneFieldBack(move);

            for (int stones = amount; stones > 0; stones--) {

                if (canAddStoneOnCurrentField(field, playerNumber)) {
                    board.addStoneOnField(field, 1);

                    if (stones == 1 && !isFieldOnMancalaStore(field, playerNumber)) {
                        if (board.getStoneAmountFromField(field) == 1) {
                            addCapturedStonesToMancala(field);
                            board.addStoneOnField(field, -1);
                        }
                        switchTurn();
                    }

                } else {
                    //ifOnOtherPlayerMancaladontdrop.
                    stones++;
                }


                field = goOneFieldBack(field);
            }

        } else if (!isMoveValidOnBoard(move, playerNumber)) {
            throw new IllegalArgumentException("Illegal move: " + move);
        } else if (playerTurn != playerNumber) {
            throw new IllegalArgumentException("Its not your turn its player" + playerNumber + " turn");
        } else if (isGameFinished()) {
            throw new IllegalArgumentException("Game is already finished");
        }
    }

    private void addCapturedStonesToMancala(int field) {
        int collectedStones = board.getStoneAmountFromField(getOppositeBoardField(field));
        if (playerTurn == 1) {
            board.addStoneOnField(board.getMancalaStorePlayerOne(), collectedStones + 1);
        } else {
            board.addStoneOnField(board.getMancalaStorePlayerTwo(), collectedStones + 1);
        }
    }

    public boolean checkMoveIsValid(int move, int playerNumber) {
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

    public int goOneFieldBack(int field) {
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

    public int getPlayerTurn() {
        return playerTurn;
    }

    public void switchTurn() {
        setPlayerTurn(PLAYER_ONE == getPlayerTurn() ? PLAYER_TWO : PLAYER_ONE);
    }

    public boolean isMoveValidOnBoard(int move, int player) {
        return !board.isMancalaStore(move)
                && board.isValidFieldForPlayer(move, player)
                && board.fieldHasStones(move);
    }

    public int getScorePlayer1() {
        return scorePlayer1;
    }

    public void setScorePlayer1(int scorePlayer1) {
        this.scorePlayer1 = scorePlayer1;
    }

    public int getScorePlayer2() {
        return scorePlayer2;
    }

    public void setScorePlayer2(int scorePlayer2) {
        this.scorePlayer2 = scorePlayer2;
    }

    public boolean isGameFinished() {
        Board board = getBoard();

        if (board.isOneSideEmpty()) {
            if (board.getPlayerWinning() == PLAYER_ONE && getScorePlayer1() >= getScorePlayer2()) {
                setPlayerWinning(PLAYER_ONE);
            } else {
                setPlayerWinning(PLAYER_TWO);
            }
            return true;
        }
        return false;
    }

    public int getPlayerWinning() {
        return playerWinning;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setPlayerWinning(int playerWinning) {
        this.playerWinning = playerWinning;
    }

    public void setPlayerTurn(int playerTurn) {
        this.playerTurn = playerTurn;
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public String getName(){
        return "Mancala";
    }
}
