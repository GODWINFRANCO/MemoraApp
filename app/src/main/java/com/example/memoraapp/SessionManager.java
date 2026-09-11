package com.example.memoraapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "Login";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";


    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String id, String sname,String sphone,String saddress,String sdob,String smail,String spassword,String sgender,String spfp){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
//php pagele variables aanu
        editor.putString("id", id);
        editor.putString("patientname", sname);
        editor.putString("patientphone", sphone);
        editor.putString("patientaddress", saddress);
        editor.putString("patientdob", sdob);
        editor.putString("patientmail", smail);
        editor.putString("patientpass", spassword);
        editor.putString("patientgender", sgender);
        editor.putString("patientpfp",spfp);






        // commit changes
        editor.commit();
    }

    public boolean checkLogin(){
        // Check login status
        if(this.isLoggedIn()) {
            return true;
        } else {
            return false;

        }

    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put("id",pref.getString("id",null));
        user.put("patientname",pref.getString("patientname",null));
        user.put("patientphone",pref.getString("patientphone",null));
        user.put("patientaddress",pref.getString("patientaddress",null));
        user.put("patientdob",pref.getString("patientdob",null));
        user.put("patientmail",pref.getString("patientmail",null));
        user.put("patientpass",pref.getString("patientpass",null));
        user.put("patientgender",pref.getString("patientgender",null));
        user.put("patientpfp",pref.getString("patientpfp",null));







        // return user
        return user;

    }


    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

    }


    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}



