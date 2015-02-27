package com.cutting_ednge.genericclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LoginScreen extends WebServiceActivity implements  View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen_layout);
        setupLoginButton();
        ws();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_screen, menu);
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
        //get password ref and username ref
        EditText username = (EditText) findViewById(R.id.editTxtUserName);
        EditText password = (EditText) findViewById(R.id.editTxtPassword);
        //get password and username
        String usrName = username.getText().toString();
        String pssWord = password.getText().toString();
        ws().login(usrName,pssWord);
        Intent waiting = new Intent(this, LoginWaitScreen.class);
        startActivity(waiting);
        finish();
    }
    /**
     *   @Override
    public void onClick(View v) {
    //get password ref and username ref
    EditText username = (EditText) findViewById(R.id.username);
    EditText password = (EditText) findViewById(R.id.password);
    //get password and username
    String usrName = username.getText().toString();
    String pssWord = password.getText().toString();
    //Run connection code
    int loginStatus = ws().login(usrName,pssWord);
    if (loginStatus != 2) {
    String message = "";
    switch (loginStatus){
    case 0:
    message = "Login Failed";
    break;
    case 1:
    message = "Login Success";
    break;
    case 2:
    message = "Too many login attempts: Please contact System Adminstrator";
    break;
    case 3:
    message = "An Error has occurred: Please try again later...";
    break;
    default:
    break;
    }
    Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    if(message.contentEquals("Login Success")){
    Intent home = new Intent(this,HomeActivity.class);
    startActivity(home);
    ws().changeActivity(1);
    finish();//♪♫this time baby i'll be crrrraaaaash proof♪♫
    //but no really closes the login activity so you can't login again
    }
    }
    else{
    Toast.makeText(this,"Please Contact System Administrator: Too many login attempts",Toast.LENGTH_LONG).show();
    }
    }
     */
}
