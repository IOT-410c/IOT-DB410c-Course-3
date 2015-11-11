package calit2.bluetoothremote;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Name: MainActivity
 * Description: The main interface the User interacts with. This class
 *              delegates the Bluetooth connectivity and the like to
 *              the other classes.
 */
public class MainActivity extends Activity {

    // TODO: Remove all Log messages

    // Determine which Activity/ Tasks to perform next for onActivityResult
    private static final int REQUEST_ENABLE_BT       = 1;
    private static final int REQUEST_CONNECT_DEVICES = 2;

    // Constants for Coding the message
    private static final String HIGH = "1-";
    private static final String LOW  = "0-";

    private BluetoothManager btManager;
    private BluetoothAdapter btAdapter;

    // Booleans to determine whether or not to send the signal
    private boolean isPIRActive         = false;
    private boolean isLEDOneActive      = false;
    private boolean isUltraSonicActive  = false;
    private boolean isStepperActive     = false;

    // Variables for stepper motor
    private static final int DEFAULT_DEGREES = 90;
    private boolean isCW = true;
    private int degrees = 0;

    /**
     * Name:onCreate
     * Description: Creates the views for the screen and instantiates the
     *              BluetoothAdapter
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TextView btStatus = (TextView) findViewById(R.id.BT_MESSAGE);

        // Determines if the device has Bluetooth capabilities
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.e("$$", "MainActivity - BluetoothAdapter Created.");

        if (btAdapter == null) {
            btStatus.setText("Your device does not have Bluetooth Capabilities.");
            Log.e("$$", "BluetoothAdapter was null.");
        }
    }

    /**
     * Name: sendCommands
     * Description: Sends the appropriate commands to the remote Bluetooth
     *              Device once a proper connection is established.
     */
    public void sendCommands(View v) {
        Log.e("$$", "MainActivity - Called sendCommands.");

        // Only performs the action if the adapter is enabled
        if (btAdapter != null && btAdapter.isEnabled()) {
            Log.e("$$", "MainActivity - sendCommands; btAdpater not null and is enabled");
            // Determines if the device is already paired and sends the
            // proper signal to the GPIO
            if (btAdapter.getBondedDevices().size() > 0 && btManager != null) {
                Log.e("$$", "MainActivity - btmanager is not null and adapter is bonded");
                String code = "test";

                // 0 = stepper activated; 1 = spin direction; 2 = degrees;
                // 3 = pir; 4 = led; 5 = ultrasonic
                if (isStepperActive) {
                    code += HIGH;
                    if (isCW) {
                        code += HIGH;
                    } else {
                        code += LOW;
                    }
                    code += degrees + "-";
                } else {
                    code += LOW + LOW + LOW;
                }

                if (isPIRActive) {
                    code += HIGH;
                } else {
                    code += LOW;
                }

                if (isLEDOneActive) {
                    code += HIGH;
                } else {
                    code += LOW;
                }

                if (isUltraSonicActive) {
                    code += HIGH;
                } else {
                    code += LOW;
                }

                byte[] codedMsg = code.getBytes();
                btManager.write(codedMsg);
            } else {
                scanBT();
            }
        } else {
            turnBTOn();
        }
    }

    /**
     * Name: sendStepper
     * Description: Stores the command for the stepper motor once a proper
     *              Bluetooth connection is established.
     */
    public void sendStepper(View v) {
        Log.e("$$", "MainActivity - Called sendStepperMotor.");

        // Only performs the action if the adapter is enabled
        if (btAdapter != null && btAdapter.isEnabled()) {

            // Toggles the command and status if device is already paired
            if (btAdapter.getBondedDevices().size() > 0) {
                isStepperActive = !isStepperActive;
                toggleStatus((TextView) findViewById(R.id.STEPPER_STATUS),
                        isStepperActive, true);
            } else {
                scanBT();
            }
        } else {
            turnBTOn();
        }
    }

    /**
     * Name: sendPIR
     * Description: Stores the command for the PIR sensor once a proper
     *              Bluetooth connection is established.
     */
    public void sendPIR(View v) {
        Log.e("$$", "MainActivity - Called sendPIR.");

        // Only performs the action if the adapter is enabled
        if (btAdapter != null && btAdapter.isEnabled()) {

            // Toggles the command and status if device is already paired
            if (btAdapter.getBondedDevices().size() > 0) {
                isPIRActive = !isPIRActive;
                toggleStatus((TextView) findViewById(R.id.PIR_STATUS),
                        isPIRActive, false);
            } else {
                scanBT();
            }
        } else {
            turnBTOn();
        }
    }

    /**
     * Name: sendLEDOne
     * Description: Stores the command for LED 1 once a proper Bluetooth
     *              connection is established.
     */
    public void sendLEDOne(View v) {
        Log.e("$$", "MainActivity - Called sendLEDOne");

        // Only performs the action if the adapter is enabled
        if (btAdapter != null && btAdapter.isEnabled()) {

            // Toggles the command and status if device is already paired
            if (btAdapter.getBondedDevices().size() > 0) {
                isLEDOneActive = !isLEDOneActive;
                toggleStatus((TextView) findViewById(R.id.LED_1_STATUS),
                        isLEDOneActive, false);
            } else {
                scanBT();
            }
        } else {
            turnBTOn();
        }
    }

    /**
     * Name: sendLEDTwo
     * Description: Stores the command for LED 2 once a proper Bluetooth
     * *            connection is established.
     */
    public void sendUltraSonic(View v) {
        Log.e("$$", "MainActivity - Called sendLEDTwo");

        // Toggles the command and status if device is already paired
        if (btAdapter != null && btAdapter.isEnabled()) {

            // Determines if the device is already paired
            if (btAdapter.getBondedDevices().size() > 0) {
                isUltraSonicActive = !isUltraSonicActive;
                toggleStatus((TextView) findViewById(R.id.ULTRA_SONIC_STATUS),
                        isUltraSonicActive, false);

            } else {
                scanBT();
            }
        } else {
            turnBTOn();
        }
    }

    /**
     * Name: turnBTOn
     * Description: Makes a request to enable Bluetooth on the device
     */
    private void turnBTOn() {
        Log.e("$$", "MainActivity - Called turnBTOn");

        // Instantiates the BluetoothManager if null
        if (btManager == null) {
            btManager = new BluetoothManager();
        }

        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

        Log.e("$$", "MainActivity - Finished Bluetooth Request");
    }

    /**
     * Name: scanBT
     * Description: Redirects the App to a different screen to scan for other
     *              Bluetooth devices.
     */
    private void scanBT() {
        Log.e("$$", "MainActivity - Called scanBT");

        // Instantiates the BluetoothManager if null
        if (btManager == null) {
            btManager = new BluetoothManager();
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
        Log.e("$$", "MainActivity - Called onActivityResult. RequestCode = "
                + requestCode + "| ResultCode = " + resultCode);

        switch(requestCode) {
            case REQUEST_ENABLE_BT:
                // Bluetooth was enabled
                if (resultCode == Activity.RESULT_OK) {
                    Intent serverIntent = new Intent(this, BluetoothScanner.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICES);
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
                    Log.e("$$", "MainActivity - onActivityResult. Address = " + address + " connecting to a device = " + btDevice.getName());
                    btManager.connect(btDevice);
                }
                break;
        }
    }

    /**
     * Name: toggleStatus
     * Description: Helper method to toggle status of each button
     */
    private void toggleStatus(TextView status, boolean isActive,
                              boolean fromStepper) {
        if (!fromStepper) {
            if (isActive) {
                status.setText(R.string.active);
            } else {
                status.setText(R.string.inactive);
            }
        } else {
            if (isActive && isCW) {
                status.setText(getString(R.string.CW) + " - " + //degrees +
                        "\n" + getString(R.string.active));
            } else if (isActive) {
                status.setText(getString(R.string.CCW) + " - " + //degrees +
                        "\n" + getString(R.string.active));
            } else {
                status.setText(R.string.inactive);
            }
        }
    }

    /**
     * Name: toggleSpinDirection
     * Description: Toggles the direction of the stepper motor's spin
     */
    public void toggleSpinDirection(View v) {
        Button spinDir = (Button) findViewById(R.id.ROTATION);

        if (isCW) {
            isCW = false;
            spinDir.setText(R.string.CCW);
        } else {
            isCW = true;
            spinDir.setText(R.string.CW);
        }
    }

    /**
     * Name: addSpin
     * Description: Increase the degrees of the stepper motor's spin
     */
    public void addSpin(View v) {
        if (isStepperActive) {
            degrees += DEFAULT_DEGREES;
        }
    }

    /**
     * Name: subSpin
     * Description: Decrease the degrees of the stepper motor's spin
     */
    public void subSpin(View v) {
        if (isStepperActive) {
            degrees -= DEFAULT_DEGREES;
        }
    }
}