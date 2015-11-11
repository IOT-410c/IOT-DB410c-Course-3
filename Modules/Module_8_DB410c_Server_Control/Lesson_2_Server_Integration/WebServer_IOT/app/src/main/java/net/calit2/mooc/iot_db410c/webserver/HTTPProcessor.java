package net.calit2.mooc.iot_db410c.webserver;

import android.util.Log;

import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by Ara on 10/16/15.
 */
public class HTTPProcessor {


    public static HTTPRequest readHTTPGetRequest(InputStream is){
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        HTTPRequest request = new HTTPRequest();
        try {
            String line=br.readLine();
            String[] firstline= line.split(" ");
            request.setHeader("Action", firstline[0]);
            String[] resources = firstline[1].split("\\?");
            if(resources.length>1){
                parseQueryString(request, resources);
            }
            request.setHeader("Resource", resources[0]);
            request.setHeader("Version", firstline[2]);
            while((line=br.readLine())!=null&& !line.equals("")){
                String[] header = line.split(": ");


                request.setHeader(header[0], header[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        return request;
    }

    public static void parseQueryString(HTTPRequest request, String[] resources){
        String [] query = resources[1].split("&");
        for (String param: query) {
            String [] keyvalue = param.split("=");
            request.setQueryString(keyvalue[0],keyvalue[1]);
        }

    }
}
