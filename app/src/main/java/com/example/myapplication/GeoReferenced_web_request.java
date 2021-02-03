package com.example.myapplication;

import android.os.Bundle;
import android.view.View;


import androidx.annotation.NonNull;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;

import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONException;
import org.json.JSONObject;


public class GeoReferenced_web_request extends AppCompatActivity {
    private TextView textView_longitude;
    private TextView textView_latitude;
    private TextView textView_heading;
    private String longitude, latitude, heading;
    //two objs to use GPS service
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String session_code;
    private JSONObject json_data;
    private Prediction prediction;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_referenced_web_request);
        //get session_code from previous activity
        if (getIntent().hasExtra("session_code")) {
            TextView show_session_code = (TextView) findViewById(R.id.textView_Sessioncode);
            session_code = getIntent().getExtras().getString("session_code");
            show_session_code.setText(session_code);
        }


        // private variables
        //The button will send request to the TTS server upon click
        Button btn = (Button) findViewById(R.id.Btn_request);
        textView_latitude = (TextView) findViewById(R.id.textView_latitude);
        textView_longitude = (TextView) findViewById(R.id.textView_longitude);
        textView_heading = (TextView) findViewById(R.id.textView_heading);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // location in the parameter has everything we need
                longitude = String.valueOf(location.getLongitude());
                latitude = String.valueOf(location.getLatitude());
                heading = String.valueOf(location.getBearing());
                textView_longitude.setText(longitude);
                textView_latitude.setText(latitude);
                textView_heading.setText(heading);
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        //start getting GPS locations
        requestGPS();

        //when the btn is triggered, the program starts to send GET request to the TTS's server and receive response from the TTS server
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String geoReferenceURL = "http://216.151.19.133:5832/APhA/Services/GeoReferencedPredictions?sessionCode=" + session_code + "&latitude=" + latitude + "&longitude=" + longitude + "&heading=" + heading + "&includeTopology=yes&asTurns=yes&includePermissives=no&includeAmber=no&bearingType=Compass&matchingMode=TTSDefault&version=1.0.10&returnJSON=yes";
                String TTS_response = "";
                try {
                    TTS_response = TestTest.SendInputs(geoReferenceURL);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                // check if TTS returns response, and parse the response
                if (TTS_response != null) {
                    if (TTS_response.equals("")) {
                        try {
                            json_data = new JSONObject(TTS_response);
                            prediction = new Prediction(json_data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    // do parsing here, you might want to create an obj as a private variable on the top
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            requestGPS();
        }
    }

    private void requestGPS() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        // request for GPS location
        locationManager.requestLocationUpdates("gps", 2000, 0, locationListener);
    }

    @Override
    public void onBackPressed() {
        //do nothing when go-back button is pressed, so user won't be able to go back to login page by pressing go-back button
    }
}
