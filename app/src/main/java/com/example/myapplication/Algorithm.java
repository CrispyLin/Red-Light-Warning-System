package com.example.myapplication;

import android.util.Log;
import java.util.Date;

public class Algorithm {
    //we will do the algorithm here
    private static Prediction prediction;
    private static double speed;
    //this color variable is used in getSmallestTimeToChange method
    //which has the information about the SmallestTimeToChange to what kind of color
    private String color;
    private int smallestPhaseIndex;
    private long previousTime=0;
    private long warningInterval = 5000; //the time period between two warning is set here
    private int minDistanceToStopLine = 5;
    private int maxDistanceToStopLine = 1000;

    //setter method
    public void set(Prediction p, double s){
        prediction = p;
        speed = s;
        color = "";
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

    //assume only one intersection in one response
    //assume the first thing in predictiveChanges is has the smallest TimeToChange
    //this method will look over all phases and find the smallest TimeToChange and update the color
    public double getSmallestTimeToChange(){
        int numOfPhases = prediction.data.data.items[0].intersections.items[0].phases.items.length;
        if(numOfPhases == 0){
            //if phases contain no phase, do nothing
            return -1.0;
        }
        int temp=-1;
        //make an int array and a for loop to save all phases' smallest TimeToChange
        int [] timeToChangeArray = new int [numOfPhases];
        for (int i=0; i<numOfPhases; i++){
            timeToChangeArray[i] = prediction.data.data.items[0].intersections.items[0].phases.items[i].PredictiveChanges.Items[0].TimeToChange;
        }
        //find the smallest TimeToChange in the array, index are the same in both array
        double smallestTimeToChange = timeToChangeArray[0];
        for(int i=1;i<timeToChangeArray.length;i++){
            if(timeToChangeArray[i] < smallestTimeToChange){
                smallestTimeToChange = timeToChangeArray[i];
                smallestPhaseIndex = i;
            }
        }
        //update global variables color and phaseIndex
        color = prediction.data.data.items[0].intersections.items[0].phases.items[smallestPhaseIndex].PredictiveChanges.Items[0].BulbColor;
        //free the array
        timeToChangeArray = null;
        return smallestTimeToChange;
    }

    //create a new thread to display warning every 5 seconds by calculating time interval to limit the warning frequency.
    //if driver is too far or too close the intersection, stop display warnings
    public void ToCompare() throws Exception
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    //get current time
                    Date date = new Date();
                    //This method returns the time in millis
                    long currentTimeInMs = date.getTime();
                    long TimeInterval = currentTimeInMs - previousTime;
                    //if the time between now and last warning is greater than the Max Interval we set
                    //and the car is not too far or too close to the stopline
                    //do the comparation
                    double distanceToStopLine = prediction.data.data.items[0].intersections.items[0].Topology.DistanceToStopLine;
                    if(TimeInterval > warningInterval && distanceToStopLine > minDistanceToStopLine && distanceToStopLine < maxDistanceToStopLine) {
                        compareTwoTimes();
                        previousTime = currentTimeInMs;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        thread.join();
    }

    //this method will look for the first green bulb in the predictiveChanges of smallestPhaseIndex, and return index
    public int searchGreenLight(){
        for(int i=0; i<prediction.data.data.items[0].intersections.items[0].phases.items[smallestPhaseIndex].PredictiveChanges.Items.length;i++){
            if(prediction.data.data.items[0].intersections.items[0].phases.items[smallestPhaseIndex].PredictiveChanges.Items[i].BulbColor.equals("Green")){
                return i;
            }
        }
        return -1;//return negative value to indicate green bulb not existing
    }

    //this method will compare time to stopline and the time to change for the traffic signal, and display warnings
    //Assume Amber means the color of bulb is going to change
    public void compareTwoTimes(){
        //handle errors here
        //1. if prediction's data field is empty, return
        //2. if prediction's intersection field is empty, return
        //3. if prediction's phase field is empty, return
        if(prediction.data.data.items.length==0 || prediction.data.data.items[0].intersections.items.length==0 || prediction.data.data.items[0].intersections.items[0].phases.items.length==0)
            return;
        double timeToStopLine = calculateTimeToStopLine();
        double smallestTimeToChange = getSmallestTimeToChange();
        Log.i("Warning", "current speed is: "+speed);
        //handling currently red bulb
        switch (prediction.data.data.items[0].intersections.items[0].phases.items[smallestPhaseIndex].BulbColor) {
            case "Red":
                if (prediction.data.data.items[0].intersections.items[0].phases.items[smallestPhaseIndex].PredictiveChanges.Items[0].BulbColor.equals("Amber")) {
                    //current red but next will be Amber, which mean the bulb is changing to green, check if user arrives stopline too early
                    if (timeToStopLine <= smallestTimeToChange + 3) {
                        Log.i("Warning", "slow down, or you will encounter Red light or Amber light");
                    }
                }
                else if(prediction.data.data.items[0].intersections.items[0].phases.items[smallestPhaseIndex].PredictiveChanges.Items[0].BulbColor.equals("Green")){
                    //current Red, but next will be Green
                    //so if driver will arrive the stopline before bulb turns green, we warn driver to slow down
                    int timeToChangeOfNextGreen = prediction.data.data.items[0].intersections.items[0].phases.items[smallestPhaseIndex].PredictiveChanges.Items[0].TimeToChange;
                    if(timeToStopLine < timeToChangeOfNextGreen){
                        Log.i("Warning", "slow down, or you will encounter red or Amber light");
                    }
                }
                else if (prediction.data.data.items[0].intersections.items[0].phases.items[smallestPhaseIndex].PredictiveChanges.Items[0].BulbColor.equals("Red")) {
                    //current red but next will be red.
                    Log.i("Warning", "slow down, or you will encounter red light");
                }
                break;
            //handling currently Amber bulb
            case "Amber":
                //current Amber, then next one has to be Red
                if(prediction.data.data.items[0].intersections.items[0].phases.items[smallestPhaseIndex].PredictiveChanges.Items[0].BulbColor.equals("Red")){
                    int greenIndex = searchGreenLight();
                    if(greenIndex < 0){
                        //this means GreenLight does not exist in the predictiveChanges
                        Log.i("Warning", "slow down, or you will encounter Amber or Red light");
                    }
                    int timeToChangeOfNextGreenLight = prediction.data.data.items[0].intersections.items[0].phases.items[smallestPhaseIndex].PredictiveChanges.Items[greenIndex].TimeToChange;
                    if(timeToStopLine < timeToChangeOfNextGreenLight){
                        //if driver arrive stopline before the bulb turns green, warn driver to slow down
                        Log.i("Warning", "slow down, or you will encounter Amber or Red light");
                    }
                }
                break;
            //handling currently Green Bulb
            case "Green":
                if (prediction.data.data.items[0].intersections.items[0].phases.items[smallestPhaseIndex].PredictiveChanges.Items[0].BulbColor.equals("Amber")) {
                    //current Green, but next will be Amber
                    if (timeToStopLine >= smallestTimeToChange) {
                        Log.i("Warning", "speed up, or you will encounter Amber or Red light");
                    }
                } else if (prediction.data.data.items[0].intersections.items[0].phases.items[smallestPhaseIndex].PredictiveChanges.Items[0].BulbColor.equals("Green")) {
                    //current Green, but next will be Green as well
                    //do nothing
                }
                break;
        }
    }

}

