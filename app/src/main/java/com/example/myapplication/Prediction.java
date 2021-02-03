package com.example.myapplication;

import org.json.JSONException;
import org.json.JSONObject;
public class Prediction {
    public int time_to_change;
    public String bulb_color;
    public float distance_to_stop_line;


    Prediction(JSONObject json_data){
        try {
            distance_to_stop_line = json_data.getLong("DistanceToStopLine");
            time_to_change = json_data.getInt("TimeToChange");
            bulb_color = json_data.getString("BulbColor");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void LightData(JSONObject json_data){

    }
}
/*{"Data":{"DataVersion":"1.0.10","TSPackage":"2021-02-03 02:26:29.044","Data":
{"Items":[{"Name":"Portland","Intersections":{"Items":[{"SCNr":3604,"ActivePlan":4,"Name":"NW 20TH PL @ EVERETT ST",
"TSPrediction":"2021-02-03 02:26:29.007","Alert":"PreemptActive","IntersectionType":"CoordinatedByActuation",\
"Phases":{"Items":[{"PhaseNr":2,"BulbColor":"Red","PredictiveChanges":{"Items":[{"TimeToChange":111,"Confidence":0,"BulbColor":"Red"},
{"TimeToChange":171,"Confidence":0,"BulbColor":"Red"}]}}]},"Topology":{"ApproachID":360401,"ApproachBearing":88.6560993960303,
"StopLineBearing":88.6560994989548,"DistanceToStopLine":48.323278986415,"Turns":{"Items":[{"TurnType":"straight","PrimarySignalHeadID":2},
{"TurnType":"right","PrimarySignalHeadID":2}]},"ApproachOutline":{"Vertices":{"Items":[{"X":-122.69420973,"Y":45.5249173636304},
{"X":-122.694171231208,"Y":45.5249179963932},{"X":-122.6935257227,"Y":45.5249286059161},{"X":-122.693487223909,"Y":45.524929238679},
{"X":-122.693483611215,"Y":45.5248213497712},{"X":-122.693522110006,"Y":45.5248207170083},{"X":-122.694167618514,"Y":45.5248101074854},
{"X":-122.694206117305,"Y":45.5248094747226}]}},"StopLineOutline":{"Vertices":{"Items":[{"X":-122.693679717865,"Y":45.5249260748649},{"X":-122.6935257227,"Y":45.5249286059161},
{"X":-122.693487223909,"Y":45.5249292386789},{"X":-122.693483611215,"Y":45.5248213497711},{"X":-122.693522110006,"Y":45.5248207170083},{"X":-122.69367610517,"Y":45.5248181859571}]}},
"StopZoneOutline":{"Vertices":{"Items":[{"X":-122.693804469783,"Y":45.5247282537965},{"X":-122.693547740558,"Y":45.5247282537965},{"X":-122.693355246602,"Y":45.5247314176105},
{"X":-122.693355246602,"Y":45.5249112819317},{"X":-122.693358859297,"Y":45.5250191708395},{"X":-122.693615588522,"Y":45.5250191708395},{"X":-122.693808082477,"Y":45.5250160070255},
{"X":-122.693808082477,"Y":45.5248361427043}]}},"StopLineVertices":{"Items":[{"X":-122.69352436794,"Y":45.5248881475757}]}}}]}}]}}}*/