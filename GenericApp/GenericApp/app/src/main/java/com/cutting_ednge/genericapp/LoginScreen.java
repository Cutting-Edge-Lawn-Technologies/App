package com.cutting_ednge.genericapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LoginScreen extends WebServiceActivity implements OnTaskCompleted, View.OnClickListener, ConnectionFailureListener{
    private Button close;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen_layout);
        setupLoginButton();
        ws();
        ws().addConnectionFailureListener(this);
        ws().connect();

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
    private void setupLoginButton(){
        // get button ref
        Button loginButton = (Button)findViewById(R.id.btnLogin);
        //set the click listener
        loginButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        EditText username = (EditText) findViewById(R.id.editTxtUserName);
        EditText password = (EditText) findViewById(R.id.editTxtPassword);
        //get password and username
        String usrName = username.getText().toString();
        String pssWord = password.getText().toString();
        ws().login(usrName, pssWord);
        Intent waiting = new Intent(this, LoginWaitScreen.class);
        startActivity(waiting);
        finish();
    }

    @Override
    public void onTaskCompleted() {

    }

    @Override
    public void ConnectionFailure() {
        Intent serversOopsed = new Intent(this, ServersDownScreen.class);
        startActivity(serversOopsed);
        finish();
    }
}