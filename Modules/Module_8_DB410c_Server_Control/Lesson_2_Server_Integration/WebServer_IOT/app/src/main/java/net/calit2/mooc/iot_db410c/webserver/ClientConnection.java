package net.calit2.mooc.iot_db410c.webserver;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Ara on 10/16/15.
 */
public class ClientConnection extends Thread {
    private final Context context;
    private Socket socket;
    JSON json;

    public ClientConnection(Socket socket, Context context){
        this.socket=socket;
        this.context = context;
        json = new JSON(context);

    }

    public void run(){
        try {
            HTTPRequest request = HTTPProcessor.readHTTPGetRequest(socket.getInputStream());
            DataOutputStream output = new DataOutputStream((socket.getOutputStream()));
            Log.i("hello","made my request and such");
            if(request.getResource().equals("/")&&request.isGet()){
                Log.i("hello","At /");
                output.writeUTF(setUpHTTPMessage(context.getString(R.string.default_html_page), "text/html"));
                Log.i("hello","Just wrote!");

            }else if(request.getResource().equals("/status")&&request.isGet()){
                Log.i("hello","At stsus");

                JSONObject jsonObject = json.createJSON();
                output.writeUTF(setUpHTTPMessage(jsonObject.toString(), "application/json"));

            }else if(request.getResource().equals("/status/pretty")&&request.isGet()) {
                Log.i("hello","At purty");

                JSONObject jsonObject = json.createJSON();

                try {
                    output.writeUTF(setUpHTTPMessage(jsonObject.toString(2), "application/json"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                output.writeUTF(setUpHTTPMessage(jsonObject.toString(), "application/json"));
            }else if(request.getResource().equals("/led")&&request.isGet()){
                String timestring = request.getQueryString("times");
                int times = 10;
                if(timestring!=null){
                    times = Integer.parseInt(timestring);
                }
                boolean red=false,yellow=false,green=false;
                if(request.getQueryString("color").equals("all")){
                    red=yellow=green=true;
                }else if(request.getQueryString("color").equals("red")){
                    red=true;
                }else if(request.getQueryString("color").equals("yellow")){
                    yellow=true;
                }else if(request.getQueryString("color").equals("green")){
                    green=true;
                }else{
                    times=0;
                }
                if(LEDBlinker.blink(times,red,yellow,green)){
                    output.writeUTF(setUpHTTPMessage("{\"status\":\"OK\"}","application/json"));
                }else{
                    output.writeUTF(setUpHTTPMessage("{\"status\":\"Error: Led is currently in use!\"}","application/json"));

                }
            }







            else {
                Log.i("hello","At error");

                String content = context.getString(R.string.error_html_page);
                SimpleDateFormat dateFormat =
                        new SimpleDateFormat(context.getString(R.string.date_format),
                                Locale.US);
                dateFormat.setTimeZone(TimeZone.getTimeZone(context.getString(
                        R.string.time_zone)));

                output.writeUTF("HTTP/1.1 404 Not Found\r\n" +
                        "Date: " + dateFormat.format(new Date()).replace("+00:00", "") +
                        "\r\n" +
                        "Content-Type: text/html\r\n" +
                        "Content-Length: " + content.length() + "\r\n" + "\r\n" +
                        content);
            }
            Log.i("hello","At error");

            socket.close();
        } catch (IOException e) {
            Log.i("hello","At error");

            e.printStackTrace();
        }
    }



    /**
     * Name:        setUpHTTPMessage
     * Description: Formats the message into an HTTP request that is to be sent
     *              to the server.
     *
     * @param content       String containing the content of the message
     * @param content_type  String containing the "Content-Type"
     * @return              String containing the formatted HTTP request
     *                      message
     */
    public String setUpHTTPMessage(String content, String content_type){
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",
                        Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        return "HTTP/1.1 200 OK\r\n"+
                "Date: " + dateFormat.format(new Date()).replace("+00:00", "") +
                "\r\n" +
                "Content-Type: " + content_type + "\r\n" +
                "Content-Length: " + content.length() + "\r\n"+ "\r\n" +
                content;
    }
}
