package com.outfieldapp.outfieldbackend.api;

import android.util.Log;

import com.outfieldapp.outfieldbackend.OutfieldApp;
import com.outfieldapp.outfieldbackend.models.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncController {
    private static final String TAG = SyncController.class.getSimpleName();
    private static SyncController instance = new SyncController();
    public static SyncController getInstance() {
        return instance;
    }
    private SyncController() {}

    private boolean isSyncing;
    private int progress;
    private int syncTotal;
    private boolean hasTeamActivity;
    private boolean userInfoCurrent;
    private String syncToken = "";

    private List<Integer> pendingContacts = new ArrayList<>();
    private List<Integer> pendingInteractions = new ArrayList<>();
    private List<Integer> pendingComments = new ArrayList<>();
    private List<Integer> pendingImages = new ArrayList<>();


    public void doSync() {
        if (isSyncing) return;
        if (!userInfoCurrent) {
            getUserDetails();
            return;
        }
    }

    public void getUserDetails() {
        Call<User.Wrapper> call = OutfieldApp.getApiService().getUserDetails();
        call.enqueue(new Callback<User.Wrapper>() {
            @Override
            public void onResponse(Call<User.Wrapper> call, Response<User.Wrapper> response) {
                User user = response.body().getUser();
                hasTeamActivity = user.hasTeamActivity();
                if (!user.isActive()) return;
                userInfoCurrent = true;
                doSync();
            }

            @Override
            public void onFailure(Call<User.Wrapper> call, Throwable t) {
                Log.d(TAG, t.toString());
            }
        });
    }
}
