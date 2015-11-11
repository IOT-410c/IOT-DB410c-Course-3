package calit2.bluetoothremotereceiver;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Name: GpioProcessor
 * Description:
 */
public class GpioProcessor {

    /**
     * Name: Gpio
     * Description:
     */
    public class Gpio {
        private int pin;
        String PATH = "/sys/class/gpio";

        /**
         * Name: Gpio
         * Description: Constructor for the Gpio class
         */
        public Gpio(int pin) {
            this.pin = pin;
        }

        /**
         * Name: setDirection
         * Description:
         */
        public void setDirection(String dir) {
            BufferedWriter out;

            try {
                FileWriter fstream = new FileWriter(PATH + "/gpio" + pin +
                        "/direction", false);
                out = new BufferedWriter(fstream);
                out.write(dir);
                out.close();
                Log.e("$$$", "GPIOPROCESSOR - Wrote setDirection");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("$$$", "GPIOPROCESSOR - Failed setDirection." + e);
            }
        }

        /**
         * Name: setValue
         * Description:
         */
        public void setValue(int value) {
            BufferedWriter out;

            try{
                FileWriter fstream = new FileWriter(PATH + "/gpio" + pin +
                        "/value", false);
                out = new BufferedWriter(fstream);
                out.write(Integer.toString(value));
                out.close();
                Log.e("$$$", "GPIOPROCESSOR - Wrote setValue");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("$$$", "GPIOPROCESSOR - Failed setValue. " + e);
            }
        }

        /**
         * Name: getDirection
         * Description:
         */
        public String getDirection() {
            BufferedReader br;
            String line = "";

            try {
                br = new BufferedReader(new FileReader(PATH + "/gpio" + pin +
                        "/direction"));
                line = br.readLine();
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return line;
        }

        /**
         * Name: getValue
         * Description:
         */
        public int getValue(){
            BufferedReader br;
            String line = "";

            try {
                br = new BufferedReader(new FileReader(PATH + "/gpio" + pin +
                        "/value"));
                line = br.readLine();
                br.close();
            } catch(Exception e){
                e.printStackTrace();
            }

            return Integer.parseInt(line);
        }
    }

    /**
     * Name: getPin
     * Description: Returns a new Gpio object
     */
    public Gpio getPin(int pin) {
        return new Gpio(pin);
    }

    /**
     * Name: getPin23
     * Description: Returns the corresponding Gpio
     */
    public Gpio getPin23() {
        return new Gpio(938);
    }

    /**
     * Name: getPin24
     * Description: Returns the corresponding Gpio
     */
    public Gpio getPin24() {
        return new Gpio(914);
    }

    /**
     * Name: getPin25
     * Description: Returns the corresponding Gpio
     */
    public Gpio getPin25() {
        return new Gpio(915);
    }

    /**
     * Name: getPin26
     * Description: Returns the corresponding Gpio
     */
    public Gpio getPin26() {
        return new Gpio(971);
    }

    /**
     * Name: getPin27
     * Description: Returns the corresponding Gpio
     */
    public Gpio getPin27() {
        return new Gpio(1017);
    }

    /**
     * Name: getPin28
     * Description: Returns the corresponding Gpio
     */
    public Gpio getPin28() {
        return new Gpio(901);
    }

    /**
     * Name: getPin29
     * Description: Returns the corresponding Gpio
     */
    public Gpio getPin29() {
        return new Gpio(926);
    }

    /**
     * Name: getPin30
     * Description: Returns the corresponding Gpio
     *
     public Gpio getPin30() {
     return new Gpio(927);
     }*/

    /**
     * Name: getPin31
     * Description: Returns the corresponding Gpio
     */
    public Gpio getPin31() {
        return new Gpio(937);
    }

    /**
     * Name: getPin32
     * Description: Returns the corresponding Gpio
     */
    public Gpio getPin32() {
        return new Gpio(936);
    }

    /**
     * Name: getPin33
     * Description: Returns the corresponding Gpio
     */
    public Gpio getPin33() {
        return new Gpio(930);
    }

    /**
     * Name: getPin34
     * Description: Returns the corresponding Gpio
     */
    public Gpio getPin34() {
        return new Gpio(935);
    }
}

// TODO: might want to remove these individual getPin## and just code it in