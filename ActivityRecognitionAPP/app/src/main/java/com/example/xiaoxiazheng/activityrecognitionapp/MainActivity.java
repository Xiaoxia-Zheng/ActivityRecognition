package com.example.xiaoxiazheng.activityrecognitionapp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    SensorService.MyBinder binder_;
    SensorService boundedService = null;
    boolean connected = false;
    TextView msg;
    private Handler myHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Create new intent to bound service*/
        Intent linkIntent = new Intent(this, SensorService.class);
        this.bindService(linkIntent, myConnection, BIND_AUTO_CREATE);

        msg = (TextView) findViewById(R.id.textView);


        /*Using handler to set a timer to receive activities every 2 minutes */
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (connected) {
                        myHandler.post(new Acclwork());
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }, 6000, 6000);

    }


    private class Acclwork implements Runnable{
        @Override
        public void run() {
            try {
                if (connected) {
                    msg.setText(SensorService.getMessage());
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private ServiceConnection myConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder_ = (SensorService.MyBinder) service;
            boundedService = binder_.getService();
            connected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            connected = false;
        }
    };






}
