package net.calit2.mooc.iot_db410c.ultrasonic;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import net.calit2.mooc.iot_db410c.db410c_gpiolib.GpioProcessor;
import net.calit2.mooc.iot_db410c.db410c_gpiolib.GpioProcessor.Gpio;
import net.calit2.mooc.iot_db410c.ultrasonic.UltrasonicSensor.UltrasonicSensorProcessor;


public class UltrasonicSensorActivity extends Activity {
    private static final String TAG = "UltrasonicActivity";



    private LEDProcessor ledProcessor;
    private GpioProcessor processor;
    private Gpio triggerPin;
    private Gpio echoPin;
    private Gpio yellow;
    private Gpio red;
    private Gpio green;
    private DistanceCalculator distanceCalculator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }
    private void init(){
        Log.i(TAG, "Initializing Variables");
        processor = new GpioProcessor();

        echoPin = processor.getPin32();
        triggerPin = processor.getPin33();
        red = processor.getPin34();
        yellow = processor.getPin26();
        green = processor.getPin24();

        ledProcessor = new LEDProcessor(red,yellow,green);
        distanceCalculator = new DistanceCalculator(this, ledProcessor);

    }

    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        distanceCalculator.setRunningState(true);
        new UltrasonicSensorProcessor(echoPin,triggerPin, distanceCalculator).start();

    }



    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        distanceCalculator.shutDown();

    }

    protected void onDestroy(){
        super.onDestroy();
        Log.i(TAG, "onDestroy");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pir__ultrasonic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
