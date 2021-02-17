package com.example.myapplication;

import android.util.Log;

public class Algorithm {
    //we will do the algorithm here
    private static Prediction prediction;
    private static double speed;

    //setter method
    public void set(Prediction p, double s){
        prediction = p;
        speed = s;
    }

    //this method will calculate the time needed for the car in current speed to the nearest stop line
    public double calculateTimeToStopLine(){
        double t = 0.0; //this is the variable will hold the result time
        //f
        double distanceToStopLine = prediction.data.data.items[0].intersections.items[0].Topology.DistanceToStopLine;
        if(speed != 0.0){
            t = distanceToStopLine/speed;
            return t;
        }else{
            Log.e("Mytag", "In Algorithm class, calculateTimeToStopLine method, dividing 0 speed!");
            return -1.0; //return negative value to indicate there is a problem
        }
    }

    //this method will look over all green light bulbs to see which one has the smallest
    public void getSmallSignal(){

    }

    //this method will compare time to stopline and the time to change for the traffic signal
    public double compareTwoTimes(){
        double t = calculateTimeToStopLine();
        return t;
    }
}

