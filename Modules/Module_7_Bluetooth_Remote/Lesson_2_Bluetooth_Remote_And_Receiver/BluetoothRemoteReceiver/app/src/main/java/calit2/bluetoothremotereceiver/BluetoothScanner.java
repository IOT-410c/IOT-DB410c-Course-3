package calit2.bluetoothremotereceiver;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

/**
 * Name: BluetoothScanner
 * Description: Creates the view for the user to choose from the available
 *              devices. Also performs the Bluetooth discovery scan.
 */
public class BluetoothScanner extends Activity {

    private BluetoothAdapter btAdapter;
    private ArrayAdapter<String> newDevicesArrayAdapter;
    public Button scan;

    /**
     * Name: deviceClickListener
     * Description: On click listener for the ListViews
     */
    private AdapterView.OnItemClickListener deviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2,long arg3){
            Log.e("$$", "BluetoothScanner - deviceClickListener");

            // Cancel discovery because it's costly and we're about to connect
            btAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Create the result Intent and include the MAC address
            Intent intent = new Intent();
            intent.putExtra("device_address", address);

            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    /**
     * Name: receiver
     * Description: BroadcastReceiver that listens for discovered devices
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("$$", "BluetoothScanner - receiver");

            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(
                        BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's listed
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    // Removes the object first to prevent duplicates
                    newDevicesArrayAdapter.remove(device.getName() + "\n" +
                            device.getAddress());
                    newDevicesArrayAdapter.add(device.getName() + "\n" +
                            device.getAddress());
                }

                // When discovery is finished, change the Activity title
            } else
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                setProgressBarIndeterminateVisibility(false);
                setTitle("select a device to connect");
                if (newDevicesArrayAdapter.getCount() == 0) {
                    newDevicesArrayAdapter.add("No devices found");
                }

                // Shows scan button once adapter finishes scanning
                if (!btAdapter.isDiscovering()) {
                    scan.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    /**
     * Name: onCreate
     * Description: Creates the views for the screen and sets up various
     *              listeners for the items on the ListViews.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("$$", "BluetoothScanner - onCreate.");

        super.onCreate(savedInstanceState);

        // Creates the view on a window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.bluetooth_device_list);

        // Sets the result as canceled in case the user backs out
        setResult(Activity.RESULT_CANCELED);

        // Initializes the button to perform device discovery
        scan = (Button) findViewById(R.id.button_scan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newDevicesArrayAdapter.remove("No devices found");
                startDiscovery();
                view.setVisibility(View.GONE);
            }
        });

        // Initialize array adapters--one for paired devices, the other for
        // newly discovered
        ArrayAdapter<String> pairedArrayAdapter =
                new ArrayAdapter<>(this, R.layout.bluetooth_device_name);
        newDevicesArrayAdapter =
                new ArrayAdapter<>(this, R.layout.bluetooth_device_name);

        // Find and sets up the ListView for  paired devices
        ListView lvPairDevices = (ListView) findViewById(R.id.paired_devices);
        lvPairDevices.setAdapter(pairedArrayAdapter);
        lvPairDevices.setOnItemClickListener(deviceClickListener);

        // Find and sets up the ListView for newly discovered devices
        ListView lvNewDevices = (ListView) findViewById(R.id.new_devices);
        lvNewDevices.setAdapter(newDevicesArrayAdapter);
        lvNewDevices.setOnItemClickListener(deviceClickListener);

        // Register for broadcast when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(receiver, filter);

        // Register for broadcast when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(receiver, filter);

        // Grabs the local Bluetooth Adapter
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

        // Adds paired devices to the ArrayAdapter if available
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                pairedArrayAdapter.add(device.getName() + "\n" +
                        device.getAddress());
            }
        } else {
            String noDevices = "No devices found";
            pairedArrayAdapter.add(noDevices);
        }
    }

    /**
     * Name: onDestroy
     * Description: Called when the screen is closed/ exited
     */
    @Override
    protected void onDestroy() {
        Log.e("$$", "BluetoothScanner - onDestroy");

        super.onDestroy();

        // Ensures the device is no longer scanning
        if (btAdapter != null) {
            btAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(receiver);
    }

    /**
     * Name: startDiscovery
     * Description: Performs the scan to search for other Bluetooth devices.
     */
    private void startDiscovery() {
        Log.e("$$", "BluetoothScanner - startDiscovery");

        if (btAdapter != null) {
            // Indicate scanning in the title
            setProgressBarIndeterminateVisibility(true);
            setTitle("scanning for devices...");

            // Turn on sub-title for new devices
            findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

            // If we're already discovering, stop it
            if (btAdapter.isDiscovering()) {
                btAdapter.cancelDiscovery();
            }

            Log.e("$$", "BluetoothScanner - startDiscovery. above startDiscovery");
            btAdapter.startDiscovery();
        }
    }
}
