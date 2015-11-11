package net.calit2.mooc.iot_db410c.ledblock.gameprocessor;

import android.util.Log;

/**
 * Created by Ara on 1/20/15.
 */
public class GameProcessor extends Thread {
    private static final String TAG ="GameProcessor" ;
    private Map map;
    private CalliBird calliBird;
    private Renderable renderer;
    private Inputtable input;
    private int score;

    public GameProcessor(Inputtable input, Renderable renderer){
        this.renderer=renderer;
        this.input=input;
        calliBird = new CalliBird(3,3);
        map= new Map(calliBird);
        score=0;
        renderer.begin();

       // this.activity=activity;
        //input = false;
        Log.e(TAG,"CONSTRUCTOR");
       // ledProcessor = new LEDProcessor(1);
       /* gpioProcessor= new GpioProcessor();
        clicker = gpioProcessor.getPin23();
        clicker.in();*/

    }

    public void run(){

        long time = System.currentTimeMillis();
        int index = 0;

        while(input.getRunningState()){
            time = System.currentTimeMillis();


            if(input.getInputState()){
                if(!calliBird.incrementy()){
                    calliBird.decrementy();
                }
                input.setInputState(false);
            }else if(!calliBird.decrementy()) {
                map.removeCalliBird();
                input.setRunningState(false);
                break;
            }
            //move the pipes over every two inputs
            if(index%2==0)
                map.move();

            if(index%6==0) {
                map.randomize();
            }

            //get map data
            char[][] data = map.updateMap();

            //we want to show the movements first. Then check for collisions. If collide, exit out.

            if(map.collisions()){
                input.setRunningState(false);
                break;
            }

            if(map.checkCalliBirdScore()&&index%2==0){
                score++;
            }

            renderer.render(data,score);

            long difference =System.currentTimeMillis()-time;
            if(difference>500) {
                sleepTime(0);
            }else {
                sleepTime(500-difference);
            }

            index++;
        }
        renderer.clear(score);

    }

    public void sleepTime(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
