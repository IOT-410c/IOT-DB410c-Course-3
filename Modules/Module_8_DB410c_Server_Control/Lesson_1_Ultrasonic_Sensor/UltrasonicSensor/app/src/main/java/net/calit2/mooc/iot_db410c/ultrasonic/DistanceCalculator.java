package net.calit2.mooc.iot_db410c.ultrasonic;

import android.app.Activity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import net.calit2.mooc.iot_db410c.ultrasonic.UltrasonicSensor.UltraSonicInterface;

/**
 * Created by Ara on 8/10/15.
 */
public class DistanceCalculator implements UltraSonicInterface {
    private static final String TAG = "UltrasonicInterface" ;
    public final int  BLACK;
    public final int RED;
    public final int YELLOW;
    public final int GREEN;


    private  Activity activity;
    private LEDProcessor ledProcessor;

    private boolean state;
    private boolean ultrasonicOn;
    private TextView textView;


    public DistanceCalculator(Activity activity, LEDProcessor ledProcessor){
        this.activity = activity;
        this.ledProcessor = ledProcessor;
        BLACK= activity.getResources().getColor(R.color.black);
        RED =activity.getResources().getColor(R.color.red);
        YELLOW=activity.getResources().getColor(R.color.yellow);
        GREEN=activity.getResources().getColor(R.color.green);

        activity.setContentView(R.layout.activity_ultrasonic_sensor);
        textView = (TextView)activity.findViewById(R.id.textView);
        ((ToggleButton)activity.findViewById(R.id.ultrasonicButton)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setUltrasonicOn(isChecked);
            }
        });
    }

    @Override
    public boolean getRunningState() {
        return state;
    }

    @Override
    public void setRunningState(boolean state) {
        this.state=state;
    }

    @Override
    public void execute(double distance) {
        if(isUltrasonicOn()){
            changeColor(distance);
        }else{
            turnOffDisplay();
        }
    }



    public void setUltrasonicOn(boolean ultrasonicOn){
       this.ultrasonicOn=ultrasonicOn;
    }

    public boolean isUltrasonicOn() {
        return ultrasonicOn;
    }


    private void changeColor(double distance){
     //   Log.i(TAG, "Turning On LED's");
        if(distance<0){
            turnOffDisplay();
            return;
        }
        int color = BLACK;
        if (distance > 0 && distance < 30) {

            ledProcessor.greenOn();
            color=GREEN;
            if (distance < 20) {
                ledProcessor.yellowOn();
                color=YELLOW;
            }
            if (distance < 10) {
                ledProcessor.redOn();
                color=RED;
            }
        }

        updateColoredDistance(distance, color);

    }

    private void updateColoredDistance(final double distance, final int color){
       // Log.i(TAG, "Updating TextView");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText("You are " + String.format("%.2f", distance) + "cm away from the nearest object");
                textView.setTextColor(color);
            }
        });


    }
    private void turnOffTextView(){

      //  Log.i(TAG, "Updating TextView");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText("The UltraSonic Sensor has been turned off");
                textView.setTextColor(BLACK);
            }
        });


    }

    public void turnOffDisplay(){
        turnOffTextView();
        ledProcessor.allOff();
    }

    public void shutDown() {
        turnOffDisplay();
        setRunningState(false);

    }
}
