package com.cutting_ednge.genericapp;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;


/**
 * Created by Aaron on 2/28/2015.
 */
public class MessagesScreenClient extends WebServiceActivity implements OnTaskCompleted, ConnectionFailureListener, ScreenConstants {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages_client_activity);
        //Just going to put this here to make sure the ws is working
        ws();
        //makes sure WebService is running
        ws().requestFeeds(MESSAGES_CLIENT);
    }
    @Override
    public void ConnectionFailure() {
        finish();
    }

    @Override
    public void onTaskCompleted() {
        String feed = ws().getFeed();
        System.out.println(feed);
        TextView messages = (TextView) findViewById(R.id.txt_view_client_messages);
        messages.setText(feed);
    }
}
