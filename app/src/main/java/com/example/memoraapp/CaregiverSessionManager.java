package com.example.memoraapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class CaregiverSessionManager {

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
    public CaregiverSessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String id, String sname,String sphone,String saddress,String sfee,String sdob,String smail,String spassword,String sgender,String spfp){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
//php pagele variables aanu
        editor.putString("id", id);
        editor.putString("caregivername", sname);
        editor.putString("caregiverphone", sphone);
        editor.putString("caregiveraddress", saddress);
        editor.putString("caregiverfee", sfee);
        editor.putString("caregiverdob", sdob);
        editor.putString("caregivermail", smail);
        editor.putString("caregiverpass", spassword);
        editor.putString("caregivergender", sgender);
        editor.putString("caregiverpfp",spfp);






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
        user.put("caregivername",pref.getString("caregivername",null));
        user.put("caregiverphone",pref.getString("caregiverphone",null));
        user.put("caregiveraddress",pref.getString("caregiveraddress",null));
        user.put("caregiverfee",pref.getString("caregiverfee",null));
        user.put("caregiverdob",pref.getString("caregiverdob",null));
        user.put("caregivermail",pref.getString("caregivermail",null));
        user.put("caregiverpass",pref.getString("caregiverpass",null));
        user.put("caregivergender",pref.getString("caregivergender",null));
        user.put("caregiverpfp",pref.getString("caregiverpfp",null));







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



