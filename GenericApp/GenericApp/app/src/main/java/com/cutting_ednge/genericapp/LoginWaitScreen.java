package com.cutting_ednge.genericapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Chronometer;
import android.widget.Toast;

/**
 * Created by Aaron on 2/25/2015.
 */
public class LoginWaitScreen extends WebServiceActivity implements OnTaskCompleted, ConnectionFailureListener {
    //timer to show that it is indeed processing
    Chronometer chronometer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen_login_layout);
        //add a listener for when the events get done
        ws().addListener(this);
        //add a failure listener in case server derps
        ws().addConnectionFailureListener(this);
        //start the login confirmation process
        ws().startLoginProcess();
        //start the timer to show that things are happening
        chronometer = (Chronometer)findViewById(R.id.chronometer);
        chronometer.start();

    }

    @Override
    public void ConnectionFailure(){
        //if the server does naughty things then let the user know
        Intent serversOopsed = new Intent(this, ServersDownScreen.class);
        startActivity(serversOopsed);
        //finish so that you can't come back
        finish();
    }

    @Override
    public void onTaskCompleted() {
        //on the server's "bing! Food is done" alert
        //check to see if it tried to login in
        if(ws().isLogedIn()) {
            //if it worked
            if(ws().loginWorked()) {
                //then create an Intent for going to the home screen
                Intent home;
                //if it was an admin who logged in
                if(ws().isAdmin()){
                    //then send him/her to the home screen for admins
                    home = new Intent(this, HomeScreenBusiness.class);
                    startActivity(home);
                    //todo: figure out why this toast doesn't work
                    Toast.makeText(this, "Welcome", Toast.LENGTH_LONG).show();
                }
                //else if it was a client that logged in
                else {
                    //then create an Intent for it going to the home screen for clients
                    home = new Intent(this, HomeScreen.class);
                    startActivity(home);
                    //todo: figure out why this toast doesn't work
                    Toast.makeText(this, "Welcome", Toast.LENGTH_LONG).show();
                }
            }
            //else if it was an invalid combo of user names and passwords
            else{
                //create an new intent for sending them to the login screen again
                Intent login = new Intent(this, LoginScreen.class);
                startActivity(login);
                //todo: figure out why this toast doesn't work
                Toast.makeText(this,"Login Failed",Toast.LENGTH_LONG).show();
            }
        }
        //else if they failed to login at all
        else{
            //todo: change to server failure page
            Intent login = new Intent(this, LoginScreen.class);
            startActivity(login);
            Toast.makeText(this, "Servers Down Please try again later", Toast.LENGTH_LONG).show();
        }
        //finish so that you can't come back
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

}
