package com.example.xiaoxiazheng.activityrecognitionapp;

/**
 * Created by XiaoxiaZheng on 3/17/16.
 */


/*Create a class which is convenient to store all the accelerometer data*/
public class AcclData {

    private double x = 0.0;
    private double y = 0.0;
    private double z = 0.0;

    public AcclData(){
    }

    public AcclData(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getZ()
    {
        return z;
    }
}
