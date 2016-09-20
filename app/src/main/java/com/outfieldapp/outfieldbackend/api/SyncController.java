package com.outfieldapp.outfieldbackend.api;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.outfieldapp.outfieldbackend.OutfieldApp;
import com.outfieldapp.outfieldbackend.database.OutfieldContract;
import com.outfieldapp.outfieldbackend.models.Contact;
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

    private List<Long> pendingContacts = new ArrayList<>();
    private List<Long> pendingInteractions = new ArrayList<>();
    private List<Long> pendingComments = new ArrayList<>();
    private List<Long> pendingImages = new ArrayList<>();


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
        List<Contact> deletedContacts = new ArrayList<>();
        List<Contact> createdContacts = new ArrayList<>();
        List<Contact> updatedContacts = new ArrayList<>();
        List<Contact> favoredContacts = new ArrayList<>();

        // Get dirty contacts
        SQLiteDatabase db = OutfieldApp.getDatabase().getReadableDatabase();
        Cursor cursor = db.query(
                OutfieldContract.Contact.TABLE_NAME,
                null,
                OutfieldContract.Contact.DIRTY + "=?",
                new String[]{"1"},
                null, null, null
        );

        // Sort dirty contacts
        while (cursor != null && cursor.moveToNext()) {
            Contact contact = new Contact(cursor);
            long id = contact.getId();
            if (pendingContacts.contains(id)) {
                continue;
            }
            pendingContacts.add(id);
            if (contact.isDirty()) {
                deletedContacts.add(contact);
            } else if (id > 0 && contact.isFavored()) {
                favoredContacts.add(contact);
            } else if (id > 0) {
                updatedContacts.add(contact);
            } else if (id < 0) {
                createdContacts.add(contact);
            }
        }
        if (cursor != null) cursor.close();

        // Sync deleted contacts
        for (final Contact contact : deletedContacts) {
            Call call = OutfieldApp.getApiService().deleteContact(contact.getId());
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    pendingContacts.remove(contact.getId());
                    contact.delete();
                }

                @Override
                public void onFailure(Call call, Throwable t) {

                }
            });
        }

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
