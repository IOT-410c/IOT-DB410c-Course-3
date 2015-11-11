package net.calit2.mooc.iot_db410c.ledblock.gameprocessor;

/**
 * Created by Ara on 8/9/15.
 */
public interface Inputtable {

    public void startGame();
    public void endGame(int score);
    public void setInputState(boolean state);
    public boolean getInputState();
    public void setRunningState(boolean running);
    public boolean getRunningState();
}
