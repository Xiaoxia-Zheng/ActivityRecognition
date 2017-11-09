package com.example.xiaoxiazheng.activityrecognitionapp;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public class SensorService extends Service implements SensorEventListener{

    private MyBinder myBinder = new MyBinder();
    private SensorManager myAcclManager;
    private Sensor accelerometer_;
    private final int DELAY = 100;
    private ArrayList<AcclData> allAcclData = new ArrayList<>();
    private LinkedList<String> msgList = new LinkedList<>();
    private double abs_y = 0.0, abs_z = 0.0;
    private double deviation_x, deviation_y, deviation_z;
    private double acclx, accly, acclz;
    Date begin;
    private long startTime, endTime;
    static final long ONE_MINUTE_IN_MILLIS = 6000;
    private String activity;
    DateFormat df = new SimpleDateFormat("h:mm a");
    String start;
    String end;
    static String message;
    private File Dir, file;


    public SensorService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        timeInitiate();
        writeToStorage();
        getAcclManager();
        return myBinder;
    }


    public class MyBinder extends Binder {
        public SensorService getService() {
            return SensorService.this;
        }
    }

    /*Register accelerometer sensor*/
    public void getAcclManager() {
        myAcclManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer_ = myAcclManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        myAcclManager.registerListener(this, accelerometer_, SensorManager.SENSOR_DELAY_NORMAL, DELAY);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        Date newTime = new Date();
        long curTime = newTime.getTime();
        Sensor mySensor = event.sensor;

        /*Receive accelerometer data from sensor*/
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER )
        {
            acclx = event.values[0];
            accly = event.values[1];
            acclz = event.values[2];
        }

        /*Store accelerometer data into an Arraylist-allAcclData every 2 minutes*/
        if (curTime < endTime)
        {
            AcclData accls = new AcclData(acclx, accly, acclz);
            allAcclData.add(accls);

        }else
        {
            /*Reset the time for the next 2 minutes*/
            startTime = curTime;
            Date beginTime = new Date(startTime - (2 * ONE_MINUTE_IN_MILLIS));
            start = df.format(beginTime);
            Date afterTwoMins = new Date(startTime + (2 * ONE_MINUTE_IN_MILLIS));
            endTime = afterTwoMins.getTime();
            end = df.format(startTime);


            CalData(allAcclData); /*Calculate data for algorithm*/
            algorithm(); /*Activity recognition algorithm*/
            upDateFile(); /*Write activities message into SD card storage*/
            allAcclData.clear();
        }
    }

    /*Initiate time*/
    public void timeInitiate(){
        begin = new Date();
        start = df.format(begin);
        startTime = begin.getTime();
        Date afterTwoMins = new Date(startTime + (2 * ONE_MINUTE_IN_MILLIS));
        endTime = afterTwoMins.getTime();
    }

    /*Calculate data*/
    public void CalData(final ArrayList<AcclData> allAcclData) {
        double sum_x = 0.0, sum_y = 0.0, sum_z = 0.0;
        double average_x = 0.0, average_y = 0.0, average_z = 0.0;
        deviation_x =0.0;
        deviation_y =0.0;
        deviation_z =0.0;
        for (AcclData accls : allAcclData )
        {
            sum_x += accls.getX();
            sum_y += accls.getY();
            sum_z += accls.getZ();
        }

        average_x = sum_x/allAcclData.size();
        average_y = sum_y/allAcclData.size();
        average_z = sum_z/allAcclData.size();

        abs_y = Math.abs(average_y);
        abs_z = Math.abs(average_z);

        for (AcclData accls : allAcclData )
        {
            deviation_x += Math.pow(accls.getX() - average_x, 2);
            deviation_y += Math.pow(accls.getY() - average_y, 2);
            deviation_z += Math.pow(accls.getZ() - average_z, 2);
        }

        deviation_x = deviation_x/allAcclData.size();
        deviation_y = deviation_y/allAcclData.size();
        deviation_z = deviation_z/allAcclData.size();
    }


    /*Activity recognition algorithm*/
    public void algorithm(){
        if (deviation_z >= 2.0)
        {
            activity = "Walking or Running";
        }

        else if (deviation_x < 2.0 && deviation_y < 2.0 && deviation_z < 2.0 && abs_y >= 3.0 && abs_y <= 10.0)
        {
            activity = "Sitting";
        }

        else if (deviation_x < 2.0 && deviation_y < 2.0 && deviation_z < 2.0 && abs_z >= 8.0 && abs_z <= 10.0)
        {
            activity = "Sleeping";
        }

        else
        {
            activity = "Other Activity";
        }

    }


    /*Initiate file to store data into SD card*/
    public void writeToStorage () {
        if (Environment.getExternalStorageState() != null)
        {
            File SDroot = Environment.getExternalStorageDirectory();
            Dir = new File(SDroot.getAbsolutePath() + "/ActivityRecog");
            if (!Dir.exists())
            {
                Dir.mkdirs();
            }
            file = new File(Dir, "Activity.txt");
            message = "Recording Activities: " + "\n";
            try
            {
                FileOutputStream fOut = new FileOutputStream(file);
                fOut.write(message.getBytes());
                fOut.close();
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(getApplicationContext(), "SD card not found", Toast.LENGTH_LONG).show();
        }
    }


    public void upDateFile () {
        /*Store activities message into SD card file*/
        message = "  " + start + " - " + end + "   " + activity + "\n";
        try {
            if (file.exists()) {
                FileOutputStream fOut = new FileOutputStream(file, true);
                OutputStreamWriter osw = new OutputStreamWriter(fOut);
                osw.write(message);
                osw.flush();
                osw.close();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        /*Create a linkedList to store all activities message and make the messages' length only 10*/
        msgList.addFirst(message);
        String tempMsg = "";
        if (msgList.size() <= 10)
        {
            for (int i = 0; i < msgList.size(); i++) {
                tempMsg += msgList.get(i);
            }
            message = tempMsg;
        }
        else if (msgList.size() >= 10)
        {
            for (int i = 0; i < 10; i++)
            {
                tempMsg += msgList.get(i);
            }
            message = tempMsg;
        }
    }

    /*Return activities for mainActivities to update UI*/
    public static String getMessage(){
        return message;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
