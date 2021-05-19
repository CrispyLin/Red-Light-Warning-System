package com.RedLightWarning.System;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;


import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
public class Login extends AppCompatActivity {
    // member variable
    private final String def_IP = "38.103.174.3:5832"; // the default IP that can be modified once TTS changes their IP

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get user's username password and IP from last login
        SharedPreferences sharedPreferences = getSharedPreferences("login_info", MODE_PRIVATE);
        // check if user's data existed
        // if yes, auto login the user into our app
        // if no, use manual login which asks user to type
        // if auto login fails to find session code, user's data might be outdated, direct user to manual login as well
        if (sharedPreferences.getString("username", "null") != "null") {
            auto_login(sharedPreferences);
        }
        else{
            // if user dont have their username, they have to manually type their credentials
            // and new credentials will be saved
            manual_login(sharedPreferences);
        }

        // create click listener for button_ForgotPassW
        Button Btn_ForgotPassW = (Button) findViewById(R.id.button_ForgotPassW);
        Btn_ForgotPassW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url_forgotPW = "http://216.151.19.135/Account/ForgotPassword";
                Uri uri = Uri.parse(url_forgotPW);
                Intent registerWeb = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(registerWeb);
            }
        });

        // create click listener for button_SignUp
        Button Btn_SignUp = (Button) findViewById(R.id.button_SignUp);
        Btn_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url_register = "http://216.151.19.135/Account/Register";
                Uri uri = Uri.parse(url_register);
                Intent registerWeb = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(registerWeb);
            }
        });
        return;
    }


    // auto login uses user credentials saved from last login
    // we use SharedPreferences to save user credentials and check if credentials exists
    // if credentials do exist, we use those to login automatically,
    // however, if we failed to get session code from TTS, we think the credentials were outdated/wrong
    // then we direct user to manual login to re-type correct credentials
    private void auto_login(SharedPreferences sharedPreferences){
        String username = sharedPreferences.getString("username", "null");
        String password = sharedPreferences.getString("password", "null");
        String IP = sharedPreferences.getString("IP", def_IP);
        String[] arguments = {"-username", username, "-password", password, "-IP", IP};
        String session_code = null;
        // try to get session_code from TTS server
        try {
            session_code = null;
            session_code = TestTest.main(arguments);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // if session code is null, it means we failed to get session code from TTS
        // we would direct user to manual login
        if (session_code == null) {
            Log.e("Mytag", "Login Failed");
            // if TTS's server doesn't like old credentials, direct user to manual login as well
            manual_login(sharedPreferences);
        } else {
            // if session code is found, it means user credentials are valid, we would proceed to make a new activity
            // and direct user to it and close the login activity for saving memory
            //create an object to another activity
            direct_user_to_next_activity(IP, session_code);
        }
        return;
    }


    // create another intent activity, send user to the next activity and close current one
    private void direct_user_to_next_activity(String IP, String session_code){
        //create an object to another activity
        Intent startIntent = new Intent(getApplicationContext(), GeoReferenced_web_request.class);

        //passing sessioncode into another activity
        startIntent.putExtra("session_code", session_code);
        startIntent.putExtra("IP", IP);
        //running the new activity
        startActivity(startIntent);
        finish();
        return;
    }


    private void manual_login(SharedPreferences sharedPreferences){
        setContentView(R.layout.activity_login);
        EditText editText_IP = (EditText) findViewById(R.id.editText_IP);
        editText_IP.setText(def_IP);
        Button Btn_Login = (Button) findViewById(R.id.button_logIn);
        Btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // find Username, password, IP's textviews
                EditText editText_Username = (EditText) findViewById(R.id.editText_username);
                EditText editText_Password = (EditText) findViewById(R.id.editText_password);
                EditText editText_IP = (EditText) findViewById(R.id.editText_IP);
                // get Username, password and IP from textviews where user types
                String username = editText_Username.getText().toString();
                String password = editText_Password.getText().toString();
                String IP = editText_IP.getText().toString();
                // format user's credentials in a certain way
                String[] arguments = {"-username", username, "-password", password, "-IP", IP};
                String session_code = null;
                // try to get session code from TTS using user's credentials
                try {
                    session_code = TestTest.main(arguments);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // if we failed to get session code from TTS, we think user's credentials were wrong
                if (session_code == null) {
                    Log.e("Mytag", "Login Failed");
                } else {
                    // if we successfully get the session code, then we save user's credentials for auto login next time
                    // and direct user to next activity (and close login activity)

                    // save user's credentials for next time they use the app
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", username);
                    editor.putString("password",password);
                    editor.putString("IP",IP);
                    editor.commit();

                    // direct user to another activity and close current activity
                    direct_user_to_next_activity(IP, session_code);
                }
            }
        });
        return;
    }
}