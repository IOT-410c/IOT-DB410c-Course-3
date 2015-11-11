package net.calit2.mooc.iot_db410c.webserver;

import net.calit2.mooc.iot_db410c.db410c_gpiolib.GpioProcessor;

/**
 * Created by Ara on 10/20/15.
 */
public class LEDBlinker{
    private static boolean lock = false;


    public static boolean blink(final int times, final boolean red, final boolean yellow, final boolean green){
        if(!lock) {
            setLock(true);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    GpioProcessor gpioProcessor = new GpioProcessor();
                    GpioProcessor.Gpio redGpio = gpioProcessor.getPin34();
                    GpioProcessor.Gpio yellowGpio = gpioProcessor.getPin26();
                    GpioProcessor.Gpio greenGpio = gpioProcessor.getPin24();
                    redGpio.out();
                    yellowGpio.out();
                    greenGpio.out();
                    for (int i = 0; i < times; i++) {
                        if(red){
                           redGpio.high();
                        }
                        if(yellow){
                            yellowGpio.high();

                        }
                        if(green){
                            greenGpio.high();
                        }
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(red){
                            redGpio.low();
                        }
                        if(yellow){
                            yellowGpio.low();

                        }
                        if(green){
                            greenGpio.low();
                        }
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    setLock(false);


                }
            }).start();
            return true;
        }

        return false;
    }

    private static void setLock(boolean status){
        lock= status;
    }
    private static void blinkLED(GpioProcessor.Gpio led){
        led.high();

        led.low();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
