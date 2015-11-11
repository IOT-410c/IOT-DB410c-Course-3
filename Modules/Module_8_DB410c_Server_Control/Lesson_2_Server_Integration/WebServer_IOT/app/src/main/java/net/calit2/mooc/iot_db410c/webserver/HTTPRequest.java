package net.calit2.mooc.iot_db410c.webserver;

import java.util.HashMap;

/**
 * Created by Ara on 10/16/15.
 */
public class HTTPRequest {
    HashMap<String,String> headers;
    HashMap<String,String> queryString;

    public HTTPRequest(){
        headers= new HashMap<String,String>();
        queryString= new HashMap<String,String>();

    }

    public void setHeader(String key, String value){
        headers.put(key,value);
    }

    public String getHeader(String key){
        return headers.get(key);
    }

    public boolean isGet(){
        return getHeader("Action").equals("GET");
    }
    public boolean isPost(){
        return getHeader("Action").equals("POST");
    }

    public String getResource(){
        return getHeader("Resource");
    }

    public String setQueryString(String key, String value){
        return queryString.put(key,value);
    }
    public String getQueryString(String key){
        return queryString.get(key);
    }
}
