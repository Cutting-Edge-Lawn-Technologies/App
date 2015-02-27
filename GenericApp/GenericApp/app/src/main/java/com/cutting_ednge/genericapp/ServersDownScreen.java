package com.cutting_ednge.genericapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Aaron on 2/27/2015.
 */
public class ServersDownScreen extends WebServiceActivity implements  View.OnClickListener {
    @Override
    public void onClick(View v) {
        finish();
    }
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.servers_down_activity);
        Button close = (Button) findViewById(R.id.btnServersDown);
        close.setOnClickListener(this);
    }

}
