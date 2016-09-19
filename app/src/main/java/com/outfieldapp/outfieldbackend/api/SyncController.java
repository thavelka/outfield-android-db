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

        isSyncing = true;
        progress = 0;
        syncTotal = 0;
        userInfoCurrent = false;

        syncCurrentUser();
    }

    private void getUserDetails() {
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

    private void syncCurrentUser() {
        final User currentUser = User.getCurrentUser();
        if (currentUser == null || !currentUser.isDirty()) return;
        Call<User.Wrapper> call = OutfieldApp.getApiService().updateUser(currentUser.wrap());
        call.enqueue(new Callback<User.Wrapper>() {
            @Override
            public void onResponse(Call<User.Wrapper> call, Response<User.Wrapper> response) {
                User user = response.body().getUser();
                if (user != null && user.getId() > 0) {
                    user.setDirty(false);
                    user.setImage(currentUser.getImage());
                    user.save();
                    Log.d(TAG, "Updated user on server");
                    syncUserImage();
                }
            }

            @Override
            public void onFailure(Call<User.Wrapper> call, Throwable t) {
                Log.e(TAG, "Failed to update user", t);
            }
        });
    }

    private void syncUserImage() {

    }

    private void syncContacts() {

    }

    private void syncInteractions() {

    }

    private void syncContactImages() {

    }

    private void syncInteractionImages() {

    }

    private void syncComments() {

    }

    private void syncNotifications() {

    }

    private void syncForms() {

    }

    private void sync() {

    }

    private void onSyncFinished() {

    }

    private void reset() {
        
    }
}
