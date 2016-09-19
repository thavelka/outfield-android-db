package com.outfieldapp.outfieldbackend;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.outfieldapp.outfieldbackend.api.ApiService;
import com.outfieldapp.outfieldbackend.api.Constants;
import com.outfieldapp.outfieldbackend.database.OutfieldDatabase;

public class OutfieldApp extends Application {

    public static final String appName = "com.outfieldapp.outfield";
    private static Context appContext;
    private static ApiService apiService = ApiService.Builder.createService();
    private SharedPreferences.OnSharedPreferenceChangeListener prefsListener;

    public static Context getContext() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this.getApplicationContext();

        SharedPreferences prefs = getSharedPrefs();
        prefsListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                if (s.equals(Constants.Headers.AUTH_TOKEN)) {
                    String email = sharedPreferences.getString(Constants.Headers.EMAIL, null);
                    String token = sharedPreferences.getString(Constants.Headers.AUTH_TOKEN, null);
                    apiService = ApiService.Builder.createService(email, token);
                    sharedPreferences.unregisterOnSharedPreferenceChangeListener(prefsListener);
                }
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(prefsListener);
    }

    public static OutfieldDatabase getDatabase() {
        return OutfieldDatabase.getInstance(getContext());
    }

    public static ApiService getApiService() {
        return apiService;
    }

    public static SharedPreferences getSharedPrefs() {
        return appContext.getSharedPreferences(appName, MODE_PRIVATE);
    }


}
