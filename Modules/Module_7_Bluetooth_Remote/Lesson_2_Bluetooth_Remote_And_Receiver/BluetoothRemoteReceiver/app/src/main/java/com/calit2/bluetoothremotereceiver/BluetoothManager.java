package com.calit2.bluetoothremotereceiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.UUID;

/******************************************************************************
 * Name:        BluetoothManager
 * Description: Manages the Bluetooth connections with other devices. The
 *              manager has "three" Threads -- AcceptThread (to listen for
 *              incoming connections), ConnectThread (to connect with a
 *              device), and ConnectedThread (to relay information to and
 *              from the other device).
 ******************************************************************************/

public class BluetoothManager {

  // Log messages
  private static final String TAG = "BluetoothManager";

  // Name for the SDP record when creating server socket
  private static final String NAME_SECURE     = "BluetoothRemoteSecure";
  private static final String NAME_INSECURE   = "BluetoothRemoteInsecure";

  // UUID for this application
  private static final UUID UUID_SECURE   =
      UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
  private static final UUID UUID_INSECURE =
      UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

  // Constants used to enable the manager to determine cource of action to take
  public static final int IDLE        = 0; // Idle
  public static final int LISTENING   = 1; // Listening for connections
  public static final int CONNECTING  = 2; // Connecting to another device
  public static final int CONNECTED   = 3; // Connected to another device

  // private instance variables
  private final BluetoothAdapter btAdapter;
  private final Handler handler;
  private AcceptThread secureAcceptThread;
  private AcceptThread insecureAcceptThread;
  private ConnectThread connectThread;
  private ConnectedThread connectedThread;
  private int state;

  /**************************************************************************
   * Name:        BluetoothManager
   * Description: Constructor for the class. Instantiates some private
   *              instances.
   *
   * @param       handler Handler to update the UI
   **************************************************************************/
  public BluetoothManager(Handler handler) {
    btAdapter = BluetoothAdapter.getDefaultAdapter();
    state = IDLE;
    this.handler = handler;
  }

  /**************************************************************************
   * Name:        setState
   * Description: Alters the state of the BluetoothManager and
   *              sets the state variable
   *
   * @param       state An integer defining the current connection status
   **************************************************************************/
  private synchronized void setState(int state) {
    this.state = state;

    // Give the new state to the Handler so the UI Activity can update
    handler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, state, -1)
           .sendToTarget();
  }

  /**************************************************************************
   * Name:        getState
   * Description: Getter method that returns the value of the private
   *              instance state
   *
   * @return      int of the private instance state
   **************************************************************************/
  public synchronized int getState() { return state; }

  /**************************************************************************
   * Name:        Listen
   * Description: Cancels the current Threads running and begins listening
   *              for other devices by starting the AcceptThreads
   **************************************************************************/
  public synchronized void listen() {
    // Cancel currently running Threads
    cancelConnectThread();
    cancelConnectedThread();

    setState(LISTENING);

    // Begin listening for other devices
    if (secureAcceptThread == null) {
      secureAcceptThread = new AcceptThread(true);
      secureAcceptThread.start();
    }
    if (insecureAcceptThread == null) {
      insecureAcceptThread = new AcceptThread(false);
      insecureAcceptThread.start();
    }
  }

  /**************************************************************************
   * Name:        connect
   * Description: Instantiates the ConnectThread and initiate a connection
   *              to another Bluetooth device.
   *
   * @param       device BluetoothDevice the manager is attempting to connect
   *              to
   * @param       secure Boolnea indicate the security type of the Bluetooth
   *              socket
   **************************************************************************/
  public synchronized void connect(BluetoothDevice device, boolean secure) {
    // Cancel any thread attempting to make a connection
    if (state == CONNECTING) {
      cancelConnectThread();
    }

    // Cancel any thread currently running a connection
    cancelConnectedThread();

    // Instantiate ConnectThread and begin connecting with external device
    connectThread = new ConnectThread(device, secure);
    connectThread.start();
    setState(CONNECTING);
  }

  /**************************************************************************
   * Name:        Connected
   * Description: Instantiates the ConnectedThread and begin managing the
   *              Bluetooth connection between the two devices
   *
   * @param       socket BluetoothSocket which a connection was established
   * @param       device BluetoothDevice we are connected to
   * @param       socketType String containing SocketType (for debugging)
   **************************************************************************/
  public synchronized void connected(BluetoothSocket socket, BluetoothDevice
      device, final String socketType) {
    // Cancel all of the current threads
    cancelConnectThread();
    cancelConnectedThread();
    cancelAcceptThreads();

    // Instantiate the ConnectedThread to manage the connection
    connectedThread = new ConnectedThread(socket, socketType);
    connectedThread.start();

    // Send the name of the connected device back to the UI Activity
    Message msg = handler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
    Bundle bundle = new Bundle();
    bundle.putString(Constants.DEVICE_NAME, device.getName());
    msg.setData(bundle);
    handler.sendMessage(msg);

    setState(CONNECTED);
  }


  /**************************************************************************
   * Name:        stop
   * Description: Cancels/stops all of the Threads and reverts the Manager
   *              to the IDLE state
   **************************************************************************/
  public synchronized void stop() {
    cancelConnectThread();
    cancelConnectedThread();
    cancelAcceptThreads();
    setState(IDLE);
  }

  /**************************************************************************
   * Name:        write
   * Description: write to the ConnectedThread using its write method
   **************************************************************************/
  public void write(byte[] out) {
    ConnectedThread temp;
    // sychronize a copy of the connectedThread
    synchronized (this) {
      if (state != CONNECTED) return;
      temp = connectedThread;
    }

    // Perform the unsynchronized write
    temp.write(out);
  }

  /**************************************************************************
   * Name:        connectionFailed
   * Description: Update the UI to notify the user of a failed connection
   **************************************************************************/
  private void connectionFailed() {
    // Send a failure message back to the Activity
    Message msg = handler.obtainMessage(Constants.MESSAGE_TOAST);
    Bundle bundle = new Bundle();
    bundle.putString(Constants.TOAST, "Unable to connect device");
    msg.setData(bundle);
    handler.sendMessage(msg);

    // Start the service over in listening mode
    BluetoothManager.this.listen();
  }

  /**************************************************************************
   * Name:        connectionLost
   * Description: Update the UI to notify the user of a lost connection
   **************************************************************************/
  private void connectionLost() {
    // Send a failure message back to the Activity
    Message msg = handler.obtainMessage(Constants.MESSAGE_TOAST);
    Bundle bundle = new Bundle();
    bundle.putString(Constants.TOAST, "Device connection was lost");
    msg.setData(bundle);
    handler.sendMessage(msg);

    // Start the service over in listening mode
    BluetoothManager.this.listen();
  }

  /**************************************************************************
   * Name:        cancelConnectedThread
   * Description: Helper method to cancel the ConnectedThread if running
   **************************************************************************/
  private void cancelConnectedThread() {
    if (connectedThread != null) {
      connectedThread.cancel();
      connectedThread = null;
    }
  }

  /**************************************************************************
   * Name:        cancelAcceptThreads
   * Description: Helper method to cancel both the AcceptThreads if running
   **************************************************************************/
  private void cancelAcceptThreads() {
    if (secureAcceptThread != null) {
      secureAcceptThread.cancel();
      secureAcceptThread = null;
    }
    if (insecureAcceptThread != null) {
      insecureAcceptThread.cancel();
      insecureAcceptThread = null;
    }
  }

  /**************************************************************************
   * Name:        cancelConnectThread
   * Description: Helper method to cancel the ConnectThread if running
   **************************************************************************/
  private void cancelConnectThread() {
    if (connectThread != null) {
      connectThread.cancel();
      connectThread = null;
    }
  }

  /**************************************************************************
   * Name:        AcceptThread
   * Description: The Thread that continuously runs and listens for incoming
   *              connections. This Thread will continue to run until a
   *              connection is accepted/cancelled
   **************************************************************************/
  private class AcceptThread extends Thread {
    // Local server socket
    private final BluetoothServerSocket mmServerSocket;
    private String mSocketType;

    /**********************************************************************
     * Name:        AcceptThread
     * Description: Constructor for the class. Attempts to instantiate the
     *              ServerSocket based on the passed parameter
     *
     * @param       secure boolean indicating the type of connection to
     *              listen for
     **********************************************************************/
    public AcceptThread(boolean secure) {
      BluetoothServerSocket tmp = null;
      mSocketType = secure ? "Secure" : "Insecure";

      // Create a new listening server socket
      try {
        if (secure)
          tmp = btAdapter.listenUsingRfcommWithServiceRecord(
              NAME_SECURE, UUID_SECURE);
        else
          tmp = btAdapter.listenUsingInsecureRfcommWithServiceRecord(
              NAME_INSECURE, UUID_INSECURE);
      } catch (IOException e) {
        e.printStackTrace();
      }
      mmServerSocket = tmp;
    }

    /**********************************************************************
     * Name:        run
     * Description: The AcceptThread will continuously attempt to connect
     *              to a different device. When a connection was accepted,
     *              the AcceptThread delegates to the appripriate methods
     **********************************************************************/
    public void run() {
      setName("AcceptThread" + mSocketType);

      BluetoothSocket socket;

      // listen to the server socket if we're not connected
      while (state != CONNECTED) {
        try {
          socket = mmServerSocket.accept();
        } catch (IOException e) {
          e.printStackTrace();
          break;
        }

        // If connection was accepted
        if (socket != null) {
          synchronized (BluetoothManager.this) {
            switch (state) {
              case LISTENING:
              case CONNECTING:
                // Situation normal. Start connected thread.
                connected(socket, socket.getRemoteDevice(), mSocketType);
                break;
              case IDLE:
              case CONNECTED:
                // Not ready/ not already connected.
                try {
                  socket.close();
                } catch (IOException e) {
                  e.printStackTrace();
                }
                break;
            }
          }
        }
      }
    }

    /**********************************************************************
     * Name:        cancel
     * Description: Closes the server socket to indicate that the Thread is
     *              no longer listening
     **********************************************************************/
    public void cancel() {
      try {
        mmServerSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**************************************************************************
   * Name:        ConnectThread
   * Description: This thread runs when the application is attempting to make
   *              a connection with a device. The connection either succeeds
   *              or fails
   **************************************************************************/
  private class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private String mSocketType;

    /**********************************************************************
     * Name:        ConnectThread
     * Description: Constructor for this class. Instantiates the private
     *              instances and attempts to connect the device
     *
     * @param       device BluetoothDevice currently attempting to connect
     *              to
     * @param       secure boolean indicating the type of connection
     **********************************************************************/
    public ConnectThread(BluetoothDevice device, boolean secure) {
      Log.e(TAG, "CONNECTTHREAD");
      mmDevice = device;
      BluetoothSocket tmp = null;
      mSocketType = secure ? "Secure" : "Insecure";

      // Get a BluetoothSocket for a connection with the
      // given BluetoothDevice
      try {
        if (secure)
          tmp = device.createRfcommSocketToServiceRecord(
              UUID_SECURE);
        else
          tmp = device.createInsecureRfcommSocketToServiceRecord(
              UUID_INSECURE);
      } catch (IOException e) {
        e.printStackTrace();
      }
      mmSocket = tmp;
    }

    /**********************************************************************
     * Name:        run
     * Description: Performs an attempt to connect to the Bluetooth Device.
     *              If it succeeds, it moves on to established a connect
     *              (i.e begins the ConnectedThread).
     **********************************************************************/
    public void run() {
      setName("ConnectThread" + mSocketType);

      //Always cancel discovery because it will slow down a connection
      btAdapter.cancelDiscovery();

      // Make a connection to the bluetoothSocket
      try {
        // This is a blocking call and will only return on a
        // successful connection or an exception
        mmSocket.connect();
      } catch (IOException e) {
        e.printStackTrace();
        // Close the socket
        try {
          mmSocket.close();
        } catch (IOException e2) {
          e2.printStackTrace();
        }
        connectionFailed();
        return;
      }

      // Reset the ConnectThread because we're done
      synchronized (BluetoothManager.this) {
        connectThread = null;
      }

      // Start the connected thread
      connected(mmSocket, mmDevice, mSocketType);
    }

    /**********************************************************************
     * Name:        cancel
     * Description: cancels/stops the current Thread by closing its socket
     **********************************************************************/
    public void cancel() {
      try {
        mmSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**************************************************************************
   * Name:        ConnectedThread
   * Description: Thread that continuously runs when the current device is
   *              connected with a remote device via Bluetooth. THis Thread
   *              handles the communication between the two devices.
   **************************************************************************/
  private class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;

    /**********************************************************************
     * Name:        ConnectedThread
     * Description: Constructor for the class. Attempts to instantiate the
     *              input and output streams to enable communication
     *              between the two devices.
     **********************************************************************/
    public ConnectedThread(BluetoothSocket socket, String socketType) {
      Log.e(TAG, "CONNECTEDTHREAD");
      mmSocket = socket;
      InputStream tmpIn = null;
      OutputStream tmpOut = null;

      try {
        tmpIn = socket.getInputStream();
        tmpOut = socket.getOutputStream();
      } catch (IOException e) {
        e.printStackTrace();
      }

      mmInStream = tmpIn;
      mmOutStream = tmpOut;
    }

    /**********************************************************************
     * Name:        run
     * Description: Continuously listens for input from the InputStream or
     *              other deivce. When an input is received, we use the
     *              GPIOInputParser to perform the necessary GPIO
     *              manipulation/changes
     **********************************************************************/
    public void run() {
      byte[] buffer = new byte[1024];
      int bytes;

      // keep listening to the inputstream while connected
      while (true) {
        try {
          // read from the inputstream
          bytes = mmInStream.read(buffer);

          String parser = new String(buffer).split("0")[0];
          Log.e(TAG, "parser " + parser);
          boolean result = false;
          try {
            result = GpioInputParser.parseMessage(parser);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }

          if (result) {
            // Send the obtained bytes to the UI Activity
            handler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer).
                sendToTarget();
          } else {
            // Send error message to UI Activity
            buffer = "Unrecognized Message".getBytes();
            bytes = buffer.length;
            handler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer).
                sendToTarget();
          }
        } catch (IOException e) {
          e.printStackTrace();
          connectionLost();
          // Start the service over to restart listening mode
          BluetoothManager.this.listen();
          break;
        }
      }
    }

    /**********************************************************************
     * Name:        write
     * Description: write the message in the buffer to the OutputStream
     *
     * @param       buffer byte array containing the message to be sent
     **********************************************************************/
    public void write(byte[] buffer) {
      try {
        mmOutStream.write(buffer);

        // share the sent message back to the UI activity
        handler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer).
            sendToTarget();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    /**********************************************************************
     * Name:        cancel
     * Description: cancels/stops the current Thread by closing its socket
     **********************************************************************/
    public void cancel() {
      try {
        mmSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}