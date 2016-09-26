package com.outfieldapp.outfieldbackend;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.outfieldapp.outfieldbackend.api.Constants;
import com.outfieldapp.outfieldbackend.api.OutfieldAPI;
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

        // Set auth headers if available.
        SharedPreferences prefs = getSharedPrefs();
        String email = prefs.getString(Constants.Headers.EMAIL, null);
        String token = prefs.getString(Constants.Headers.AUTH_TOKEN, null);
        if (email != null && token != null) OutfieldAPI.setAuthHeaders(email, token);
    }

    public static OutfieldDatabase getDatabase() {
        return OutfieldDatabase.getInstance(getContext());
    }

    public static SharedPreferences getSharedPrefs() {
        return appContext.getSharedPreferences(appName, MODE_PRIVATE);
    }
}
