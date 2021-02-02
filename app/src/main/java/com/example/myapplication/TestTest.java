package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TestTest
{
    public static String Username = "";
    public static String Password = "";
    public static String session_code = "";

    public static void DoTest(String[] args) throws Exception
    {
        ParseArguments(args);

        String loginURL = "http://216.151.19.133:5832/APhA/Services/Login?";

        String params = "username=" + Username + "&password=" + Password;

        String loginResponse = GetStringFromURLRequest(loginURL + params);
        Log.i("Mytag","Sessioncode Request Result: " + loginResponse);
        // Find what is in between <SessionCode> and </SessionCode>
        String sessionCode = "";
        Pattern p = Pattern.compile(".*<SessionCode>(.*)</SessionCode>.*");
        Matcher m = p.matcher(loginResponse);
        if(m.matches()) {
            sessionCode = m.group(1);
        }

        if (sessionCode.length() == 0 ) {
            Log.e("Mytag","Error in finding session code.");
            session_code = "";
            return;
        }
        session_code = sessionCode;
        String geoReferenceURL = "http://216.151.19.133:5832/APhA/Services/GeoReferencedPredictions?sessionCode=" + sessionCode + "&latitude=45.524868&longitude=-122.694144&heading=85&includeTopology=yes&asTurns=yes&includePermissives=no&includeAmber=no&bearingType=Compass&matchingMode=TTSDefault&version=1.0.10&returnJSON=yes";

        String geoReferenceResponse = GetStringFromURLRequest(geoReferenceURL);
    }

    public static String SendInputs(String url) throws Exception
    {
        final String[] geoReferenceResponse = {null};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    geoReferenceResponse[0] = GetStringFromURLRequest(url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        thread.join();
        return geoReferenceResponse[0];

    }

    public static String GetStringFromURLRequest(String url) throws Exception
    {
        HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();

        // optional default is GET
        httpClient.setRequestMethod("GET");

        //add request header
        httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = httpClient.getResponseCode();
        Log.i("Mytag","\nSending 'GET' request to URL : " + url);
        Log.i("Mytag","Response Code : " + responseCode);

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(httpClient.getInputStream()))) {

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            //print result
            Log.i("Mytag","Response : " + response.toString());
            return response.toString();
        }
    }

    public static void ParseArguments(String[] args)
    {
        for (int i = 0; i < args.length; i++) {
            String cVal = args[i].toLowerCase();

            switch (cVal) {
                case "-username":
                    if ((i + 1) < args.length) {
                        Username = args[i + 1];
                        i++;
                    }
                    break;

                case "-password":
                    if ((i + 1) < args.length) {
                        Password = args[i + 1];
                        i++;
                    }
                    break;
            }
        }
    }

    public static String main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    DoTest(args); //Your code goes here
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        thread.join();
        return session_code;
    }


}
