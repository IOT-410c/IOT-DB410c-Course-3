package net.calit2.mooc.iot_db410c.ledblock;

import android.app.Activity;

import net.calit2.mooc.iot_db410c.ledblock.gameprocessor.Inputtable;

/**
 * Created by Ara on 8/9/15.
 */
public class CalliBirdInterface implements Inputtable{

    private Activity activity;
    private boolean input;
    private boolean running;

    public CalliBirdInterface(Activity activity){
        this.activity = activity;
    }

    @Override
    public void startGame() {}

    @Override
    public void endGame(int score) {}

    @Override
    public void setInputState(boolean state) {
        input = state;
    }

    @Override
    public boolean getInputState() {
        return input;
    }

    @Override
    public void setRunningState(boolean running) {
        this.running=running;
    }

    @Override
    public boolean getRunningState() {
        return running;
    }


}
