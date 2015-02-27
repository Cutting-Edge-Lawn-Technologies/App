package com.cutting_ednge.genericapp;

import android.app.Activity;

/**
 * Created by Aaron on 11/2/2014.
 */
public class WebServiceActivity extends Activity{
    private final WebService fWebService = WebService.getInstance();

    protected WebService ws() { return fWebService; }
}
