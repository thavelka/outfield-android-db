package com.outfieldapp.outfieldbackend;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.outfieldapp.outfieldbackend.database.OutfieldDatabase;

public class OutfieldApp extends Application {

    public static final String appName = "com.outfieldapp.outfield";
    private static Context appContext;

    public static Context getContext() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this.getApplicationContext();
    }

    public static OutfieldDatabase getDatabase() {
        return OutfieldDatabase.getInstance(getContext());
    }

    public static SharedPreferences getSharedPrefs() {
        return appContext.getSharedPreferences(appName, MODE_PRIVATE);
    }
}
