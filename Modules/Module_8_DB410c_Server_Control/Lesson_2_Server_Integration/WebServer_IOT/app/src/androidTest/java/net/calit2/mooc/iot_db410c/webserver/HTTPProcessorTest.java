package net.calit2.mooc.iot_db410c.webserver;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.io.StringReader;

/**
 * Created by Ara on 10/20/15.
 */
public class HTTPProcessorTest extends TestCase {

    InputStream stream;
    public void setUp() throws Exception {
        super.setUp();
        String request = "GET /status?hello=asdf&p=fe2 HTTP/1.1\r\nContent-type: application/json\r\nHost: calit2.net\r\n\r\n";
        stream = new ByteArrayInputStream(request.getBytes("UTF-8"));
    }

    public void tearDown() throws Exception {
        stream.close();
        stream=null;
    }

    public void testReadHTTPGetRequest() throws Exception {
        HTTPRequest request = HTTPProcessor.readHTTPGetRequest(stream);
        assertEquals("GET",request.getHeader("Action"));
        assertEquals("/status",request.getHeader("Resource"));
        assertEquals("HTTP/1.1",request.getHeader("Version"));
        assertEquals("application/json",request.getHeader("Content-type"));
        assertEquals("calit2.net",request.getHeader("Host"));
        assertEquals("asdf",request.getQueryString("hello"));
        assertEquals("fe2",request.getQueryString("p"));

    }
}