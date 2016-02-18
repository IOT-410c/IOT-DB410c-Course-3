package com.calit2.bluetoothremotereceiver;

import net.calit2.mooc.iot_db410c.db410c_gpiolib.GpioProcessor;

/******************************************************************************
 * Name:        GpioInputParser
 * Description: Class used to parse the message from BluetoothManager and
 *              execute the appropriate Gpio commands.
 *****************************************************************************/
public class GpioInputParser {

  // Log Messages
  private static final String TAG = "GpioInputParser";

  // GpioProcessor to make changes to Gpios
  private static GpioProcessor processor = new GpioProcessor();

  // Constants for Gpio
  private static final int HIGH = 1;
  private static final int LOW  = 0;

  /****************************************************************************
   * Name:        parseMessage
   * Description: Delegates to helper methos the appropriate information based
   *              on the message received from ConnectThread's run
   *
   * @param       message String containing the message received from the
   *                      BluetoothRemote
   ***************************************************************************/
  public static boolean parseMessage(String message) throws InterruptedException {
    switch (message) {
      case "yellow_on":
        signalLED(processor.getPin26(), true);
        return true;
      case "yellow_off":
        signalLED(processor.getPin26(), false);
        return true;
      case "red_on":
        signalLED(processor.getPin34(), true);
        return true;
      case "red_off":
        signalLED(processor.getPin34(), false);
        return true;
      case "green_on":
        signalLED(processor.getPin24(), true);
        return true;
      case "green_off":
        signalLED(processor.getPin24(), false);
        return true;
      default:
        return false; // TODO why did you return true in the video?
    }
  }

  /****************************************************************************
   * Name:        signalLed
   * Description: Lights up/ turns off the indicator LED
   *
   * @param       pin the Pin whose value will be changed
   * @param       on  Determines if the LED indicator should be turned on/ off
   ***************************************************************************/
  private static void signalLED(GpioProcessor.Gpio pin, boolean on) {
    pin.out();
    setPinValue(pin, on ? HIGH : LOW);
  }

  /****************************************************************************
   * Name:        setPinValue
   * Description: Helper method to determine what value to set a specific Gpio
   *
   * @param       pin GpioProcessor.Gpio pin whose value will be modified
   * @param       value int thay will be used to set the value of the pin.
   *                    Either HIGH (1) or LOW (0)
   ***************************************************************************/
  private static void setPinValue(GpioProcessor.Gpio pin, int value) {
    if (value == HIGH) {
      pin.high();
    } else {
      pin.low();
    }
  }
}