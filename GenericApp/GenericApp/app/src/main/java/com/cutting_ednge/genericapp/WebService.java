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

    private boolean connected = true;

    private boolean changeAct = false;
    private int activity = 0;
    private boolean getFeed = false;
    private boolean login = false;
    private boolean loginComplete = false;
    private boolean loginResults = false;
    private boolean admin = false;
    private boolean sendMessage = false;
    private String feed = "";
    private String message = "";
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
    //gets the feed when it is done
    public String getFeed(){return feed;}
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
        String SVRCMD = "#@!?Server:";
        String PAGECMD = "GOTOSCREEN:";
        String GETFEEDS = "GETFEED";
        String CLIENTCMD = "#@!?Client:";
        String MESSAGECMD = "MSSGCMD:";
        String ENDFEEDCMD ="#@!?EOF";
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
            else if(changeAct){
                changeAct = false;
                pw.println(SVRCMD+PAGECMD+activity);
                pw.flush();
            }
            //else if getting the feeds
            else if(getFeed){
                //so we don't keep getting feeds
                getFeed = false;
                //reset the feed
                feed = "";
                //tell server to get the feed
                String cmd = SVRCMD+GETFEEDS;
                pw.println(cmd);
                pw.flush();
                String line;
                try {
                    line = br.readLine();
                    if((line).equals(CLIENTCMD)) {
                        line = br.readLine();
                        boolean stillReading = !(line.equals(ENDFEEDCMD));
                        while (stillReading) {
                            System.out.println(line);
                            feed =feed + line + "\n";
                            line = br.readLine();
                            stillReading = !(line.trim().equals("#@!?EOF"));
                        }
                        listener.onTaskCompleted();
                        System.out.println("Done");
                    }
                    else{
                        System.out.println("invalid server command");
                        connListener.ConnectionFailure();
                    }
                } catch (IOException e) {
                    connListener.ConnectionFailure();
                    e.printStackTrace();
                }
            }
            else if(sendMessage){
                String cmd = SVRCMD+MESSAGECMD+message;
                System.out.println(cmd);
                pw.print(cmd);
                pw.flush();
                listener.onTaskCompleted();
                sendMessage = false;
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

    public void requestFeeds(int page) {
        /**
         * 0 = Home screen Client
         * 1 = Home screen Business
         * 2 = Messages Client
         * 3 = Message home Business
         * 4 = Messages Business
         * 5 =
         * 6 =
         * 7 =
         * 8 =
         */
        //changeActivity(page);
        getFeed = true;
    }
    public void changeActivity(int activity){
        this.activity = activity;
        changeAct=true;
    }
    public void sendMessage(String message){
        this.message = message;
        sendMessage=true;
    }
    //**********************************************************************************************
    //**********************noting below this line is currently not being used yet *****************
    //**********************************************************************************************
    public void logout() {//log that shit out
        try {
            socket.close();
            instance = null;
            listener.onTaskCompleted();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
