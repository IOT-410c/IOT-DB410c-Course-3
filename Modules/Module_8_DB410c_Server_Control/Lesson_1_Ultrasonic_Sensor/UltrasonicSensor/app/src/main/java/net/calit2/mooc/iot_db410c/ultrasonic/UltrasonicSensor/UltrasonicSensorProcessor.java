package net.calit2.mooc.iot_db410c.ultrasonic.UltrasonicSensor;

/**
 * Created by Ara on 2/24/15.
 */

import android.util.Log;

import net.calit2.mooc.iot_db410c.db410c_gpiolib.GpioProcessor.Gpio;

/*
    This class encapsulates the use of the ultrasonic sensor. It contains the algorithms needed to
    compute the distance the cube is away from the nearest object. It then displays this information
    on the screen and through LED indications.
 */
public class UltrasonicSensorProcessor extends Thread {

    private static final String TAG = "UltrasonicProcessor";

    public static final int SOUNDVELOCITY = 33450; //speed of sound at our location. can be variable
    public static final int DELAY = 10000;//delay in nanoseconds.
    public static final long FREQUENCY = 500;//every 500 milliseconds
    public static final double BILLION = 1000000000.;

    private Gpio echoPin;
    private Gpio triggerPin;
    private UltraSonicInterface ultrasonicInterface;

    private double distance;
    private long time;


    public UltrasonicSensorProcessor(Gpio echoPin, Gpio triggerPin, UltraSonicInterface ultrasonicInterface){

        Log.i(TAG, "UltrasonicSensorProcessor intializing");

        this.echoPin = echoPin;
        this.triggerPin = triggerPin;
        this.ultrasonicInterface = ultrasonicInterface;
        triggerPin.out();
        echoPin.in();
        triggerPin.low();
        time = System.currentTimeMillis();
        distance = -1;

    }

    // computes the distance given the time
    private double distance(double time){
        Log.v(TAG,"Calculating Distance");
        return SOUNDVELOCITY*time/2.0;
    }

    private double processDistance() {

        Log.i(TAG, "Processing Distance");
        triggerPin.high();
        try {
            Thread.sleep(0, DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        triggerPin.low();

        long timeout = System.currentTimeMillis();
        while (echoPin.getValue() == 0) {
            if (System.currentTimeMillis() - timeout > 50){
                Log.w(TAG,"Processing taking too long. Discarding value");
                return -1;
            }
        }
        long time1 = System.nanoTime();

        long stop = System.currentTimeMillis();


        while (echoPin.getValue() == 1) {
            if (System.currentTimeMillis() - timeout > 50) {
                Log.w(TAG,"Processing taking too long. Discarding value1");
                return -1;
            }
        }
        long time2 = System.nanoTime();
        double deltaTime = (time2 - time1) / BILLION;
        return distance(deltaTime);

    }

    public double process() {
        if ((System.currentTimeMillis() - time > UltrasonicSensorProcessor.FREQUENCY)) {
            distance =processDistance();
            time = System.currentTimeMillis();
        }
        return distance;
    }

    public void run(){
        while(ultrasonicInterface.getRunningState()){
            ultrasonicInterface.execute(process());
        }

    }



}
