package net.calit2.mooc.iot_db410c.ledblock;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import net.calit2.mooc.iot_db410c.ledblock.gameprocessor.Map;
import net.calit2.mooc.iot_db410c.ledblock.gameprocessor.Renderable;

import java.util.ArrayList;

/**
 * Created by Ara on 1/20/15.
 */
public class ScreenRender implements Renderable {

    //private static final String TAG = "ScreenRender" ;
    private final LEDBlockActivity activity;
    private TextView scoreView;
    private GridView grid;

    private Bitmap bird;
    private Bitmap block;
    private Bitmap blank;

    public ScreenRender(LEDBlockActivity activity){
        this.activity = activity;
    }

    @Override
    public void begin() {
        activity.setContentView(R.layout.activity_ledblock);
        scoreView = (TextView)activity.findViewById(R.id.score);
        activity.findViewById(R.id.layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TAG", "IM BEING CLICKED");
                activity.setInput(v);
            }
        });

        grid = (GridView) activity.findViewById(R.id.gridView);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                activity.setInput(view);
            }
        });

        bird = BitmapFactory.decodeResource(activity.getResources(),
                                            R.drawable.bird);
        block = BitmapFactory.decodeResource(activity.getResources(),
                                            R.drawable.block);
        blank = BitmapFactory.decodeResource(activity.getResources(),
                                            R.drawable.blank);
    }

    @Override
    public void render(char[][] data, final int score) {
        ArrayList<Bitmap> arrayList = new ArrayList<>();

        for(int i = 0; i< Map.WIDTH; i++){
            for(int j = 0; j<Map.HEIGHT; j++){
                if (data[i][j] == 0) {
                    arrayList.add(blank);
                } else if (data[i][j] == 1) {
                    arrayList.add(block);
                } else {
                    arrayList.add(bird);
                }
            }
        }

        final GridAdapter gA = new GridAdapter(arrayList, activity);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scoreView.setText("Score: " + score);
                grid.setAdapter(gA);

            }
        });

    }

    @Override
    public void clear(final int score) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.setContentView(R.layout.gameover_ledblock);
                ((TextView) activity.findViewById(R.id.gameover_score)).setText("Score: " + score);

                Button restartButton = (Button) activity.findViewById(R.id.restart);
                restartButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.beginNewGame();
                    }
                });
            }
        });
    }
}
