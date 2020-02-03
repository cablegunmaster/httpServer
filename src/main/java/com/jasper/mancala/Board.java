package com.jasper.mancala;

import javax.annotation.Nonnull;


public class Board {

    private int boardLength = 6;
    private int stonesPerPit = 6;
    private int[] playingField = new int[6];

    public Board() {
        initBoard(6, 6);
    }

    public Board(@Nonnull Integer boardLength, int stonesPerPit) {
        initBoard(boardLength, stonesPerPit);
    }

    public Board(int stonesPerPit) {
        initBoard(boardLength, stonesPerPit);
    }

    private void initBoard(Integer boardLength, int stonesPerPit) {
        if (boardLength <= 0) {
            throw new IllegalArgumentException("Size of the Mancala board should be bigger as 0");
        }

        if (stonesPerPit <= 0) {
            throw new IllegalArgumentException("Stones per mancala Pit cannot be empty, needs to be higher value as 0");
        }

        setBoardLength(boardLength);
        setStonesPerPit(stonesPerPit);
    }

    public void setBoardLength(int boardLength) {
        setPlayingField(boardLength);
        this.boardLength = boardLength;
    }

    public void fillBoard() {
        for (int field = 0; field < playingField.length; field++) {
            //skip after (1 length +1) //standard modulo 7.
            if (isMancalaStore(field)) {
                playingField[field] = 0;
            } else {
                playingField[field] = stonesPerPit;
            }
        }
    }

    public void setStonesPerPit(int stonesPerPit) {
        this.stonesPerPit = stonesPerPit;

        if (getPlayingField() != null) {
            fillBoard();
        }
    }

    /**
     * Checks if length is empty
     *
     * @return if 6 consequtive of array are empty.
     */
    public boolean isOneSideEmpty() {
        int j = 0;

        for (int field = 0; field < getPlayingField().length; field++) {

            if (isMancalaStore(field)) {
                continue;
            }

            if (getPlayingField()[field] == 0) {
                j++;
            } else {
                j = 0;
            }

            if (j == boardLength) {
                return true;
            }
        }

        return false;
    }

    public int getPlayerWinning() {
        int j = 0;
        for (int field = 0; field < getPlayingField().length; field++) {

            if (isMancalaStore(field)) {
                continue;
            }

            if (getPlayingField()[field] == 0) {
                j++;
            } else {
                j = 0;
            }

            if (j == boardLength) {
                if (isPlayerOneField(field)) {
                    return Mancala.PLAYER_TWO;
                } else {
                    return Mancala.PLAYER_ONE;
                }
            }
        }
        return 0;
    }

    /**
     * @param field  move 1 - 6 && 8-13 are valid fields
     * @param player 1 or 2 is expected.
     * @return if field is a valid field in the array for the player side .
     */
    public boolean isValidFieldForPlayer(int field, int player) {
        if (player == 1) {
            return isPlayerOneField(field);
        } else {
            //13 2x6+1
            return isPlayerTwoField(field);
        }
    }

    public boolean isPlayerOneField(int field) {
        return field <= boardLength && field > 0;
    }

    public boolean isPlayerTwoField(int field) {
        return field > boardLength && field <= (2 * boardLength + 1);
    }

    public boolean fieldHasStones(int field) {
        return getPlayingField()[field] > 0;
    }

    public void addStoneOnField(int field, int amount) {
        getPlayingField()[field] = getPlayingField()[field] + amount;
    }

    public boolean isMancalaStore(int currentField) {
        return currentField % (boardLength + 1) == 0;
    }

    private void setPlayingField(int boardLength) {
        playingField = new int[boardLength * 2 + 2];
    }

    public boolean isMancalaStoreForPlayerOne(int currentField) {
        return currentField == 0;
    }

    public int getMancalaStorePlayerOne(){
        return 0;
    }

    public int getMancalaStorePlayerTwo(){
        return (boardLength + 1);
    }

    public boolean isMancalaStoreForPlayerTwo(int currentField) {
        return (boardLength + 1) == currentField;
    }

    public int[] getPlayingField() {
        return playingField;
    }

    public int getBoardLength() {
        return boardLength;
    }

    /**
     * @param field which Players grabs from.
     * @return stones from pit
     */
    public int getFromPit(int field){
        int amount = playingField[field];
        playingField[field] = 0;
        return amount;
    }

    public int getStoneAmountFromField(int field){
        return playingField[field];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < playingField.length; i++) {
            if (isMancalaStore(i)) {
                sb.append("<br/>");
                sb.append("Pit").append(i);
            }else{
                sb.append(" - Mov").append(i);
            }
            sb.append("(S").append(getPlayingField()[i]).append(")");
        }
        return sb.toString();
    }
}
