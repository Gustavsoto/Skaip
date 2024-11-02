package com.example.skaip;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    //shared prefereces klase, lai nav lieki jaraksta
    public final SharedPreferences sharedPreferences;
    public Preferences(Context context){
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }
    public boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key, false);
    }
    public String getString(String key){
        return sharedPreferences.getString(key, null);
    }
    public void putBoolean(String key, boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
    public void putString(String key, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public void clear(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
