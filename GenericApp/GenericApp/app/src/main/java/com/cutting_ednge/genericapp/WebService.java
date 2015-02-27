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
    private WebService() {
        this.execute("");
    }
    public boolean isLogedIn(){
        return loginComplete;
    }
    public boolean loginWorked(){
        return loginResults;
    }
    public boolean isAdmin(){
        return admin;
    }
    public void addListener(OnTaskCompleted listener) {
        this.listener = listener;
    }
    public void addConnectionFailureListener(ConnectionFailureListener connListener){
        this.connListener = connListener;
    }
    public void connect(){connected = false;}
    @Override
    protected String doInBackground(String... params) {
        boolean running = true;
        //so can notify in loop thread
        Looper.prepare();
        while(running) {
            if (!connected) {
                try {//on create instance it tries to connect to server
                    socket = new Socket("98.223.97.52", PORT);
                    InputStream is = socket.getInputStream();
                    OutputStream os = socket.getOutputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    pw = new PrintWriter(os);
                    br = new BufferedReader(isr);
                    connected = true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    connListener.ConnectionFailure();
                    connected= true;
                }
            }
            if (login) {
                login = false;
                String s = USRNAME + "~" + PSSWORD;
                try {
                    pw.println(s);
                    pw.flush();
                    String message;
                    message = br.readLine();
                    System.out.println(message);
                    if (message.contentEquals("Login Success~Admin")) {
                        System.out.println("Admin");
                        admin = true;
                        loginComplete = true;
                        loginResults = true;
                        listener.onTaskCompleted();
                    } else if (message.contentEquals("Login Success~Client")) {
                        System.out.println("Client");
                        admin = false;
                        loginComplete = true;
                        loginResults = true;
                        listener.onTaskCompleted();
                    } else {
                        loginComplete = true;
                        loginResults = false;
                        listener.onTaskCompleted();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    loginComplete = false;
                    loginResults = false;
                    listener.onTaskCompleted();
                    connListener.ConnectionFailure();
                }
            }
        }
        return null;
    }

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
    public void startLoginProcess(){
        login =true;
    }
    public String getFeeds() {
        /**
         * 0 = Home screen
         * 1 = Contacts Search Screen
         * 2 = Staff Page
         * 3 = Job Search
         * 4 = Inbox
         * 5 = Job Page
         * 6 = Ticket page
         * 7 = Employee Page
         * 8 = Contacts Screen
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
