package net.calit2.mooc.iot_db410c.webserver;

import android.content.Context;
import android.util.Log;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Created by Ara on 10/16/15.
 */
public class WebServer extends Thread {
    public static final int PORT=8080;
    private final Context context;
    private ServerSocket serverSocket;
    private boolean running;
    public WebServer(Context context){
        this.context = context;

    }

    public void bindServer(int port){
        try {
            serverSocket=new ServerSocket(port,50,getIPAddress());
            setRunning(true);
            Log.i("hello", serverSocket.getLocalSocketAddress() + ":" + serverSocket.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        Socket client;
        while(isRunning()){

            try {
                client = serverSocket.accept();
                if(client!=null){
                }
                new ClientConnection(client,context).start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void stopServer(){
        try {
            setRunning(false);
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void startServer() {
        this.start();
    }



    /**
     * Name:        getIPAddress
     * Description: Iterates through the device's IPAddresses and returns
     *              first non-local IPAddress
     *
     * @return String containing the first non-local IPAddress of
     *          the device.
     */
    public InetAddress getIPAddress(){

        try {
            for (Enumeration<NetworkInterface> en =
                 NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements();) {

                NetworkInterface networkInterface = en.nextElement();

                for (Enumeration<InetAddress> enumIpAddr =
                     networkInterface.getInetAddresses();
                     enumIpAddr.hasMoreElements();) {

                    InetAddress inetAddress = enumIpAddr.nextElement();

                    if (!inetAddress.isLoopbackAddress()&& InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
                        return inetAddress;
                    }
                }
            }
            return InetAddress.getByName("127.0.0.1");

        } catch (SocketException ex) {
            ex.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }
}
