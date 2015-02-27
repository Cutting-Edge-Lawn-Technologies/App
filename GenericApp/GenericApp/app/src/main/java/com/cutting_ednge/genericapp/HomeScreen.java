package com.cutting_ednge.genericapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * Created by Aaron on 2/25/2015.
 */
public class HomeScreen extends WebServiceActivity implements View.OnClickListener {
    //buttons
    Button settings;
    Button messages;
    Button viewRequests;
    Button submitRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_activity);
        //makes sure WebService is running
        ws();
        //sets up the buttons
        setupButtons();
    }

    private void setupButtons(){
        //settings button
        settings = (Button) findViewById(R.id.btnSettingsClient);
        settings.setOnClickListener(this);
        //messages button
        messages = (Button) findViewById(R.id.btnMessagesClient);
        messages.setOnClickListener(this);
        //view requests button
        viewRequests = (Button) findViewById(R.id.btnCheckStatusClient);
        viewRequests.setOnClickListener(this);
        //submit requests button
        submitRequest = (Button) findViewById(R.id.btnSubmitRequest);
        submitRequest.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        //todo: add buttons and shits to this part and make them work
    }
}
