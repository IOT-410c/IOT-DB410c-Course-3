package net.calit2.mooc.iot_db410c.ultrasonic;

/**
 * Created by Ara on 2/24/15.
 */

import android.util.Log;
import net.calit2.mooc.iot_db410c.db410c_gpiolib.GpioProcessor;
import net.calit2.mooc.iot_db410c.db410c_gpiolib.GpioProcessor.Gpio;

/*
    This class encapsulates the processing of the LED lights.
 */
public class LEDProcessor {

    private static final String TAG = "LEDProcesser";
    private Gpio red;
    private Gpio yellow;
    private Gpio green;

    public LEDProcessor(Gpio red,Gpio yellow, Gpio green){
        Log.i(TAG, "Initializing LEDProcessor");
        this.red = red;
        this.yellow=yellow;
        this.green = green;
        red.out();
        yellow.out();
        green.out();

    }

    public void redOn(){
        //Log.i(TAG,"Turning Red On");
        red.high();
    }
    public void redOff(){
       // Log.i(TAG,"Turning Red Off");
        red.low();
    }
    public void yellowOn(){
       // Log.i(TAG,"Turning Yellow On");
        yellow.high();
    }
    public void yellowOff(){
      //  Log.i(TAG,"Turning Yellow Off");
        yellow.low();
    }
    public void greenOn(){
       // Log.i(TAG,"Turning Green On");
        green.high();
    }
    public void greenOff(){
       // Log.i(TAG,"Turning Green Off");
        green.low();
    }
    public void allOn(){
        redOn();
        yellowOn();
        greenOn();
    }
    public void allOff(){
        redOff();
        yellowOff();
        greenOff();
    }



}
