package net.calit2.mooc.iot_db410c.ledblock;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.calit2.mooc.iot_db410c.ledblock.gameprocessor.Map;
import net.calit2.mooc.iot_db410c.ledblock.gameprocessor.Renderable;
import net.calit2.mooc.iot_db410c.db410c_gpiolib.GpioProcessor;

/**
 * Created by Ara on 8/9/15.
 */
public class LEDRenderer implements Renderable {

    private final LEDBlockActivity activity;
    TextView scoreView;
    LEDProcessor ledProcessor;
    GpioProcessor gpioProcessor;
    private Button restartButton;

    public LEDRenderer(LEDBlockActivity activity, int devices){
        this.activity=activity;
        this.gpioProcessor= new GpioProcessor();

        ledProcessor = new LEDProcessor(devices,gpioProcessor.getPin24(),
                gpioProcessor.getPin34(),gpioProcessor.getPin26());
    }

    @Override
    public void begin() {
        activity.setContentView(R.layout.activity_ledblock);

        scoreView = (TextView)activity.findViewById(R.id.score);

    }

    @Override
    public void render(char[][] data, final int score) {
        char[] newData = new char[Map.HEIGHT];
        for(int i = 0;i<data.length;i++){
            for(int j=0;j<data[i].length;j++){
                newData[i] = (char) (newData[i]|(((int)data[i][j])<<(Map.WIDTH-j-1)));
            }
        }
        for(int i = 0;i<newData.length;i++){
            ledProcessor.setRow(0, i, newData[i]);
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scoreView.setText("Score: "+ score);

            }
        });
    }

    @Override
    public void clear(final int score) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {


                activity.setContentView(R.layout.gameover_ledblock);
                restartButton = (Button)activity.findViewById(R.id.restart);
                restartButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.beginNewGame();
                    }
                });
                ((TextView) activity.findViewById(R.id.gameover_score)).setText("Score: " + score);
            }
        });

    }
}
