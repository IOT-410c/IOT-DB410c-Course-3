package com.calit2.bluetoothremotereceiver;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/******************************************************************************
 * Name:        BluetoothRemoteFragment
 * Description: Allows device to communicate with the other devices.
 *              This is the Receiver.
 *****************************************************************************/
public class BluetoothRemoteFragment extends Fragment {

  // Log Message
  private static final String TAG = "BluetoothRemoteFragment";

  // Intent Request Codes
  private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
  private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
  private static final int REQUEST_ENABLE_BT = 3;

  // BluetoothAdapter in charge of Bluetooth Scanning
  private BluetoothAdapter btAdapter;

  // BluetoothManager to Handle Socket/ Server Socket connections
  private BluetoothManager btManager;

  // TextView to display messages
  private TextView receivedMessage;

  /****************************************************************************
   * Name:        onCreate
   * Description: Instantiates the BluetoothAdapter
   *
   * @param       savedInstanceState Reference to Bundle object. Fragment
   *                                 can be restored to a former state using
   *                                 data saved in this bundle.
   ***************************************************************************/
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);

    // Get local Bluetooth Adapter
    btAdapter = BluetoothAdapter.getDefaultAdapter();

    // Determines if Bluetooth is supported by Device
    if (btAdapter == null) {
      FragmentActivity activity = getActivity();
      Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
    }
  }

  /****************************************************************************
   * Name:        newInstance
   * Description: Instantiates a new BluetoothReceiverFragment object
   ***************************************************************************/
  public static BluetoothRemoteFragment newInstance() {
    BluetoothRemoteFragment fragment = new BluetoothRemoteFragment();

    // If you need to pass anything to the fragment, set the arguments here

    return fragment;
  }

  /****************************************************************************
   * Name:        onStart
   * Description: Called at the start of the Fragment. This method will attempt
   *              to turn Bluetooth on if not enabled, else will call
   *              setupConnection()
   ***************************************************************************/
  @Override
  public void onStart() {
    super.onStart();

    // Request to enable Bluetooth if off
    if (!btAdapter.isEnabled()) {
      requestBluetooth();
    } else if (btManager == null) {
      setupConnection();
    }
  }

  /****************************************************************************
   * Name:        onDestroy
   * Description: Halts the BluetoothManager/ Stops the Threads
   ***************************************************************************/
  @Override
  public void onDestroy() {
    super.onDestroy();
    if (btManager != null) {
      btManager.stop();
    }
  }

  /****************************************************************************
   * Name:        onResume
   * Description: Lets the BluetoothManager know to begin listening
   ***************************************************************************/
  @Override
  public void onResume() {
    super.onResume();

    if (btManager != null) {
      // Tells BluetoothManager to begin listening for connections
      if (btManager.getState() == BluetoothManager.IDLE) {
        btManager.listen();
      }
    }
  }

  /****************************************************************************
   * Name:        onCreateView
   * Description: Inflates the current view with the fragment_remote layout
   *
   * @param       inflater LayoutInflater used to inflate/ create the view
   *                       for the Bluetooth Remote
   * @param       container ViewGroup that will be populated with layout
   * @param       savedInstanceState Reference to a bundle object
   ***************************************************************************/
  @Override
  public View onCreateView(LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_remote, container, false);
  }

  /****************************************************************************
   * Name:        onViewCreated
   * Description: Attaches the private instance TextView to its corresponding
   *              xml
   *
   * @param       view View containing the TextViews and other objects in the
   *                   layout
   * @param       savedInstanceState Reference to a bundle object
   ***************************************************************************/
  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    view.findViewById(R.id.yellow_on).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendMessage("yellow_on0");
      }
    });
    view.findViewById(R.id.red_on).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendMessage("red_on0");
      }
    });
    view.findViewById(R.id.green_on).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendMessage("green_on0");
      }
    });
    view.findViewById(R.id.yellow_off).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendMessage("yellow_off0");
      }
    });
    view.findViewById(R.id.red_off).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendMessage("red_off0");
      }
    });
    view.findViewById(R.id.green_off).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendMessage("green_off0");
      }
    });
  }

  /****************************************************************************
   * Name:        requestBluetooth
   * Description: Starts an Activity to request the User to enable Bluetooth
   ***************************************************************************/
  private void requestBluetooth() {
    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
  }

  /****************************************************************************
   * Name:        setupConnection
   * Description: Instantiates the BluetoothManager so that the Remote can
   *              begin listening/ making connections
   ***************************************************************************/
  private void setupConnection() {
    // Initialize the BluetoothManager to perform Bluetooth Connections
    btManager = new BluetoothManager(mHandler);
  }

  /****************************************************************************
   * Name:        ensureDiscoverable
   * Description: Starts an intent to make the application discoverable via
   *              Bluetooth
   ***************************************************************************/
  private void ensureDiscoberable(){
    if(btAdapter.getScanMode() !=
        BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
      Intent discoverIntent =
          new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
      discoverIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
      startActivity(discoverIntent);
    }
  }

  /****************************************************************************
   * Name:        sendMessage
   * Description: Delegates the BluetoothManager to write/ send the message
   *              after performing some checks
   *
   * @param       message String containing the message to send over
   ***************************************************************************/
  private void sendMessage(String message) {
    // Ensure device is connected
    if (btManager.getState() != BluetoothManager.CONNECTED) {
      Toast.makeText(getActivity(), "You are not connected to a device.",
                     Toast.LENGTH_SHORT).show();
    }

    byte[] send = message.getBytes();
    btManager.write(send);
  }

  /****************************************************************************
   * Name:        setStatus
   * Description: Updates the status on the Action Bar
   *
   * @param       resId a int referencing a resource ID
   ***************************************************************************/
  private void setStatus(int resId) {
    FragmentActivity activity = getActivity();
    if (activity == null) {
      return;
    }

    final ActionBar actionBar = activity.getActionBar();

    if (actionBar == null) {
      return;
    }

    actionBar.setSubtitle(resId);
  }

  /****************************************************************************
   * Name:        onCreateView
   * Description: Handler that listens to information/ updates from the
   *              BluetoothManager, and appropriately updates the UI
   ***************************************************************************/
  private final Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case Constants.MESSAGE_STATE_CHANGE:
          switch (msg.arg1) {
            case BluetoothManager.CONNECTED:
              setStatus(R.string.title_connected_to);
              break;
            case BluetoothManager.CONNECTING:
              setStatus(R.string.title_connecting);
              break;
            case BluetoothManager.LISTENING:
            case BluetoothManager.IDLE:
              setStatus(R.string.not_connected);
              break;
          }
          break;
        case Constants.MESSAGE_DEVICE_NAME:
          String connectedDeviceName = msg.getData().getString(Constants
                                                                   .DEVICE_NAME);
          break;
        case Constants.MESSAGE_READ:
          byte[] readBuf = (byte[]) msg.obj;
          String readMessage = new String(readBuf, 0, msg.arg1);
          receivedMessage.setText(readMessage);
          break;
      }
    }
  };

  /****************************************************************************
   * Name:        onActivityResult
   * Description: Determines the course of action to take after an Activity
   *              returns
   *
   * @param       requestCode int pertaining to a requested Activity
   * @param       resultCode  int containing the result of requested Activity
   * @param       data        Intent object containing information/ data from
   *                          Activity
   ***************************************************************************/
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case REQUEST_CONNECT_DEVICE_SECURE:
        if (resultCode == Activity.RESULT_OK) {
          connectDevice(data, true);
        }
        break;
      case REQUEST_CONNECT_DEVICE_INSECURE:
        if (resultCode == Activity.RESULT_OK) {
          connectDevice(data, false);
        }
        break;
      case REQUEST_ENABLE_BT:
        if (resultCode == Activity.RESULT_OK) {
          setupConnection();
        } else {
          Toast.makeText(getActivity(),
                         "Bluetooth was not enabled.", Toast.LENGTH_SHORT)
               .show();
        }
    }
  }

  /****************************************************************************
   * Name:        connectDevice
   * Description: Establish connection with the other device after parsing the
   *              information with the BluetoothAdapter
   *
   * @param       data Intent with DeviceListActivity.EXTRA_DEVICE_ADDRESS
   * @param       secure boolean indication the type of connection
   ***************************************************************************/
  private void connectDevice(Intent data, boolean secure) {
    String address = data.getExtras()
                         .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
    BluetoothDevice device = btAdapter.getRemoteDevice(address);
    btManager.connect(device, secure);
  }

  /****************************************************************************
   * Name:        onCreateOptionsMenu
   * Description: Inflates the menu with menu xml
   *
   * @param       menu Menu that will be inflated with the xml
   * @param       inflater MenuInflater to inflate the menu
   ***************************************************************************/
  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.menu, menu);
  }

  /****************************************************************************
   * Name:        onOptionsItemSelected
   * Description: Determines the course of action to take When the user clicks
   *              on one of the items on the menu
   *
   * @param       item MenuItem from menu
   ***************************************************************************/
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.secure_connect_scan: {
        Intent serverIntent = new Intent(getActivity(), DeviceListActivity
            .class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
        return true;
      }
      case R.id.insecure_connect_scan: {
        Intent serverIntent = new Intent(getActivity(), DeviceListActivity
            .class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
        return true;
      }
      case R.id.discoverable: {
        ensureDiscoberable();
        return true;
      }
    }
    return false;
  }
}
