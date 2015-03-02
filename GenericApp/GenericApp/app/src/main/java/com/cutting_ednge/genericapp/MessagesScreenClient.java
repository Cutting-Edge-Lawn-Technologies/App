package com.cutting_ednge.genericapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * Created by Aaron on 2/28/2015.
 */
public class MessagesScreenClient extends WebServiceActivity implements View.OnClickListener, OnTaskCompleted, ConnectionFailureListener, ScreenConstants {
    String feed = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages_client_activity);
        //Just going to put this here to make sure the ws is working
        ws();
        ws().addListener(this);
        //makes sure WebService is running
        ws().requestFeeds(MESSAGES_CLIENT);
        Button send = (Button) findViewById(R.id.btn_send_client);
        send.setOnClickListener(this);
        while(feed == null){
        }
        makeMessages();
    }
    @Override
    public void ConnectionFailure() {
        finish();
    }

    @Override
    public void onTaskCompleted() {
        String feed = ws().getFeed();
        this.feed = feed;
    }
    private void makeMessages(){
        System.out.println(feed);
        TextView messages = (TextView) findViewById(R.id.txt_view_client_messages);
        Editable message = messages.getEditableText();
        message.clear();
        message.append(feed);
    }

    @Override
    public void onClick(View v) {
        EditText toSend = (EditText) findViewById(R.id.edit_text_client_messages_show);
        Editable editableMessage = toSend.getText();
        System.out.println(editableMessage.toString());
        ws().sendMessage(editableMessage.toString());
    }
}
