package net.calit2.mooc.iot_db410c.webserver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class WebServerActivity extends Activity {
    WebServer server;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, WebServerService.class);
        startService(intent);
        finish();

    }

    protected void onDestroy(){
        super.onDestroy();
    }

}
