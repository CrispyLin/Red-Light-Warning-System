package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;


import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
public class MainActivity extends AppCompatActivity {
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
    }

    private void auto_login(SharedPreferences sharedPreferences){
        String username = sharedPreferences.getString("username", "null");
        String password = sharedPreferences.getString("password", "null");
        String IP = sharedPreferences.getString("IP", "null");
        String[] arguments = {"-username", username, "-password", password, "IP", IP};
        String session_code = null;
        // try to get session_code from TTS server
        try {
            session_code = null;
            session_code = TestTest.main(arguments);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // if session code is null,
        if (session_code == null) {
            Log.e("Mytag", "Login Failed");
            // if TTS's server doesn't like old credentials, direct user to manual login as well
            manual_login(sharedPreferences);
        } else {
            //create an object to another activity
            Intent startIntent = new Intent(getApplicationContext(), GeoReferenced_web_request.class);

            //passing sessioncode into another activity
            startIntent.putExtra("session_code", session_code);
            //running the new activity
            startActivity(startIntent);
            finish();
        }
    }


    private void manual_login(SharedPreferences sharedPreferences){
        setContentView(R.layout.activity_main);
        Button Btn_Login = (Button) findViewById(R.id.button_logIn);
        Btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText_Username = (EditText) findViewById(R.id.editText_username);
                EditText editText_Password = (EditText) findViewById(R.id.editText_password);
                EditText editText_IP = (EditText) findViewById(R.id.editText_IP);
                String username = editText_Username.getText().toString();
                String password = editText_Password.getText().toString();
                String IP = editText_IP.getText().toString();
                String[] arguments = {"-username", username, "-password", password, "-IP", IP};
                String session_code = null;
                try {
                    session_code = TestTest.main(arguments);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (session_code == null) {
                    Log.e("Mytag", "Login Failed");
                } else {
                    // save user's credentials for next time they use the app
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", username);
                    editor.putString("password",password);
                    editor.putString("IP",IP);
                    editor.commit();

                    //create an object to another activity
                    Intent startIntent = new Intent(getApplicationContext(), GeoReferenced_web_request.class);

                    //passing sessioncode into another activity
                    startIntent.putExtra("session_code", session_code);
                    //running the new activity
                    startActivity(startIntent);
                    finish();
                }
            }
        });
    }
}