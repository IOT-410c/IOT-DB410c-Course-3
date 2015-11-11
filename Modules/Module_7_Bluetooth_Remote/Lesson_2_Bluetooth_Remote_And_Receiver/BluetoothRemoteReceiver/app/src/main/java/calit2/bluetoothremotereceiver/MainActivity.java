package calit2.bluetoothremotereceiver;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

/**
 * Name:
 * Description:
 */
public class MainActivity extends Activity {

    // Determine which Activity/ Tasks to perform next for onActivityResult
    private static final int REQUEST_ENABLE_BT       = 1;
    private static final int REQUEST_CONNECT_DEVICES = 2;

    private BluetoothManager btManager;
    private BluetoothAdapter btAdapter;
    private GpioProcessor processor;

    // Constants for Pins
    private static final String DIRECTION = "out";
    private static final int HIGH = 1;
    private static final int LOW  = 0;

    // Constants for Stepper Motor
    private static final float STEP_ANGLE = 1.8f;
    private static final int STEP_DELAY = 200; // in milliseconds
    private static final int STEPPER_SEQ_CW [][]  = {{LOW, HIGH, LOW, HIGH},
            {HIGH, LOW, LOW, HIGH},
            {HIGH, LOW, HIGH, LOW},
            {LOW, HIGH, HIGH, LOW}};
    private static final int STEPPER_SEQ_CCW [][] = {{LOW, HIGH, LOW, HIGH},
            {LOW, HIGH, HIGH, LOW},
            {HIGH, LOW, HIGH, LOW},
            {HIGH, LOW, LOW, HIGH}};

    private final Handler handler = new Handler() {
        /**
         * Name: handleMessage
         * Description:
         */
        @Override
        public void handleMessage(Message msg) {
            byte[] buff = (byte[]) msg.obj;
            String input = new String(buff, 0, msg.arg2);
            String[] decodedMsg = input.split("-");
            Log.e("$$", "Handler - received message; message = " + input + "; decodedMsg = " + decodedMsg.toString());
/*
            // Determines if the program will spin the motor
            if (decodedMsg[0].equals("" + HIGH)) {
                int degrees = Integer.parseInt(decodedMsg[2]);

                // Does the motor spin CW or CCW
                if (decodedMsg[1].equals("" + HIGH)) { // CW
                    try {
                        rotateStepperMotor(true, degrees);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else { //CCW
                    try {
                        rotateStepperMotor(false, degrees);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            // Determines if the PIR should be activated
            if (decodedMsg[3].equals("" + HIGH)) {
                // do gpio thing for pin
                // 1 pin
            } else {
                // turn it off/ set to 0
            }

            // Determines if the LED should be activated
            if (decodedMsg[4].equals("" + HIGH)) {
                // Do gpio thing for pin
                // 1 pin
            } else {
                // turn gpio off/ set to 0
            }

            // Determines if the UltraSonic should be activated
            if (decodedMsg[5].equals("" + HIGH)) {
                // do gpio stuff
                // 5 pins
                // try to make it display distance
            } else {
                //turn gpio off
            }*/
        }
    };

    /**
     * Name:
     * Description:
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Determines if device has Bluetooth Capabilities
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter == null) {
            Toast.makeText(getApplicationContext(),
                    "Unfortunately your device does not have Bluetooth " +
                            "Capabilities.",
                    Toast.LENGTH_SHORT).show();
        } else {
            // Instantiates the GpioProcessor
            processor = new GpioProcessor();
        }
    }

    /**
     * Name:
     * Description:
     */
    public void receiveData(View v) {
        Log.e("$$", "MainActivity - receiveData");
        if (btAdapter != null) {
            if (btAdapter.isEnabled()) {
                TextView a = (TextView) findViewById(R.id.textView);
                startReceiving();
                a.setText("It's on receive data");
            } else {
                turnBTOn();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Unfortunately your device does not have Bluetooth " +
                            "Capabilities.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Name: startReceiving
     * Description:
     */
    private void startReceiving() {
        Log.e("$$", "MainActivity - startReceiving");
        if (btManager != null) {
            btManager.start();
        }
    }

    /**
     * Name: turnBTOn
     * Description: Makes a request to enable Bluetooth on the device
     */
    private void turnBTOn() {
        Log.e("$$", "MainActivity - turnBTOn");

        // Instantiates the BluetoothManager if null
        if (btManager == null) {
            btManager = new BluetoothManager(handler);
        }

        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    /**
     * Name: scanBT
     * Description: Redirects the App to a different screen to scan for other
     *              Bluetooth devices.
     */ //TODO: Might not need this since you only need to scan
    private void scanBT() {
        // Instantiates the BluetoothManager if null
        if (btManager == null) {
      //      btManager = new BluetoothManager();
        }

        Intent serverIntent = new Intent(this, BluetoothScanner.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICES);
    }

    /**
     * Name: onActivityResult
     * Description: Delegates a specific task depending on the outcome of
     *              startActivityResult (called from scanBT and turnBTOn).
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        switch(requestCode) {
            case REQUEST_ENABLE_BT:
                // Bluetooth was enabled
                if (resultCode == Activity.RESULT_OK) {
                    btManager.start();
                } else {
                    Toast.makeText(this, "Bluetooth not enabled.",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CONNECT_DEVICES:
                if (data != null) {
                    String address = data.getExtras()
                            .getString("device_address");
                    BluetoothDevice btDevice =
                            btAdapter.getRemoteDevice(address);
                    btManager.connect(btDevice);
                }
                break;
        }
    }

    /**
     * Name: rotateStepperMotor
     * Description: Sends the appropriate signals to the Dragonboard
     */
    private void rotateStepperMotor(boolean isCW, int degrees) throws InterruptedException {
        // Stepper Motor Pins
        GpioProcessor.Gpio pin23 = processor.getPin23();
        GpioProcessor.Gpio pin24 = processor.getPin24();
        GpioProcessor.Gpio pin25 = processor.getPin25();
        GpioProcessor.Gpio pin26 = processor.getPin26();

        // Sets the Direction of the pins
        pin23.setDirection(DIRECTION);
        pin24.setDirection(DIRECTION);
        pin25.setDirection(DIRECTION);
        pin26.setDirection(DIRECTION);

        // Variable to run the stepper motor sequence
        int temp = (int) (degrees/STEP_ANGLE);

        // Rotates the Stepper Motor Clockwise or Counter-Clockwise
        if (isCW) {
            for (int i = 0; i < temp; i++) {
                pin23.setValue(STEPPER_SEQ_CW[i%4][0]);
                Thread.sleep(STEP_DELAY);
                pin24.setValue(STEPPER_SEQ_CW[i%4][1]);
                Thread.sleep(STEP_DELAY);
                pin25.setValue(STEPPER_SEQ_CW[i%4][2]);
                Thread.sleep(STEP_DELAY);
                pin26.setValue(STEPPER_SEQ_CW[i%4][3]);
                Thread.sleep(STEP_DELAY);
            }
        } else {
            for (int i = 0; i < temp; i++) {
                pin23.setValue(STEPPER_SEQ_CCW[i%4][0]);
                Thread.sleep(STEP_DELAY);
                pin24.setValue(STEPPER_SEQ_CCW[i%4][1]);
                Thread.sleep(STEP_DELAY);
                pin25.setValue(STEPPER_SEQ_CCW[i%4][2]);
                Thread.sleep(STEP_DELAY);
                pin26.setValue(STEPPER_SEQ_CCW[i%4][3]);
                Thread.sleep(STEP_DELAY);
            }
        }
    }
}