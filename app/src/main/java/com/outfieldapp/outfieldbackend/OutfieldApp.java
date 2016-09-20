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
    private static ApiService apiService;

    public static Context getContext() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this.getApplicationContext();
        apiService = createApiService();

        SharedPreferences prefs = getSharedPrefs();
        prefs.registerOnSharedPreferenceChangeListener(
                new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                if (s.equals(Constants.Headers.AUTH_TOKEN)) {
                    apiService = createApiService();
                }
            }
        });
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

    private static ApiService createApiService() {
        String email = getSharedPrefs().getString(Constants.Headers.EMAIL, null);
        String token = getSharedPrefs().getString(Constants.Headers.AUTH_TOKEN, null);
        return ApiService.Builder.createService(email, token);
    }


}
