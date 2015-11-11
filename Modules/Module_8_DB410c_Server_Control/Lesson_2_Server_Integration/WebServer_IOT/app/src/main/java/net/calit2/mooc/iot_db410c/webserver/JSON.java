package net.calit2.mooc.iot_db410c.webserver;

import android.content.Context;

import org.json.*;

import java.util.ArrayList;

/**
 * Name:        JSON
 * Description: JSON creates a JSONObject that includes SystemInformation
 *              about the device. (See SystemInfo for the parsed information).
 */
public class JSON {
    private SystemInfo info;
    private Context context;

    /**
     * Name:        JSON
     * Description: Constructor for the class.
     *
     * @param context  WebServerService class passed into the private instance
     *                 SystemInfo to enable it to pull information regarding
     *                 the Android device. Additionally, it is used to grab
     *                 strings from strings.xml
     */
    public JSON(Context context) {
        info = new SystemInfo(context);
        this.context = context;
    }

    /**
     * Name:        createJSON
     * Description: Creates a JSONObject that includes information regarding
     *              the current device. This method uses the SystemInfo class
     *              to gather data.
     *
     * @return      String containing the JSONObject with the SystemInfo
     *              information
     */
    public JSONObject createJSON() {
        JSONObject json = new JSONObject();

        try {
            // Computation
            JSONObject computation = new JSONObject();

            computation.put(context.getString(R.string.cores),
                    info.getNumCores());

            computation.put(context.getString(R.string.cpu_usage),
                    info.getCpuUsage() + "%");

            computation.put(context.getString(R.string.mem_usage),
                    info.getMemoryUsage() + "%");

            // Connection
            JSONObject network = new JSONObject();


            network.put(context.getString(R.string.bluetooth),
                    info.getBluetoothStatus());

            network.put(context.getString(R.string.location_service),
                    info.getLocationStatus());

            JSONObject wifi = new JSONObject();
            wifi.put(context.getString(R.string.ssid), info.getWifiNetwork());

            wifi.put(context.getString(R.string.status),
                    info.getWifiStatus());

            network.put(context.getString(R.string.wifi), wifi);

            // Peripherals
            JSONObject peripherals = new JSONObject();

            JSONArray usb = new JSONArray();
            String [] usbList = info.getUSBList();

            for (String device : usbList) {
                JSONObject temp = new JSONObject();
                temp.put(context.getString(R.string.device), device);
                usb.put(temp);
            }

            peripherals.put(context.getString(R.string.usb), usb);

            // GPIO
            JSONArray gpio = new JSONArray();

            ArrayList<String> direction = info.getGPIODirection();
            ArrayList<Integer> values = info.getGPIOValues();

            // Values are 23 and 34 to account for the GPIO Numbers
            for (int i = 23; i < 34; i++) {
                JSONObject temp = new JSONObject();

                if (i == 30) {
                    continue;
                }

                temp.put(context.getString(R.string.gpio_number), i);
                temp.put(context.getString(R.string.value),
                        values.get(i - 23));
                temp.put(context.getString(R.string.direction),
                        direction.get(i - 23));
                gpio.put(temp);
            }

            // Combine Everything
            JSONObject status = new JSONObject();

            status.put(context.getString(R.string.computation), computation);
            status.put(context.getString(R.string.networks), network);
            status.put(context.getString(R.string.gpio), gpio);
            status.put(context.getString(R.string.peripherals), peripherals);
            status.put(context.getString(R.string.last_update),
                    info.getLastUpdate());

            json.put(context.getString(R.string.status), status);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}

/**
 * Format of JSON
 * {
 *     "Status" : {
 *          "Computation" : {
 *              "Number of Cores" : Number,
 *              "CPU Usage" : Percentage,
 *              "Memory Usage" : Percentage
 *          },
 *
 *          "Networks : {
 *              "Bluetooth" : True/ False,
 *              "Location Services" : True/ False
 *              "Wifi" : {
 *                  "SSID" : "Name",
 *                  "Status" : True/ False
 *              },
 *          },
 *
 *          "GPIO" : {
 *              [GPIO Number : #,
 *               Value : #,
 *               Direction : In/Out
 *              ],
 *              [GPIO Number : #
 *               Value : #,
 *               Direction : In/Out
 *              ]
 *              ...
 *          },
 *
 *          "Peripherals" : {
 *              "USB" : {
 *                  ["Device" : Name of Device],
 *                  ["Device" : Name of Device],
 *                  ...
 *              }
 *          },
 *
 *          "Last Update" : {
 *              mm/dd/yyyy hh:mm:ss a
 *          }
 *     }
 * }
 */