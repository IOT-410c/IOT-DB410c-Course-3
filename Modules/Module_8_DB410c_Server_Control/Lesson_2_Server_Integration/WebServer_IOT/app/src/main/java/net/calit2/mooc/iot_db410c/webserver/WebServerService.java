package net.calit2.mooc.iot_db410c.webserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Name:        WebServerService
 * Description: Service class to enable the application to run in the
 *              background
 */
public class WebServerService extends Service {
    WebServer server;
    /**
     * Name:        onCreate
     * Description: Creates an instance of the WebServer to run in the
     *              background
     */
    @Override
    public void onCreate() {
        server = new WebServer(this);
        server.bindServer(8080);
        server.startServer();
    }

    /**
     * Name: onBind
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy(){
        server.stopServer();
    }
}