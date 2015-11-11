package net.calit2.mooc.iot_db410c.ledblock;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import net.calit2.mooc.iot_db410c.ledblock.gameprocessor.GameProcessor;
import net.calit2.mooc.iot_db410c.ledblock.gameprocessor.Inputtable;
import net.calit2.mooc.iot_db410c.ledblock.gameprocessor.Renderable;

public class LEDBlockActivity extends Activity {

    TextView textView;
    GameProcessor gameProcessor;
    private Inputtable inputter;
    private Renderable renderer;
    LEDProcessor ledProcessor;
    TextView score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledblock);

        renderer = new LEDRenderer(this,1);
        //renderer = new ScreenRender(this);
        inputter = new CalliBirdInterface(this);
    }

    protected void onResume(){
        super.onResume();
        beginNewGame();
    }

    protected void onPause(){
        super.onPause();
        endGame();
    }

    public void beginNewGame(){
        inputter.setRunningState(true);
        gameProcessor = new GameProcessor(inputter,renderer);
        gameProcessor.start();

    }

    public void endGame(){
        inputter.setRunningState(false);
    }


    public void setInput(View view){
        if(inputter!=null)
            inputter.setInputState(true);
    }
}
