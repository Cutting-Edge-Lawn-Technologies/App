package com.cutting_ednge.genericclient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Chronometer;
import android.content.Intent;
import android.widget.Toast;

import java.util.EventListener;

/**
 * Created by Aaron on 2/25/2015.
 */
public class LoginWaitScreen extends WebServiceActivity implements OnTaskCompleted {
    Chronometer chronometer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen_login_layout);
        chronometer = (Chronometer)findViewById(R.id.chronometer);
        chronometer.start();
        ws().addListener(this);
    }

    @Override
    public void onTaskCompleted() {
        if(ws().isLogedIn()) {
            if(ws().loginWorked()) {
                Toast.makeText(this, "Welcome", Toast.LENGTH_LONG).show();
                Intent home;
                if(ws().isAdmin()){
                    home = new Intent(this, HomeScreenBusiness.class);
                    startActivity(home);
                }
                else {
                    home = new Intent(this, HomeScreen.class);
                    startActivity(home);
                }
            }
            else{
                Toast.makeText(this,"Login Failed",Toast.LENGTH_LONG).show();
                Intent login = new Intent(this, LoginScreen.class);
                startActivity(login);
            }
        }
        else{
            Toast.makeText(this, "Servers Down Please try again later", Toast.LENGTH_LONG).show();
            Intent login = new Intent(this, LoginScreen.class);
            startActivity(login);
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_screen, menu);
        return true;
    }

}
