package com.cutting_ednge.genericapp;

import android.os.AsyncTask;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Aaron on 11/2/2014.
 */
public class WebService extends AsyncTask<String, Void, String> {
    private OnTaskCompleted listener;
    private ConnectionFailureListener connListener;
    private static WebService instance;
    private Socket socket;

    private final int PORT = 4444;
    private InputStreamReader isr;
    private PrintWriter pw;
    private BufferedReader br;
    //commands
    private final String SVRCMD = "#@!?Server:";
    private final String PAGECMD = "GOTOSCREEN:";
    private final String GETFEEDS = "GETFEED";
    private final String CLIENTCMD = "#@!?Client:";
    private final String JOBCMD = "SETJOB:";

    private boolean connected = true;

    private boolean login = false;
    private boolean loginComplete = false;
    private boolean loginResults = false;
    private boolean admin = false;
    private String USRNAME;
    private String PSSWORD;
    //on create instance, then start the thread
    private WebService() {
        this.execute("");
    }
    //if it completed the login process, then say so
    public boolean isLogedIn(){
        return loginComplete;
    }
    //if login worked, then say so
    public boolean loginWorked(){
        return loginResults;
    }
    //if they where an admin say so
    public boolean isAdmin(){
        return admin;
    }
    //adds a listener to tell when the task is done
    public void addListener(OnTaskCompleted listener) {
        this.listener = listener;
    }
    //creates a listener for if the connection failed
    public void addConnectionFailureListener(ConnectionFailureListener connListener){
        this.connListener = connListener;
    }
    //sets connected to false so that it runs the connection code
    public void connect(){connected = false;}
    //starts the login process
    public void startLoginProcess(){
        login =true;
    }


    @Override
    protected String doInBackground(String... params) {
        //todo: add a close option that sets running to false
        boolean running = true;
        //so can notify in loop thread
        Looper.prepare();
        //infinte loop because can't start thread more than once
        while(running) {
            //if it is not connected than try to connect
            if (!connected) {
                //try to connect
                try {//on create instance it tries to connect to server
                    socket = new Socket("98.223.97.52", PORT);
                    InputStream is = socket.getInputStream();
                    OutputStream os = socket.getOutputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    pw = new PrintWriter(os);
                    br = new BufferedReader(isr);
                    //if this all worked than it connected
                    connected = true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    //if it didn't then it didn't connect and we need to tell somebody
                    //no seriously if we don't tell someone we could get into trouble
                    //they might think we did it, or the app with crash
                    //or something bad will happen
                    connListener.ConnectionFailure();
                    //set connected to true so that it doesn't keep looping the failed connection
                    connected= true;
                }
            }
            //if entering the login part of the loop
            if (login) {
                //set login to false so that it doesn't keep trying to login
                login = false;
                //parsing stuffs
                String s = USRNAME + "~" + PSSWORD;
                //try to read this stuff, and find out if I got in
                try {
                    //send the stuff to the server
                    pw.println(s);
                    pw.flush();
                    //message is message from server
                    String message;
                    //read the message
                    message = br.readLine();
                    //if the message is Login Success~Admin then it was a success and an admin
                    if (message.contentEquals("Login Success~Admin")) {
                        //admin? yes
                        admin = true;
                        //login completed? yes
                        loginComplete = true;
                        //login worked? yes
                        loginResults = true;
                        //ship it
                        listener.onTaskCompleted();
                    }
                    //else if message is \|/ that then it was a client who logged in successfully
                    else if (message.contentEquals("Login Success~Client")) {
                        //admin? no
                        admin = false;
                        //login completed? yes
                        loginComplete = true;
                        //login worked? yes
                        loginResults = true;
                        //ship it
                        listener.onTaskCompleted();
                    }
                    //else login verification failed
                    else {
                        //login completed? yes
                        loginComplete = true;
                        //login worked? no
                        loginResults = false;
                        //ship it
                        listener.onTaskCompleted();
                    }
                }
                //catch the server derping, or something crazy
                catch (Exception e) {
                    e.printStackTrace();
                    //login completed? no
                    loginComplete = false;
                    //login worked? maybe, but lets go with no
                    loginResults = false;
                    //ship it
                    listener.onTaskCompleted();
                    //also ship that it derped
                    connListener.ConnectionFailure();
                }
            }
        }
        //no idea why i need this, but it is important
        return null;
    }
    //not sure why I need this either, but it is important
    public static WebService getInstance() {//if an instance does not exist make a new instance
        if (instance == null)
            instance = new WebService();
        return instance;
    }// getInstance()

    public void login(String USRNAME, String PSSWORD) {
    //login that shit
        this.USRNAME = USRNAME;
        this.PSSWORD = PSSWORD;
    }
    //**********************************************************************************************
    //************************* noting below this line is currently being used yet *****************
    //**********************************************************************************************
    public String getFeeds() {
        /**
         * 0 = Home screen Client
         * 1 = Home screen Business
         * 2 =
         * 3 =
         * 4 =
         * 5 =
         * 6 =
         * 7 =
         * 8 =
         */
        try {
            pw.println(SVRCMD + GETFEEDS);
            pw.flush();
            String feed;
            feed = br.readLine();
            if(feed.startsWith(CLIENTCMD)){
                return feed.replace(CLIENTCMD,"");
            }
            else{
                return "A server error has occurred...";
            }
        }
        catch(IOException e){
            e.printStackTrace();
            return "An error has occurred...";
        }
    }
    public void changeActivity(int activity){
        pw.println(SVRCMD+PAGECMD+activity);
        pw.flush();
    }
    public void setJob(int companyID){
        pw.println(SVRCMD+JOBCMD+companyID);
        pw.flush();
    }
    public void logout() {//log that shit out
        try {
            socket.close();
            instance = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
