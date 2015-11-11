package net.calit2.mooc.iot_db410c.webserver;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import net.calit2.mooc.iot_db410c.db410c_gpiolib.GpioProcessor;

/**
 * Name:        SystemInfo
 * Description: This class pulls system information (e.g. Bluetooth, WiFi,
 *              GPIO, etc.) from the device.
 */
public class SystemInfo {
    // Sleep time for the calculation of CPU Usage in milliseconds
    private static final int THREAD_SLEEP = 300;

    // Multiplier to convert CPU and Memory Usage into percentages
    private static final int PERCENTAGE   = 100;

    private Context context;

    /**
     * Name:        SystemInfo
     * Description: Constructor for class
     *
     * @param context  WebServerService object to allow SystemInfo to pull
     *                 System Information (e.g. WiFi, Bluetooth,
     *                 LocationServices, etc.) from the android device.
     */
    public SystemInfo(Context context) {
        this.context = context;
    }

    /**
     * Name:        getGPIOValues
     * Description: Creates a GpioProcessor to gather the Values of all of
     *              the GPIO pins
     *
     * @return      ArrayList<Integer> of values for each GPIO Pin
     */
    ArrayList<Integer> getGPIOValues() {
        GpioProcessor gpioProcessor = new GpioProcessor();

        ArrayList<Integer> value = new ArrayList<>();

        GpioProcessor.Gpio[] allPins = gpioProcessor.getAllPins();

        for(GpioProcessor.Gpio gpio : allPins) {
            value.add(gpio.getValue());
        }

        return value;
    }

    /**
     * Name:        getGPIODirection
     * Description: Creates a GpioProcessor to gather the Direction of
     *              all the pins
     *
     * @return      ArrayList<String> of the Direction of each GPIO Pin
     */
    ArrayList<String> getGPIODirection() {
        GpioProcessor gpioProcessor = new GpioProcessor();

        ArrayList<String> direction = new ArrayList<>();

        GpioProcessor.Gpio[] allPins = gpioProcessor.getAllPins();

        for (GpioProcessor.Gpio gpio : allPins) {
            direction.add(gpio.getDirection());
        }

        return direction;
    }

    /**
     * Name:        getBluetoothStatus
     * Description: Creates a BluetoothAdapter to determine if
     *              the Bluetooth on the device is on
     *
     * @return      Boolean signaling whether or not the device's Bluetooth
     *              is on
     */
    boolean getBluetoothStatus() {
        BluetoothAdapter bluetoothAdapter =
                BluetoothAdapter.getDefaultAdapter();

        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    /**
     * Name:        getLocationStatus
     * Description: Creates a LocationManager and returns the status of the
     *              location provider
     *
     * @return      boolean signaling whether or not the device's Location
     *              services are on
     */
    boolean getLocationStatus() {
        LocationManager manager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager != null &&
                (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    /**
     * Name:        getWifiNetwork
     * Description: Returns the SSID of the Wifi if it is enabled/ available
     *
     * @return      String with the SSD if available
     */
    String getWifiNetwork() {
        WifiManager wifiManager =
                (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        if (wifiManager.isWifiEnabled()) {
            String ssid = wifiInfo.getSSID();

            if (!ssid.isEmpty()) {
                return wifiInfo.getSSID();
            } else {
                return context.getString(R.string.wifi_enabled_not_connected);
            }
        } else {
            return context.getString(R.string.wifi_disabled);
        }
    }

    /**
     * Name:        getWifiStatus
     * Description: Returns the wifi status of the device
     *
     * @return      Boolean indicating whether or not the Wifi is turned on
     */
    boolean getWifiStatus() {
        WifiManager wifiManager =
                (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        return wifiManager.isWifiEnabled();
    }

    /**
     * Name:        getUSBStatus
     * Description: Determines the name of the USB devices currently attached
     *
     * @return      String array of the names of the USB Devices attached
     */
    String [] getUSBList() {
        UsbManager manager =
                (UsbManager) context.getSystemService(Context.USB_SERVICE);

        HashMap<String, UsbDevice> deviceMap = manager.getDeviceList();
        String [] deviceList = new String [deviceMap.size()];

        int i = 0;
        for (UsbDevice devices : deviceMap.values()) {
            deviceList[i] = deviceMap.get(devices).getDeviceName();
            i++;
        }

        return deviceList;
    }

    /**
     * Name:        getNumCores
     * Description: Uses the Runtime Environment to get the working
     *              processors.
     *
     * @return      The number of cores the device's processor has
     */
    int getNumCores() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * Name:        getMemoryUsage
     * Description: Calculates the percentage of available RAM/ Memory Usage
     *              on the device
     *
     * @return      Double representing percentage of Memory Usage
     */
    String getMemoryUsage() {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();

        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.getMemoryInfo(memoryInfo);

        return context.getString(R.string.percentage,
                ((float) memoryInfo.availMem / memoryInfo.totalMem * PERCENTAGE));
    }

    /**
     * Name:        getCpuUsage
     * Description: Calculates the CPU Usage of the current device
     *
     * @return      Percentage of CPU Usage of the device as a double
     */
    String getCpuUsage() {
        try {
            // Allows reading from the /proc/stat file
            RandomAccessFile reader =
                    new RandomAccessFile(context.getString(R.string.cpu_file),
                            context.getString(R.string.read_only));
            String load = reader.readLine();

            ArrayList<Long> cpu  = new ArrayList<>();
            ArrayList<Long> idle = new ArrayList<>();

            for (int i = 0; i < 2; i ++) {
                long [] temp = parseCpu(load);

                idle.add(temp[0]);
                cpu.add(temp[1]);

                try {
                    Thread.sleep(THREAD_SLEEP);
                } catch (InterruptedException ignored) {}

                if (i == 0) {
                    reader.seek(0);
                    load = reader.readLine();
                    reader.close();
                }
            }

            return context.getString(R.string.percentage,
                    ((float) (cpu.get(1) - cpu.get(0)) /
                            ((cpu.get(1) + idle.get(1)) -
                                    (cpu.get(0) + idle.get(0))) * PERCENTAGE));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Name:        parseCpu
     * Description: Separates the idle and used CPU into an array.
     *
     * @param load  is a String read from /proc/stat that contains the
     *              information regarding the CPU Usage of the device
     * @return      A long array housing the idle cpu (at index 0) and the used
     *              cpu (at index 1)
     */
    long [] parseCpu(String load) {
        String [] token = load.split(" +"); // Splits load on one or more spaces
        long [] temp = new long[2];

        // Calculates the idle CPU
        temp[0] = Long.parseLong(token[4]);

        // Calculates the non-idle CPU
        temp[1] = Long.parseLong(token[2]) + Long.parseLong(token[3]) +
                Long.parseLong(token[5]) + Long.parseLong(token[6]) +
                Long.parseLong(token[7]) + Long.parseLong(token[8]);
        return temp;
    }

    /**
     * Name:        getLastUpdate
     * Description: Determines the most current update/ call for the SystemInfo
     *
     * @return      String with a date and time formatted as "mm/dd/yyyy
     *              hh:mm:ss a"
     */
    String getLastUpdate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat df = new SimpleDateFormat("mm/dd/yyyy hh:mm:ss a");
        return df.format(date);
    }
}