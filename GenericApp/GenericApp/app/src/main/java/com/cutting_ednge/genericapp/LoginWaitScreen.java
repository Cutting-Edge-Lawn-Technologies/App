package com.cutting_ednge.genericapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Chronometer;
import android.widget.Toast;

/**
 * Created by Aaron on 2/25/2015.
 */
public class LoginWaitScreen extends WebServiceActivity implements OnTaskCompleted {
    Chronometer chronometer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ws().addListener(this);
        ws().startLoginProcess();
        setContentView(R.layout.loading_screen_login_layout);
        chronometer = (Chronometer)findViewById(R.id.chronometer);
        chronometer.start();

    }

    @Override
    public void onTaskCompleted() {
        if(ws().isLogedIn()) {
            if(ws().loginWorked()) {
                Intent home;
                if(ws().isAdmin()){
                    home = new Intent(this, HomeScreenBusiness.class);
                    startActivity(home);
                    Toast.makeText(this, "Welcome", Toast.LENGTH_LONG).show();
                }
                else {
                    home = new Intent(this, HomeScreen.class);
                    startActivity(home);
                    Toast.makeText(this, "Welcome", Toast.LENGTH_LONG).show();
                }
            }
            else{
                Intent login = new Intent(this, LoginScreen.class);
                startActivity(login);
                Toast.makeText(this,"Login Failed",Toast.LENGTH_LONG).show();
            }
        }
        else{
            Intent login = new Intent(this, LoginScreen.class);
            startActivity(login);
            Toast.makeText(this, "Servers Down Please try again later", Toast.LENGTH_LONG).show();
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

}
