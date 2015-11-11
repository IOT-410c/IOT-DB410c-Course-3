package calit2.bluetoothremotereceiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Name: BluetoothManager
 * Description:
 */
public class BluetoothManager {

    private static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private static final String NAME = "Dragonboard 410c Remote App";

    // Constants for the Connection state of the app
    private static final int IDLE       = 0;
    private static final int LISTENING  = 1;
    private static final int CONNECTING = 2;
    private static final int CONNECTED  = 3;

    // Constants for Reading Messages
    private final BluetoothAdapter btAdapter;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private AcceptThread acceptThread;
    private Handler handler;
    private int state;

    /**
     * Name: BluetoothManager
     * Description: Default constructor. Creates a BluetoothAdapter
     */
    public BluetoothManager(Handler handler) {
        Log.e("$$", "BluetoothManager - BluetoothManager Constructor");
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        state = IDLE;
        this.handler = handler;
    }

    /**
     * Name: setState
     * Description:
     */
    private synchronized void setState(int status) {
        state = status;
    }

    /**
     * Name: start
     * Description:
     */
    public synchronized void start() {
        Log.e("$$", "BluetoothManager - BluetoothManager start");

        // Cancel any currently running threads
        // Cancels previous connections if available
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        // Begins the AcceptThread to listen for connections
        if (acceptThread == null) {
            acceptThread = new AcceptThread();
            acceptThread.start();
        }

        setState(LISTENING);
    }

    /**
     * Name: connect
     * Description:
     */
    public synchronized void connect(BluetoothDevice btDevice) {
        Log.e("$$", "BluetoothManager - BluetoothManager connect. Bt Device = " + btDevice.getName());

        // Cancels previous connections if available
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        connectThread = new ConnectThread(btDevice);
        connectThread.start();
        setState(CONNECTING);
    }

    /**
     * Name: connected
     * Description:
     */
    public synchronized void connected(BluetoothSocket socket) {
        Log.e("$$", "BluetoothManager - BluetoothManager connected. Socket = " + socket);

        // Cancels previous connections if available
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }

        connectedThread = new ConnectedThread(socket);
        connectedThread.start();
        setState(CONNECTED);
    }

    /**
     * Name: stop
     * Description:
     */
    public synchronized void stop() {
        Log.e("$$", "BluetoothManager - BluetoothManager stop");
        // Cancels all of the threads
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }

        setState(IDLE);
    }

    /**
     * Name: ConnectThread
     * Description:
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket socket;

        public ConnectThread (BluetoothDevice device) {
            Log.e("$$", "BluetoothManager - ConnectThread constructor.");

            BluetoothSocket temp = null;

            // Get a BluetoothSocket for a connection to given BluetoothDevice
            try {
                temp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = temp;

            Log.e("$$", "BluetoothManager - ConnectThread end of Constructor.\n" + socket);
        }

        /**
         * Name: run
         * Description:
         */
        public void run() {
            Log.e("$$", "BluetoothManager - ConnectThread run.");

            // Stops the device from searching to improve connection
            btAdapter.cancelDiscovery();

            try {
                socket.connect();
            } catch (IOException e) {
                Log.e("$$", "BluetoothManager - ConnectThread; Failed to connect");
                try {
                    socket.close();
                    Log.e("$$", "BluetoothManager - ConnectThread; socket closed. e = " + e);
                } catch (IOException e1) {
                    e1.printStackTrace();
                    Log.e("$$", "BluetoothManager - ConnectedThread run error e1 = " + e1);
                }
                // Connection failed
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothManager.this) {
                connectedThread = null;
            }

            connected(socket);
        }

        /**
         * Name: cancel
         * Description:
         */
        public void cancel() {
            Log.e("$$", "BluetoothManager - ConnectThread cancel. Socket closed.");

            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Name: ConnectedThread
     * Description:
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket socket;
        private final InputStream in;

        /**
         * Name: ConnectedThread
         * Description:
         */
        public ConnectedThread(BluetoothSocket socket) {
            Log.e("$$", "BluetoothManager - ConnectedThread constructor.");

            InputStream tmpIn = null;
            this.socket = socket;

            // Gets the socket's OutputStream
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            in = tmpIn;
        }

        /**
         * Name: run
         * Description:
         */
        public void run() {
            Log.e("$$", "BluetoothManager - ConnectedThread on run.");

            byte[] buffer = new byte[1024]; // 1024 is probably too much
            int bytes;

            while (true) {
                try {
                    bytes = in.read(buffer);

                    handler.obtainMessage(0, 0, bytes, buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Name: cancel
         * Description:
         */
        public void cancel() {
            Log.e("$$", "BluetoothManager - ConnectedThread cancel. Socket close");

            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Name: AcceptThread
     * Description:
     */
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket serverSocket;

        /**
         * Name: AcceptThread
         * Description: Default Constructor for the AcceptThread
         */
        public AcceptThread() {
            Log.e("$$", "BluetoothManager - AcceptThread Constructor");

            BluetoothServerSocket tmp = null;

            // Creates a listening server socket
            try {
                tmp = btAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }

            serverSocket = tmp;
        }

        /**
         * Name: run
         * Description:
         */
        public void run() {
            Log.e("$$", "BluetoothManager - AcceptThread run");

            BluetoothSocket socket = null;

            // Attempt to find a connection while not connected
            while (state != CONNECTED) {
                //
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (socket != null) {
                    switch (state) {
                        case CONNECTING:
                            connected(socket);
                            break;
                        case CONNECTED:
                            try {
                                socket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }
            }
            Log.e("$$", "BluetoothManager - AcceptThread run | end");
        }

        /**
         * Name: cancel
         * Description:
         */
        public void cancel() {
            Log.e("$$", "BluetoothManager - AcceptThread cancel");
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
