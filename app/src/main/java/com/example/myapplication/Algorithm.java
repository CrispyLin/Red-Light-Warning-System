package com.example.myapplication;

import android.media.Ringtone;
import android.util.Log;
import android.widget.TextView;

import java.util.Date;

public class Algorithm {
    //we will do the algorithm here
    private static Prediction prediction;
    private static double speed;
    //this color variable is used in getSmallestTimeToChange method
    //which has the information about the SmallestTimeToChange to what kind of color
    private int straightPhaseIndex;
    private long previousTime=0;
    private long warningInterval = 5000; //the time period between two warning is set here
    // if minDTS < currentDTS < maxDTS is true, then the algorithm will decide weather an alarm is needed
    private int minDistanceToStopLine = 5;
    private int maxDistanceToStopLine = 1000;
    private double minTriggerSpeed = 0.5; //if CurrentSpeed > minTriggerSpeed, then the algorithm will decide weather an alarm is needed
    private Ringtone alarm;


    //setter method
    public void set(Prediction p, double s, Ringtone r)  {
        prediction = p;
        speed = s;
        alarm = r;
        }


    //  this method will check if the prediction has a enough data for us to proceed
    //  1. if prediction's data field is empty, return false
    //  2. if prediction's intersections field is empty, return false
    //  3. if prediction's intersections's item field is empty, return false
    //  4. if prediction's phases field is empty, return false
    //  5. if prediction's phases' item field is empty, return false
    //  otherwise return true
    //  the order matters
    public boolean content_checking(){
        if(prediction.data.data.items.length == 0
                || prediction.data.data.items[0].intersections == null
                || prediction.data.data.items[0].intersections.items.length == 0
                || prediction.data.data.items[0].intersections.items[0].phases == null
                || prediction.data.data.items[0].intersections.items[0].phases.items.length == 0
                || prediction.data.data.items[0].intersections.items[0].Topology == null)
            return false;
        else
            return true;
    }


    // this method will set up textview for DTS, street and current bulb's color
    public void set_textView(TextView textView_DTS, TextView textView_currentStreet, TextView textView_straightBulb) throws Exception {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    // check if we can get street name from prediction
                    if(prediction.data.data.items.length!=0
                            && prediction.data.data.items[0].intersections != null
                            && prediction.data.data.items[0].intersections.items.length!=0)
                        // set Street first
                        textView_currentStreet.setText(prediction.data.data.items[0].intersections.items[0].Name);

                    // check if DTS exists
                    if(prediction.data.data.items.length!=0
                            && prediction.data.data.items[0].intersections != null
                            && prediction.data.data.items[0].intersections.items.length!=0
                            && prediction.data.data.items[0].intersections.items[0] != null
                            && prediction.data.data.items[0].intersections.items[0].Topology != null)
                        textView_DTS.setText(prediction.data.data.items[0].intersections.items[0].Topology.DistanceToStopLine + "");

                    // check if straightBulb exists
                    if(prediction.data.data.items.length!=0
                            && prediction.data.data.items[0].intersections != null
                            && prediction.data.data.items[0].intersections.items.length!=0
                            && prediction.data.data.items[0].intersections.items[0] != null
                            && prediction.data.data.items[0].intersections.items[0].Topology != null
                            && prediction.data.data.items[0].intersections.items[0].Topology.Turns != null
                            && prediction.data.data.items[0].intersections.items[0].Topology.Turns.Items.length != 0
                            && prediction.data.data.items[0].intersections.items[0].phases != null
                            && prediction.data.data.items[0].intersections.items[0].phases.items.length != 0)
                    {
                        for (int i=0; i<prediction.data.data.items[0].intersections.items[0].Topology.Turns.Items.length; i++)
                        {
                            int turn_ID = prediction.data.data.items[0].intersections.items[0].Topology.Turns.Items[i].PrimarySignalHeadID;
                            String turn_type = prediction.data.data.items[0].intersections.items[0].Topology.Turns.Items[i].TurnType;
                            // if we find straight bulb, and its ID, we check its current color in Phase
                            if (turn_type.equals("straight"))
                            {
                                for(int j=0; j<prediction.data.data.items[0].intersections.items[0].phases.items.length;j++)
                                {
                                    if(prediction.data.data.items[0].intersections.items[0].phases.items[j].PhaseNr == turn_ID)
                                    {
                                        textView_straightBulb.setText(prediction.data.data.items[0].intersections.items[0].phases.items[j].BulbColor);
                                        break;
                                    }
                                }
                                break; //after update straight bulb's color, break the for loop
                            }
                        }

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        thread.join();
    }

    //this method will calculate the time needed for the car in current speed to the nearest stop line
    public double calculateTimeToStopLine(){
        double t = 0.0; //this is the variable will hold the result time
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
    //this method will look over all phases and find the "straight" phase's first TimeToChange
    public double getSmallestTimeToChange(){
        int numOfPhases = prediction.data.data.items[0].intersections.items[0].phases.items.length;
        //get straight bulb's turn number
        int turn_ID = -1;
        for (int i=0; i<prediction.data.data.items[0].intersections.items[0].Topology.Turns.Items.length; i++)
        {
            turn_ID = prediction.data.data.items[0].intersections.items[0].Topology.Turns.Items[i].PrimarySignalHeadID;
            String turn_type = prediction.data.data.items[0].intersections.items[0].Topology.Turns.Items[i].TurnType;
            // if we find straight bulb, and its ID, we check its current color in Phase
            if (turn_type.equals("straight"))
            {
                break; //after update straight bulb's color, break the for loop
            }
        }
        if (turn_ID == -1){
            //this means we did not find straight bulb
            return -1;
        }
        else {
            for(int i=0;i<prediction.data.data.items[0].intersections.items[0].phases.items.length;i++)
            {
                if(turn_ID == prediction.data.data.items[0].intersections.items[0].phases.items[i].PhaseNr)
                {
                    if(prediction.data.data.items[0].intersections.items[0].phases.items[i].PredictiveChanges.Items.length==0){
                        return -1;
                    }
                    straightPhaseIndex = i;
                    return prediction.data.data.items[0].intersections.items[0].phases.items[i].PredictiveChanges.Items[0].TimeToChange;
                }
            }
        }
        return -1.0;
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
                    //and the speend is not too small (this is to ensure user is not waiting for something)
                    //do the comparation
                    double distanceToStopLine = prediction.data.data.items[0].intersections.items[0].Topology.DistanceToStopLine;
                    if(TimeInterval > warningInterval && distanceToStopLine > minDistanceToStopLine && distanceToStopLine < maxDistanceToStopLine && speed >= minTriggerSpeed) {
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


    //this method will look for the first green bulb in the predictiveChanges of straightPhaseIndex, and return index
    public int searchGreenLight(){
        for(int i = 0; i<prediction.data.data.items[0].intersections.items[0].phases.items[straightPhaseIndex].PredictiveChanges.Items.length; i++){
            if(prediction.data.data.items[0].intersections.items[0].phases.items[straightPhaseIndex].PredictiveChanges.Items[i].BulbColor.equals("Green")){
                return i;
            }
        }
        return -1;//return negative value to indicate green bulb not existing
    }


    //this method will compare time to stopline and the time to change for the traffic signal, and display warnings
    //Assume Amber means the color of bulb is going to change
    public void compareTwoTimes(){
        double timeToStopLine = calculateTimeToStopLine();
        double smallestTimeToChange = getSmallestTimeToChange();//this step also gives us the smallestPhaseIndex variable
        String nextColor = prediction.data.data.items[0].intersections.items[0].phases.items[straightPhaseIndex].BulbColor;
        //handling currently red bulb
        switch (nextColor) {
            case "Red":
                switch (prediction.data.data.items[0].intersections.items[0].phases.items[straightPhaseIndex].PredictiveChanges.Items[0].BulbColor) {
                    case "Amber":
                        //current red but next will be Amber, which mean the bulb is changing to green, check if user arrives stopline too early
                        if (timeToStopLine <= smallestTimeToChange + 3) {
                            try {
                                alarm.play();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Log.i("Warning", "slow down, or you will encounter Red light or Amber light");
                        }
                        break;
                    case "Green":
                        //current Red, but next will be Green
                        //so if driver will arrive the stopline before bulb turns green, we warn driver to slow down
                        int timeToChangeOfNextGreen = prediction.data.data.items[0].intersections.items[0].phases.items[straightPhaseIndex].PredictiveChanges.Items[0].TimeToChange;
                        if (timeToStopLine < timeToChangeOfNextGreen) {
                            Log.i("Warning", "slow down, or you will encounter red or Amber light");
                        }
                        break;
                    case "Red":
                        //current red but next will be red.
                        try {
                            alarm.play();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.i("Warning", "slow down, or you will encounter red light");
                        break;
                }
                break;
            //handling currently Amber bulb
            case "Amber":
                //current Amber, then next one has to be Red
                if(prediction.data.data.items[0].intersections.items[0].phases.items[straightPhaseIndex].PredictiveChanges.Items[0].BulbColor.equals("Red")){
                    int greenIndex = searchGreenLight();
                    if(greenIndex < 0){
                        //this means GreenLight does not exist in the predictiveChanges
                        Log.i("Warning", "slow down, or you will encounter Amber or Red light");
                    }
                    int timeToChangeOfNextGreenLight = prediction.data.data.items[0].intersections.items[0].phases.items[straightPhaseIndex].PredictiveChanges.Items[greenIndex].TimeToChange;
                    if(timeToStopLine < timeToChangeOfNextGreenLight){
                        //if driver arrive stopline before the bulb turns green, warn driver to slow down
                        try {
                            alarm.play();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }

                        Log.i("Warning", "slow down, or you will encounter Amber or Red light");
                    }
                }
                break;
            //handling currently Green Bulb
            case "Green":
                if (prediction.data.data.items[0].intersections.items[0].phases.items[straightPhaseIndex].PredictiveChanges.Items[0].BulbColor.equals("Amber")) {
                    //current Green, but next will be Amber
                    if (timeToStopLine >= smallestTimeToChange) {
                        Log.i("Warning", "speed up, or you will encounter Amber or Red light");
                    }
                } else if (prediction.data.data.items[0].intersections.items[0].phases.items[straightPhaseIndex].PredictiveChanges.Items[0].BulbColor.equals("Green")) {
                    //current Green, but next will be Green as well
                    //do nothing
                }
                break;
        }
    }
}

