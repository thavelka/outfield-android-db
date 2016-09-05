package com.outfieldapp.outfieldbackend;

import android.app.Application;
import android.content.Context;

import com.outfieldapp.outfieldbackend.database.OutfieldDatabase;

public class OutfieldApp extends Application {

    private static Context mAppContext;

    public static Context getContext() {
        return mAppContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = this.getApplicationContext();
    }

    public static OutfieldDatabase getDatabase() {
        return OutfieldDatabase.getInstance(getContext());
    }
}
